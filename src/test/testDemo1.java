package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;


public class testDemo1 {
	public static void main(String[] args) throws IOException {
		List<String> list=new ArrayList<String>();
		List<String> list2=new ArrayList<String>();
		String str=new String("富帅哥傻逼");
		list.add(str);
		list.add("123");
		list.clear();
		list.add("1234");
		System.out.println(list.size());
	}
}
