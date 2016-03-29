package sementicAccurate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import com.sun.org.apache.xerces.internal.impl.dv.xs.FullDVFactory;

import clustering.Threshold;
import database.mongoDBManipulate;
import dynamicText.WordSimilarityNet;

public class Word {
	public static Set<String> ALLWORDS;//保存所有词语及词语出现的次数
	public static Map<String,Integer> WORDIDF;//保存所有词语及包含该词语的消息的数量
	public static List<String> FREQUENTWORD=new ArrayList<String>();
	public static Integer totalTextNum=23030;//消息总数
	static{
		mongoDBManipulate db=mongoDBManipulate.getInstance();
		try {
//			pseudoload();
			//计算词频，对词频大于0.00004的词语添加到常用词列表中，之后对常用词列表中的词计算两两之间的相似度，添加到词语相似度网络中
			ALLWORDS=db.getAllWord();
			totalTextNum=db.selectTextNum();
			WORDIDF=db.getWordIDF(ALLWORDS);
			for(Entry<String, Integer> entry:WORDIDF.entrySet()){
				if(entry.getValue()/totalTextNum>Threshold.miu){
					FREQUENTWORD.add(entry.getKey());
				}
			}
			for(String word1:FREQUENTWORD){
				for(String word2:FREQUENTWORD){
					if(word1.equals(word2))
						WordSimilarityNet.putSimilarity(word1, word2, 1.0);
					else {
						Double similarity=WordSimilarity.simWord(word1, word2);
						WordSimilarityNet.putSimilarity(word1, word2, similarity);
					}
				}
			}
			
//			
//			/**
//			 * 这部分是把数据库中查出来的map存储到txt中，加快读取速度。
//			 */
//			try{
//				String encoding="utf-8";
//				File file=new File("ALLWORDS.txt");
//				BufferedWriter fw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),encoding));
//				for(String key:ALLWORDS.keySet()){
//					fw.append(key+" "+ALLWORDS.get(key));
//					fw.newLine();
//				}
//				fw.flush();
//				fw.close();
//				File file2=new File("WORDIDF.txt");
//				BufferedWriter fw2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2,true),encoding));
//				for(String key:WORDIDF.keySet()){
//					fw2.append(""+WORDIDF.get(key));
//					fw2.newLine();
//				}
//				fw2.flush();
//				fw2.close();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Word中加载所有词语失败");
		}
	}
	
	private static void pseudoload() throws NumberFormatException, IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream("ALLWORDS.txt"), "UTF-8"));
		String str=null;
		ALLWORDS=new HashSet<String>();
		while((str=reader.readLine())!=null){
			ALLWORDS.add(str);
		}
		reader=new BufferedReader(new InputStreamReader(new FileInputStream("WORDIDF.txt"), "UTF-8"));
		WORDIDF=new HashMap<String,Integer>();
		while((str=reader.readLine())!=null){
			String[] strs=str.split(" ");
			WORDIDF.put(strs[0],Integer.parseInt(strs[1]));
		}
		reader.close();
	}
	private String word;
	private String type;
	/**
	 * 第一基本义原
	 */
	private String firstBasicSememe;
	/**
	 * 其他基本义原
	 */
	private List<String> otherBasicSememe=new ArrayList<String>();
	/**
	 * 关系义原
	 */
	private Map<String,List<String>> relationalSememe=new HashMap<String,List<String>>();
	/**
	 * 符号义原
	 */
	private Map<String,List<String>> relationalSymbolSememe=new HashMap<String,List<String>>();
	/**
	 * 虚词义原，如果本词是虚词，则这个List不为空
	 */
	private List<String> functionalWords=new ArrayList<String>();
	//词语未分割义原描述，方便查看词语的语义组成
	private String originSememes;
	public void setOriginSememes(String originSememes){
		this.originSememes=originSememes;
	}
	public String getOriginSemems(){
		return originSememes;
	}
	public void setWord(String word){
		this.word=word;
	}
	public String getWord(){
		return word;
	}
	public void setType(String type){
		this.type=type;
	}
	public String getType(){
		return type;
	}
	public boolean isFunctionalWord(){
		return !functionalWords.isEmpty();
	}
	public void setFirstBasicSememe(String firstBasicSememe){
		this.firstBasicSememe=firstBasicSememe;
	}
	public String getFirstBasicSememe(){
		return firstBasicSememe;
	}
	public void addOtherBasicSememe(String sememe){
		this.otherBasicSememe.add(sememe);
	}
	public List<String> getOtherBasicSememe(){
		return this.otherBasicSememe;
	}
	public void addRelationalSememe(String key,String value){
		List<String> list=this.relationalSememe.get(key);
		if (list == null) {
            list = new ArrayList<String>();
            list.add(value);
            relationalSememe.put(key, list);
        } else {
            list.add(value);
        }
	}
	public Map<String,List<String>> getRelationalSememe(){
		return relationalSememe;
	}
	public void addRelationalSymbolSememe(String key,String value){
        List<String> list = relationalSymbolSememe.get(key);

        if (list == null) {
            list = new ArrayList<String>();
            list.add(value);
            relationalSymbolSememe.put(key, list);
        } else {
            list.add(value);
        }
    }
	public Map<String,List<String>> getRelationalSymbolSememe(){
		return this.relationalSymbolSememe;
	}
	public void addFunctionalWords(String functionalWord){
		this.functionalWords.add(functionalWord);
	}
	public List<String> getFunctionalWords(){
		return this.functionalWords;
	}
	public static void wordInitial(){
		System.out.println("词语加载完毕");
	}
	public static void main(String[] args) throws IOException {
		System.out.println(ALLWORDS.size()+"   "+WORDIDF.size());
		Double Num=0.0;
		
		for(Integer value:WORDIDF.values()){
			Num+=value;
		}
		System.out.println(Num);
		String encoding="utf-8";
		File file=new File("cipin.txt");
		BufferedWriter fw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),encoding));
		for(Integer value:WORDIDF.values()){
			fw.append(new Double(value/(double)totalTextNum).toString());
			fw.newLine();
		}
		fw.flush();
		fw.close();
//		double part=0;
//		double beta=0;
//		while(part>0.21||part<0.19){
//			beta+=0.002;
//			int n=0;
//			for(Integer value:ALLWORDS.values()){
//				if((value/Num)>beta) 
//					n++;
//			}
//			part=n/ALLWORDS.size();
//			System.out.println("运行过程：n="+n+" beta="+beta);
//		}
//		System.out.println(beta+"   "+part);
	}
}
