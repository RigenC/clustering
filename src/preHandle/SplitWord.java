package preHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import database.DBmanipulate;

public class SplitWord {
	public static HashSet<String> stopwordset=new HashSet<String>();
	static{
		try{
			File file=new File("dict\\停用词.txt");
			InputStreamReader read=new InputStreamReader(new FileInputStream(file),"utf-8");
			BufferedReader bufferedReader =new BufferedReader(read);
			String nextline=null;
			while((nextline=bufferedReader.readLine())!=null){
				stopwordset.add(nextline);
			}
			bufferedReader.close();
			read.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static List<String> ripeStopWord(List<String> afterSplit){
		List<String> afterripe=new ArrayList<String>();
		for(String word:afterSplit){
			if(word.equals(""))
				continue;
			if(word.equals(" "))
				continue;
			if(stopwordset.contains(word)){
				continue;
			}
			afterripe.add(word);
		}
		return afterripe;
	}
	static void loadStopWords() throws IOException{
		File file=new File("dict\\停用词.txt");
		InputStreamReader read=new InputStreamReader(new FileInputStream(file),"utf-8");
		BufferedReader bufferedReader =new BufferedReader(read);
		String nextline=null;
		while((nextline=bufferedReader.readLine())!=null){
			stopwordset.add(nextline);
		}
		bufferedReader.close();
		read.close();
	}
}
