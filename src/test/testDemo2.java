package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class testDemo2 {
	public static void main(String[] args) throws IOException {
		HashSet<String> set=new HashSet<String>();
		String[] files={"百度停用词列表","哈工大停用词表","四川大学机器智能实验室停用词库","中文停用词库"};
		for(int i=0;i<files.length;i++)
		{
			File file=new File("F:\\HowNet\\"+files[i]+".txt");
			InputStreamReader read=new InputStreamReader(new FileInputStream(file),"GBK");
			BufferedReader bufferedReader =new BufferedReader(read);
			String nextline=null;
			while((nextline=bufferedReader.readLine())!=null){
				set.add(nextline);
			}
			bufferedReader.close();
			read.close();
		}
		File file=new File("F:\\HowNet\\停用词整合.txt");
		OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(file),"utf-8");
		BufferedWriter bufferWriter=new BufferedWriter(writer);
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			String str=it.next();
			System.out.println(str);
			bufferWriter.write(str+"\r\n");
		}
		bufferWriter.flush();
		bufferWriter.close();
		writer.close();
	}
}
