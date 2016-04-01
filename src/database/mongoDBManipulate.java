package database;
import com.mongodb.util.*;

import clustering.Session;
import preHandle.RecordN;
import preHandle.transformFromQQ;
import preHandle.transformFromQQ2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.*;
public class mongoDBManipulate {
	private static mongoDBManipulate db;
	private static Mongo mongo;
	public static DB clusterdb;
	private static SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String recordCollection="chatrecord";
	String userCollection="users";
	String sessionCollection="sessions";
	private mongoDBManipulate(){
		mongo=new Mongo("localhost",27017);
		clusterdb=mongo.getDB("cluster");
	}
	//单例模式
	public static mongoDBManipulate getInstance(){
		if(db==null){
			db=new mongoDBManipulate();
			return db;
		}
		else 
			return db;
	}
	/**
	 * 插入到chatrecord中，保存的是分好词的记录
	 * @throws ParseException 
	 */
	public void insertIntoChatRecord(List<RecordN> list) throws ParseException{
		DBCollection chatrecord=clusterdb.getCollection(recordCollection);
		List<BasicDBObject> insert=new ArrayList<BasicDBObject>();
		for(RecordN record:list){
			BasicDBObject object=new BasicDBObject();
			object.put("record_id", record.record_id);
			object.put("user_number", record.user_number);
			object.put("content", record.content);
			object.put("record_time", record.record_time);
			if(record.response_to!=null&&!record.response_to.equals("")){
				object.put("response_to", record.response_to);
			}
			insert.add(object);
		}
		chatrecord.insert(insert);
	}
	/**
	 * 查询记录中出现的所有词语
	 * @return
	 */
	public Set<String> getAllWord(){
		Set<String> result=new HashSet<String>();
		DBObject command=new BasicDBObject();
		command.put("distinct", recordCollection);
		command.put("key", "content");
		CommandResult resultobj=clusterdb.command(command);
		BasicDBList values=(BasicDBList) resultobj.get("values");
		for(Object word:values){
			result.add(word.toString());
		}
		return result;
	}
	public int selectTextNum(){
		DBCollection chatrecord=clusterdb.getCollection(recordCollection);
		return (int)chatrecord.count();
	}
	/**
	 * 获取一维候选频繁词集
	 * @param allwords
	 * @return
	 */
	public Map<String,Integer> getWordIDF(Set<String> allwords){
		Map<String,Integer> result=new HashMap<String,Integer>();
		DBCollection chatrecord=clusterdb.getCollection(recordCollection);
		for(String word:allwords){
			BasicDBObject query=new BasicDBObject();
			query.put("content", word);
			long num=chatrecord.count(query);
			result.put(word, (int)num);
		}
		return result;
	}
	/**
	 * 将所有用户信息保存到数据库中
	 */
	public void insertUserName(){
		DBCollection user=clusterdb.getCollection(userCollection);
		List<BasicDBObject> list=new ArrayList<BasicDBObject>();
		for(Entry<String, Set<String>> entry:transformFromQQ2.ALLUSERNAME.entrySet()){
			BasicDBObject insert=new BasicDBObject();
			insert.put("number", entry.getKey());
			insert.put("names", entry.getValue());
			list.add(insert);
		}
//		System.out.println(list.size());
		WriteResult result=user.insert(list);
		
	}
	/**
	 * 删除chatrecord中content为空或者只包含一个空格的记录
	 */
	public void removeIllegal(){
		DBCollection chatrecord=clusterdb.getCollection(recordCollection);
		BasicDBObject query=new BasicDBObject();
		BasicDBObject size=new BasicDBObject();
		size.put("$size", 0);
		query.put("content", size);
		chatrecord.remove(query);
		List<String> list=new ArrayList<String>();
		list.add("");
		chatrecord.remove(new BasicDBObject("content", list));
		list.clear();
		list.add(" ");
		chatrecord.remove(new BasicDBObject("content", list));
	}
	/**
	 * 读取所有的用户信息
	 * @return
	 */
	public Map<String,Set<String>> getAllUser(){
		Map<String,Set<String>> result=new HashMap<String,Set<String>>();
		DBCollection users=clusterdb.getCollection(userCollection);
		DBCursor cursor=users.find();
		while(cursor.hasNext()){
			DBObject record=cursor.next();
			String number=(String) record.get("number");
			BasicDBList names=(BasicDBList) record.get("names");
			Set<String> set=new HashSet<String>();
			for(Object name:names){
				set.add((String)name);
			}
			result.put(number, set);
		}
		return result;
	}
	public DBCursor selectChatRecordN(){
		DBCollection chatrecord=clusterdb.getCollection(recordCollection);
		return chatrecord.find();
	}
	public void saveSession(){
		DBCollection session=clusterdb.getCollection(sessionCollection);
		List<BasicDBObject> insert=new ArrayList<BasicDBObject>();
		for(Entry<Integer, Session> entry:Session.ALLSESSIONS.entrySet()){
			BasicDBObject object=new BasicDBObject();
			object.put("id", entry.getKey());
			List<String> content=new ArrayList<String>();
			Map<String,Integer> originalMap=entry.getValue().getOriginalMap();
			for(String key:originalMap.keySet()){
				for(int i=0;i<originalMap.get(key);i++){
					content.add(key);
				}
			}
			object.put("content", content);
			insert.add(object);
		}
		WriteResult result=session.insert(insert);
		clusterdb.getWriteConcern().callGetLastError();
		System.out.println();
	}
	public int getWordFrequency(Collection<String> list){
		DBCollection sessions=clusterdb.getCollection(sessionCollection);
		BasicDBObject simbolall=new BasicDBObject();
		simbolall.put("$all", list);
		BasicDBObject query=new BasicDBObject();
		query.put("content", simbolall);
		DBCursor cursor=sessions.find(query);
		return cursor.count();
	}
	public static void main(String[] args) throws ParseException {
	}
}
