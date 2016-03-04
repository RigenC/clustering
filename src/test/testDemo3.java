package test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.comparators.ComparableComparator;

import database.DBmanipulate;
public class testDemo3 {
	public static void main(String[] args) throws ParseException {
//		Map<demo, Double> map=new HashMap<demo,Double>();
//		demo d1=new demo("中国","美国");
//		demo d2=new demo("美国","中国");
//		System.out.println(d1.equals(d2));
//		map.put(d1, 2.0);
//		System.out.println(map.containsKey(d2));
////		map.put(d2, 1.0);
//		System.out.println(map.get(d2));
//		List<String> list=new ArrayList<String>();
//		list.add("中国");
//		System.out.println(list.remove("中国")+" "+list.size());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date datetime1=sdf.parse("2015-2-27 02:24:14");
		Date datetime2=sdf.parse("2015-2-27 14:24:14");
		System.out.println((datetime2.getTime()-datetime1.getTime())>=43200000);
	}
}
class demo{
	public String word1;
	public String word2;
	public demo(String word1,String word2){
		this.word1=word1;
		this.word2=word2;
	}
	public int hashCode(){
		return word1.hashCode()+word2.hashCode();
	}
	public boolean equals(Object e){
		if(demo.class.isInstance(e)){
			demo d1=(demo)e;
			if(word1.equals(d1.word1)&&word2.equals(d1.word2)){
				return true;
			}
			else if(word1.equals(d1.word2)&&word2.equals(d1.word1)){
				return true;
			}
			else
				return false;
		}
		else return false;
	}
}