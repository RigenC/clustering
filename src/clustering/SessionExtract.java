package clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import database.DBmanipulate;
import dynamicText.DynamicTextVector;
import dynamicText.TextVector;
import preHandle.transformFromQQ;
import sementicAccurate.Word;

public class SessionExtract {
	/**
	 * 本类为会话抽取的流程类，运行流程为：ExecuteExtract（进行会话抽取运算）——ExecuteCreateOriginalMap（生成会话的原始向量）——ExecuteCreateTFIDFMap（生成会话的TFIDF权重向量）
	 */
//	public final static double gama=0.3;
	public final static int maxSessionNum=4;
	public static void ExecuteExtract() throws SQLException, ParseException {
		Word.wordInitial();
		DBmanipulate db=DBmanipulate.getInstance();
		transformFromQQ.ALLUSERNAME=db.getAllUser();
		ResultSet rs=db.selectChatRecordN();
		TextVector lastvector = null;
		while(rs.next()){
			int id=rs.getInt("id");
			String content=rs.getString("content");
			String user_number=rs.getString("user_number");
			String record_time=rs.getString("record_time");
			String response_to=rs.getString("response_to");
			String response_to_number=null;
			if(response_to!=null){
				for(Entry<String, Set<String>> entry:transformFromQQ.ALLUSERNAME.entrySet()){
					if(entry.getValue().contains(response_to)){
						response_to_number=entry.getKey();
						break;
					}
				}
			}
			TextVector vector=new TextVector(id, content, record_time, user_number,response_to_number);
			//初始情况下，ALLSESSION为空，新建一个Session包含第一个消息
			if(Session.ALLSESSIONS.size()==0){
				Session session=new Session();
				session.addVector(vector);
				Session.ALLSESSIONS.put(session.getId(), session);
				System.out.println("新建会话:"+session.getId()+" 添加消息"+id);
				lastvector=vector;
				continue;
			}
			//如果连续两条消息的时间间隔小于minduration并且是同一个人发的，则认为两条消息应该是一条消息
			if(lastvector!=null&&vector.getDatetime().getTime()-lastvector.getDatetime().getTime()<Threshold.minduration
					&&vector.getUser_number().equals(lastvector.getUser_number())){
				lastvector.getSessionBelong2().addVector(vector);
				System.out.println("消息"+vector.getId()+"添加到会话"+lastvector.getSessionBelong2().getId()+"中， 连续两条同一人");
				lastvector=vector;
				continue;
			}
			//消息中存在@对象时，将艾特消息划分到被艾特的消息会话中
			if(response_to_number!=null){
				TextVector target=TextVector.getClosestVector(response_to_number, record_time);
				target.getSessionBelong2().addVector(vector);
				System.out.println("消息"+vector.getId()+"添加到会话"+target.getSessionBelong2().getId()+"中， 存在艾特");
				lastvector=vector;
				continue;
			}
			double sessionMax1=0;
			double sessionMax2=0;
			Session belong1=null;
			Session belong2=null;
			List<Session> lastestSessions=Session.getLastestSessions(vector);
			String sessionsnum="";
			for(Session s:lastestSessions){
				sessionsnum+=s.getId()+" ";
			}
			System.out.println("消息"+id+"的最新会话为："+sessionsnum);
			for(Session session:lastestSessions){
					double similarity=session.getMaxSimilarity(vector);
					if((vector.getDatetime().getTime()-session.getLatestTime().getTime())<Threshold.minduration&&similarity>Threshold.lgama){
						if(similarity>sessionMax1){
							belong1=session;
							sessionMax1=similarity;
						}
					}
					if(similarity>sessionMax2&&similarity>Threshold.gama){
						sessionMax2=similarity;
						belong2=session;
					}
			}
			if(belong1!=null){
				belong1.addVector(vector);
				lastvector=vector;
				continue;
			}
			else if(belong2==null||sessionMax2==0){
				Session session=new Session();
				session.addVector(vector);
				Session.ALLSESSIONS.put(session.getId(), session);
				System.out.println("新建会话"+session.getId()+" 添加消息"+id);
				lastvector=vector;
				continue;
			}
			else{
				belong2.addVector(vector);
				System.out.println("消息"+id+"添加到会话"+belong2.getId()+"中");
			}
			lastvector=vector;
//			//计算belong2会话中的所有消息与vector的相似度的最大值
//			double vectorMax=0;//与该vector的相似度最大值
//			TextVector parentVector=null;//与该vector相似度最大的消息向量
//			Session parentVectorSession=belong2;//与该vector相似度最大的消息向量所在的会话
//			for(Integer vectorId:belong2.vectors){
//				Map<String,Double> map=TextVector.ALLTEXTVECTORS.get(vectorId).getTfidfVector();
//				double similarity=DynamicTextVector.getDynamicSimilarity(map, vector.getTfidfVector());
//				if(similarity>vectorMax){
//					vectorMax=similarity;
//					parentVector=TextVector.ALLTEXTVECTORS.get(vectorId);
//				}
//			}
//			
//			//计算另外三个会话中的所有消息与该vector的相似度。若大于vectorMax，移动新的消息向量
//			for(Session session:queue){
//				if(session.equals(belong2))
//					continue;
//				else{
//					for(Integer vectorId:session.vectors){
//						Map<String,Double> map=TextVector.ALLTEXTVECTORS.get(vectorId).getTfidfVector();
//						double similarity=DynamicTextVector.getDynamicSimilarity(map, vector.getTfidfVector());
//						//相似度大于最大相似度，且时间差不大于12h
//						if(similarity>vectorMax&&TextVector.getTimeDiff(vector.getDatetime(), TextVector.ALLTEXTVECTORS.get(vectorId).getDatetime())<12){
//							vectorMax=similarity;
//							parentVector=TextVector.ALLTEXTVECTORS.get(vectorId);
//							parentVectorSession=session;
//						}
//					}
//				}
//			}
//			//如果与该vector相似度最大的向量不在belong2中，那么将该向量及后续向量移动到belong2中
//			if(!parentVectorSession.equals(belong2)){
//				belong2.addVector(parentVector);
//				parentVectorSession.removeVector(parentVector);
//			}
		}
	}
	/**
	 * 遍历ALLSESSIONS，对每个Session都创建原始向量
	 */
	public static void ExecuteCreateOriginalMap(){
		for(Session session:Session.ALLSESSIONS.values()){
			session.createOriginalMap();
		}
	}
	/**
	 * 根据所有Session更新WORD中的WORDIDF和totalTextNum，然后给每个Session创建TFIDF向量
	 */
	public static void ExecuteCreateIFTDFMap(){
		//首先计算所有词语的IDF部分，计算所有词语的支持度（包含该词语的会话的数量）
		Word.WORDIDF.clear();
		for(String word:Word.ALLWORDS.keySet()){
			int Num=0;
			for(Session session:Session.ALLSESSIONS.values()){
				if(session.getOriginalMap().containsKey(word)){
					Num++;
				}
			}
			Word.WORDIDF.put(word, Num);
		}
		Word.totalTextNum=Session.ALLSESSIONS.size();
		for(Session session:Session.ALLSESSIONS.values()){
			session.createTIDFMap();
		}
	}
	/**
	 * 测试用，之前抽取的会话保存到了本地文件中，直接读取，减少运行等待时间
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void loadSessions() throws NumberFormatException, IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("Session.txt"), "UTF-8"));
		String str=null;
		while((str=reader.readLine())!=null){
			String[] strs=str.split(" \\[");
			int id=Integer.parseInt(strs[0]);
			Session session=new Session();
			session.setId(id);
			String vector=strs[1].substring(0, strs[1].length()-1);
			String[] vectors=vector.split(",");
			for(String s:vectors){
				System.out.println(s.trim());
				session.vectors.add(Integer.parseInt(s.trim()));
			}
			Session.ALLSESSIONS.put(id, session);
		}
	}
	public static void main(String[] args) throws SQLException, ParseException, IOException{
		/**
		 * 以下部分是会话抽取算法，结果保存在Session.txt中
		 */
//		Long starttime=System.currentTimeMillis();
//		ExecuteExtract();
//		System.out.println(Session.ALLSESSIONS.size());
//		String encoding="utf-8";
//		File file=new File("Session.txt");
//		BufferedWriter fw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),encoding));
//		for(Session session:Session.ALLSESSIONS.values()){
////			System.out.println("会话"+session.getId()+" : "+session.vectors.size());
//			fw.append(session.getId()+" "+session.vectors.toString());
//			fw.newLine();
//		}
//		fw.close();
//		Long endtime=System.currentTimeMillis();
//		double trans=60000.0;
//		System.out.println("运行时间为："+(endtime-starttime)/trans+"分钟");
		/**
//		 * 以下部分是读取文件中的Session，计算其会话向量并保存到数据库中
//		 */
		DBmanipulate db=DBmanipulate.getInstance();
		ResultSet rs=db.selectChatRecordN();
		while(rs.next()){
			int id=rs.getInt("id");
			String content=rs.getString("content");
			String user_number=rs.getString("user_number");
			String record_time=rs.getString("record_time");
			String response_to=rs.getString("response_to");
			TextVector vector=new TextVector(id, content, record_time, user_number,response_to);
		}
		loadSessions();
		ExecuteCreateOriginalMap();
//		ExecuteCreateIFTDFMap();
		for(Entry<Integer, Session> entry:Session.ALLSESSIONS.entrySet()){
			int id=entry.getKey();
			Map<String,Integer> originalMap=entry.getValue().getOriginalMap();
			List<String> content=new ArrayList<String>();
			for(String key:originalMap.keySet()){
				for(int i=0;i<originalMap.get(key);i++){
					content.add(key);
				}
			}
			db.saveSession(id, content.toString());
		}
		System.out.println(Session.ALLSESSIONS.size());
	}
}
