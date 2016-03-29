package test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.comparators.ComparableComparator;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.*;
import database.DBmanipulate;
public class testDemo3 {
	public static void main(String[] args) throws ParseException {
		Set<String> st=new TreeSet<String>();
		st.add("老师");st.add("合同");st.add("物资");st.add("订单");
		Set<String> st2=new TreeSet<String>();
		st2.add("物资");st2.add("合同");st2.add("老师");st2.add("股票");
		System.out.println(st.toString());
		System.out.println(st2.toString());
		Set<Set<String>> bigset=new TreeSet<Set<String>>();
		bigset.add(st2);bigset.add(st);
		System.out.println(bigset.toString());
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