package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

import clustering.Session;
import preHandle.RecordN;
import preHandle.SplitWord;
import preHandle.transformFromQQ;
@Deprecated //富帅哥是撒币！
public class DBmanipulate {
	String driver="com.mysql.jdbc.Driver";
	String url="jdbc:mysql://127.0.0.1:3306/sql_test";
	String username="root";
	String password="admin";
	private static DBmanipulate db;
	private Connection con;
	private DBmanipulate(){
		try{
			Class.forName(driver);
			con=DriverManager.getConnection(url, username, password);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 查询chatrecord中的所有记录
	 * @return ResultSet
	 * @throws Exception
	 */
	public ResultSet selectAll() throws Exception{
		Statement statement=con.createStatement();
		ResultSet rs=statement.executeQuery("select id,content,user_number,record_time,session_id,response_to from chatrecord");
		return rs;
	}
	/**
	 * 查询chatrecordn中的所有记录
	 * @return
	 * @throws SQLException
	 */
	public ResultSet selectChatRecordN() throws SQLException{
		Statement statement=con.createStatement();
		ResultSet rs=statement.executeQuery("select id,content,user_number,record_time,response_to from chatrecordn");
		return rs;
	}
	/**
	 * 查询chatrecordn的记录数量，用在计算idf中
	 * @return
	 * @throws SQLException
	 */
	public int selectTextNum() throws SQLException{
		Statement statement =con.createStatement();
		ResultSet rs=statement.executeQuery("select count(*) from chatrecordn");
		rs.next();
		return rs.getInt(1);
	}
	//原来的方法
	public boolean insertGroup(String groupname, int grouptype) throws SQLException{
		boolean result=true;
		Statement statement=null;
		
		statement=con.createStatement();
		String sql="insert into chat_group(group_name,group_type) value('"+groupname+"',"+grouptype+")";
		result=statement.execute(sql);
		statement.close();
		return result;
	}
	/**
	 * 删除chatrecord中无意义的内容，包括''
	 * @return
	 */
	public boolean removeIllegal(){
		try{
			Statement statement=con.createStatement();
			String sql="delete from chatrecord where content=\'\'";
			statement.executeUpdate(sql);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	public Map<String,Set<String>> getAllUser(){
		Map<String,Set<String>> result=new HashMap<String,Set<String>>();
		try {
			Statement statement=con.createStatement();
			String sql="select user_number,user_name from user";
			ResultSet rs=statement.executeQuery(sql);
			while(rs.next()){
				String number=rs.getString("user_number");
				String names=rs.getString("user_name");
				String[] str=names.split(",");
				Set<String> list=new HashSet<String>();
				for(String s:str){
					list.add(s.substring(1,s.length()-1));
				}
				result.put(number, list);
				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	/**
	 * 将分词后的聊天内容插入的到chatrecordN中，拼凑成一整句sql语句后再执行插入
	 * @param list为分词后的记录内容
	 * @return
	 */
	public boolean insertRecordN(List<RecordN> list){
		boolean result=true;
		try{
		Statement statement=con.createStatement();
		//StringBuilder sql=new StringBuilder("insert into chatrecordN(content,user_number,record_time,session_id) values");
		Iterator<RecordN> it=list.iterator();
		while(it.hasNext()){
			RecordN rn=it.next();
			String sql;
			if(rn.response_to==null){
				sql="insert into chatrecordN(content,user_number,record_time,session_id) value("
						+ "'"+rn.content+"','"+rn.user_number+"','"+rn.record_time+"',"+rn.session_id+")";
			}
			else{
				sql="insert into chatrecordN(content,user_number,record_time,session_id,response_to) value("
						+ "'"+rn.content+"','"+rn.user_number+"','"+rn.record_time+"',"+rn.session_id+",'"+rn.response_to+"')";
			}
			System.out.println(sql);
			statement.executeUpdate(sql);
		}
		}catch(SQLException e){
			e.printStackTrace();
			result=false;
			return result;
		}
		return result;
	}
	/**
	 * 将从文本文件中读取到的内容插入到数据库中，每读取一条即插入一次
	 * @param content 内容String
	 * @param date 时间数据库中为datetime，java中为String
	 * @param number 用户QQ号
	 * @return 插入成功返回true
	 * @throws SQLException
	 */
	public boolean insertShortText(String content,String date, String number,String session_id,String responseTo) throws SQLException{
		boolean result=true;
		
		Statement statement=null;
		statement=con.createStatement();
		String sql;
		if(responseTo==null)
			sql="insert into chatrecord(content,record_time,user_number,session_id) value('"+content+"','"+date+"','"+number+"',"+session_id+")";
		else
			sql="insert into chatrecord(content,record_time,user_number,session_id,response_to) value('"+content+"','"+date+"','"+number+"',"+session_id+",'"+responseTo+"')";
		System.out.println(sql);
		result=statement.execute(sql);
		statement.close();
		return result;
	}
	/**
	 * 将素有的用户昵称保存到数据库中
	 * @throws SQLException
	 */
	public void insertUserName() throws SQLException{
		Statement statement=con.createStatement();
		StringBuilder sql=new StringBuilder();
		sql.append("insert into user(user_number,user_name) values");
		for(Entry<String, Set<String>> entry:transformFromQQ.ALLUSERNAME.entrySet()){
			sql.append("('"+entry.getKey()+"','"+entry.getValue().toString()+"'),");
		}
		sql.deleteCharAt(sql.length()-1);
		statement.execute(sql.toString());
	}
	public static void main(String[] args) throws Exception {
		ResultSet rs=DBmanipulate.getInstance().selectAll();
		while(rs.next()){
			System.out.println(rs.getString("response_to"));
		}
	}
	public static DBmanipulate getInstance(){
		if(db!=null)
			return db;
		else{
			synchronized(DBmanipulate.class){
				if(db==null){
					db=new DBmanipulate();
					return db;
				}
			}
		}
		return db;
	}
	/**
	 * 获取chatrecordn中的所有词语
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String,Integer> getAllWord() throws SQLException{
		HashMap<String,Integer> resultSet=new HashMap<String,Integer>();
		Statement statement=con.createStatement();
		String sql="select content from chatrecordN";
		ResultSet result=statement.executeQuery(sql);
		while(result.next()){
			String content=result.getString("content");
			content=content.substring(1, content.length()-1);
			String[] words=content.split(",");
			for(int i=0;i<words.length;i++){
				String word=words[i].trim();
				if(resultSet.containsKey(word))
					resultSet.put(word,resultSet.get(word)+1);
				else
					resultSet.put(word, 1);
			}
		}
		return resultSet;
	}
	/**
	 * 将所有的Session暂时保存
	 * @throws SQLException 
	 */
	public void saveSession(int id,String content) throws SQLException{
		Statement statement=con.createStatement();
		String sql="insert into session(id,content) value("+id+",'"+content+"')";
		System.out.println(sql);
		statement.execute(sql);
	}
	public HashMap<String,Integer> getWordIDF(Map<String,Integer> allwords) throws SQLException{
		HashMap<String,Integer> resultSet=new HashMap<String,Integer>();
		Statement statement=con.createStatement();
		Iterator<Entry<String,Integer>> entries=allwords.entrySet().iterator();
		long start=System.currentTimeMillis();
		while(entries.hasNext()){
			Entry<String, Integer> entry=entries.next();
			String word=entry.getKey();
			String sql="select count(*) from chatrecordN where content like \'%"+word+"%\'";
			ResultSet rs=statement.executeQuery(sql);
			if(rs.next()){
				int number=rs.getInt(1);
				resultSet.put(word, number);
			}
			//这种的运行时间太长，约是上面的两倍
//			String sql="select content from chatrecordn";
//			ResultSet rs=statement.executeQuery(sql);
//			int num=0;
//			while(rs.next()){
//				String content=rs.getString("content");
//				if(content.contains(word))
//					num++;
//			}
//			resultSet.put(word, num);
		}
		System.out.println(System.currentTimeMillis()-start);
		return resultSet;
	}
}
