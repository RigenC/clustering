package dynamicText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.BasicDBList;

import clustering.Session;
import sementicAccurate.Word;

public class TextVector {
	public static Map<Integer,TextVector> ALLTEXTVECTORS=new HashMap<Integer,TextVector>();
	private int id;
	private double max;
	private String user_number;
	private Date datetime;
	private String response_to_number;
	//	private List<Integer> childlIdList=new ArrayList<Integer>();
	private Map<String,Integer> originalWords=new HashMap<String,Integer>();
	private Map<String,Double> tfidfVector=new HashMap<String,Double>();
	@SuppressWarnings("deprecation")
	/**
	 * 创建TextVector的构造函数，在创建对象之前必须初始化Word。在创建Vector的同时也将该Vector保存到静态的Map中
	 * @param id
	 * @param content
	 * @param recordtime
	 * @param user_number
	 * @throws ParseException
	 */
	public TextVector(int id,BasicDBList content,Date recordtime,String user_number,String response_to_number) throws ParseException{
		this.id=id;
		this.user_number=user_number;
		this.response_to_number=response_to_number;
		this.datetime=recordtime;
		for(Object object:content){
			String word=(String)object;
			if(originalWords.containsKey((String)word)){
				originalWords.put(word, originalWords.get(word));
			}
			else {
				originalWords.put(word, 1);
			}
		}
		accurateTfidfVector();
		ALLTEXTVECTORS.put(id, this);
	}

	public String getResponse_to_number() {
		return response_to_number;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public int getId() {
		return id;
	}
	public String getUser_number() {
		return user_number;
	}
	public void setUser_number(String user_number) {
		this.user_number = user_number;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public Map<String, Integer> getWords() {
		return originalWords;
	}
	public void addWords(String word){
		if(originalWords.containsKey(word)){
			originalWords.replace(word, originalWords.get(word)+1);
		}
		else originalWords.put(word, 1);
	}
	public Map<String, Double> getTfidfVector() {
		return tfidfVector;
	}
	public Session getSessionBelong2(){
		if(Session.ALLSESSIONS.isEmpty())
			return null;
		for(Session s:Session.ALLSESSIONS.values()){
			if(s.vectors.contains(this.id))
				return s;
		}
		return null;
	}
	/**
	 * 生成消息的原始向量后，计算消息的TFIDF权重向量
	 */
	public void accurateTfidfVector(){
		double totalWordNum=0;
		for(Integer value:originalWords.values())
			totalWordNum+=value;
		for(String key:originalWords.keySet()){
			Integer value=originalWords.get(key);
			double tf=value/totalWordNum;
			double idf=Math.log(Word.totalTextNum/(double)Word.WORDIDF.get(key)+0.01);
			tfidfVector.put(key, tf*idf);
		}
		double der=0;
		for(Double value:tfidfVector.values()){
			der+=value*value;
		}
		der=Math.sqrt(der);
		for(Entry<String,Double> entry:tfidfVector.entrySet()){
			tfidfVector.put(entry.getKey(), entry.getValue()/der);
		}
//		originalWords.clear();
	}
	/**
	 * 根据用户名和时间获取它前面离它最近的该用户的消息
	 * @param usernumber 用户名，被艾特的人
	 * @param date 该消息的时间
	 * @return
	 * @throws ParseException
	 */
	public static TextVector getClosestVector(String usernumber,Date datestr) throws ParseException{
		SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date recent=format.parse("1970-01-01 00:00:00");
		Date date=datestr;
		TextVector result=null;
		for(TextVector vector:ALLTEXTVECTORS.values()){
			if(vector.getUser_number().equals(usernumber)
					&&vector.getDatetime().before(date)&&vector.getDatetime().after(recent)){
				recent=vector.getDatetime();
				result=vector;
			}
		}
		return result;
	}
	public static void main(String[] args) throws ParseException {
		Word.wordInitial();

		
	}
	/**
	 * 与Child相关的方法和属性是第一版本的会话抽取算法的需求，第二版本中取消
	 * @return
	 */
//	public void addChild(int id){
//		this.childlIdList.add(id);
//	}
//	public void removeChild(int id){
//		if(this.childlIdList.contains(id)){
//			childlIdList.remove(new Integer(id));
//		}
//	}
//	public List<Integer> getAllChildId(){
//		List<Integer> result=new ArrayList<Integer>();
//		result.add(id);
//		for(Integer id:childlIdList){
//			result.add(id);
//			result.addAll(ALLTEXTVECTORS.get(id).getAllChildId());
//		}
//		return result;
//	}
}
