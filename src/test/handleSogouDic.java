package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import sementicAccurate.Sememe;

public class handleSogouDic {
	public static void main(String[] args) {
		try{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                "F:\\HowNet\\搜狗实验室互联网词库\\SogouLabDic.dic"),"gbk"));
		BufferedWriter stopwriter=new BufferedWriter(new OutputStreamWriter
				(new FileOutputStream(new File("F:\\HowNet\\搜狗实验室互联网词库\\None.dic"),true),"gbk"));
		BufferedWriter dicwriter=new BufferedWriter(new OutputStreamWriter
				(new FileOutputStream(new File("F:\\HowNet\\搜狗实验室互联网词库\\myUserDic.dic"),true),"gbk"));
        String line = reader.readLine();
        while (line != null) {
        	int root=-1;
            line = line.trim().replaceAll("\\s+", " ");
            String[] str=line.split(" ");
            if(str.length<3){
            	stopwriter.append(str[0]+" NET");
            	stopwriter.newLine();
            }
            else{
            	System.out.println(line);
            	dicwriter.append(str[0]+" "+str[2].split(",")[0]);
            	dicwriter.newLine();
            }
            line=reader.readLine();
        }
        stopwriter.close();
        dicwriter.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
