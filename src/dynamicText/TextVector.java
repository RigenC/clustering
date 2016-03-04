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

import sementicAccurate.Word;

public class TextVector {
	public static Map<Integer,TextVector> ALLTEXTVECTORS=new HashMap<Integer,TextVector>();
	private int id;
	private double max;
	private String user_number;
	private Date datetime;
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
	public TextVector(int id,String content,String recordtime,String user_number) throws ParseException{
		this.id=id;
		this.user_number=user_number;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.datetime=sdf.parse(recordtime);
		content=content.substring(1,content.length()-1);
		String[] str=content.split(",");
		for(int i=0;i<str.length;i++){
			if(originalWords.containsKey(str[i])){
				originalWords.put(str[i].trim(), originalWords.get(str[i])+1);
			}
			else {
				originalWords.put(str[i].trim(), 1);
			}
		}
		accurateTfidfVector();
		ALLTEXTVECTORS.put(id, this);
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
	 * 计算两个消息向量之间的时间差，单位为h
	 * @param time1 第一个时间
	 * @param time2 第二个时间
	 * @return
	 */
	public static int getTimeDiff(Date time1,Date time2){
		long diffTime=Math.abs(time1.getTime()-time2.getTime());
		return (int) (diffTime/3600000);
	}
	public static void main(String[] args) throws ParseException {
		Word.wordInitial();
		TextVector vector=new TextVector(1,"[老师, 采购, 物资, 合同, 订单, 入账, 分, 录, 做]", "2015-10-23 13:14:39", "756257469");

		
	}
}
