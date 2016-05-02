package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import database.mongoDBManipulate;
import kevin.zhang.NLPIR;

import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;


public class testDemo1 {
	public static void main(String[] args) throws IOException {
		NLPIR nlpir = new NLPIR();  
	     // NLPIR_Init方法第二个参数设置0表示编码为GBK, 1表示UTF8编码(此处结论不够权威)  
	    if (!NLPIR.NLPIR_Init("./file/".getBytes("utf-8"), 1)) {  
	        System.out.println("NLPIR初始化失败...");  
	        return;  
	    }
	    String[] str={"报道", "新人", "公司", "公司", "月", "大型", "招聘", "招聘", "公益", "第一", "慢慢来", "公布", "指点", "成都", "模", "考试", "群", "日", "次日", "财会", "机会", "价值", "朋友", "书籍", "新", "甘肃省", "上市", "感兴趣", "答案", "财务经理", "指教" };
	    StringBuilder sb=new StringBuilder();
	    for(int i=0;i<str.length;i++){
	    	if(i!=str.length-1)
	    		sb.append(str[i]);
	    	else 
	    		sb.append(str[i]);
	    }
	    System.out.println(sb.toString());
	    byte[] b=nlpir.NLPIR_GetKeyWords(sb.toString().getBytes(), 50, false);
	    System.out.println(new String(b));
	}
}
