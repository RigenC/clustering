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
		String s="123@小财务（小湘)";
		String str="@小财务（小湘)";
		if(str.contains("(")||str.contains(")")){
			str=str.replaceAll("\\(", "\\\\(");
			str=str.replaceAll("\\)", "\\\\)");
		}
		System.out.println(s.replaceAll(str, ""));
	}
}
