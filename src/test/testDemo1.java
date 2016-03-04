package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testDemo1 {
	public static void main(String[] args) {
		String pattern="^\\d{4}-[0-1]\\d-[0-3]\\d \\d{1,2}:[0-6]\\d:[0-6]\\d .*\\(\\d{1,13}\\)$";
		String timestamppattern="\\d{4}-[0-1]\\d-[0-3]\\d [0-2]\\d:[0-6]\\d:[0-6]\\d ";
		String test="    9  own|有                                 8";
		test = test.trim().replaceAll("\\s+", " ");
		String[] strs = test.split(" ");
		System.out.println(test);
		for(int i=0;i<strs.length;i++)
        System.out.println(strs[i]);
//		String str="neiro'ng\\";
//		System.out.println(str.replaceAll("\\\\", ""));
//		Pattern p=Pattern.compile("^\\(\\d{4,12}\\)$");
//		Matcher mtime=p.matcher("2016-01-28 12:26:20 会计（叫我小温）(781902195)");
		System.out.println(Pattern.matches(pattern, "2015-12-15 9:47:30 财智东方-王涛(1737862228)"));
//		System.out.println(Pattern.matches(pattern, "2016-01-28 12:26:20 会计（叫我小温）(781902195)"));
		int count=0;
		int totalcount=0;
		int errorcount=0;
//		boolean result=Pattern.matches(pattern, "2015-10-23 13:14:39 (756257469)");
//		System.out.println(result);
//		String lastline="求职-上邪(756257469)";
//		String[] result=lastline.split("\\(");
		/*
		try{
			File file=new File("C:\\Users\\administrater\\Desktop\\四大国际会计师事务所.txt");
			if(file.isFile()&&file.exists()){
				InputStreamReader read=new InputStreamReader(new FileInputStream(file), "utf-8");
				BufferedReader bufferedReader =new BufferedReader(read);
				String nextline=null;
				StringBuffer content=new StringBuffer();
				String title=null;
				String lastline=null;
				while((nextline=bufferedReader.readLine())!=null){
					totalcount++;
					if(lastline!=null&&lastline.equals("================================================================")
							&nextline.contains("消息对象:"))
					{
						System.out.println(nextline.split(":")[1]);
					}
					else if(title==null&&Pattern.matches(pattern, nextline)){
						title=nextline;
						continue;
					}
					else if(title!=null&&!Pattern.matches(pattern, nextline)){
						content.append(nextline);
					}
					else if (title!=null&&Pattern.matches(pattern, title)&&Pattern.matches(pattern, nextline))
					{
						count++;
						try{
							Pattern ptime=Pattern.compile(timestamppattern);
							Pattern pnumber=Pattern.compile("\\(\\d{5,12}\\)");
							Matcher mtime=ptime.matcher(title);
							Matcher mnumber=pnumber.matcher(title);
							String date=null;
							mtime.find();
							date=mtime.group();
							String number=null;
							while(mnumber.find()){
								number=mnumber.group();
							}
							number=number.replace("(", "");
							number=number.replace(")", "");
							System.out.println(date+"_"+number+"  "+content);
							title=nextline;
							content.delete(0, content.length());
							
						}catch(java.util.regex.PatternSyntaxException e)
						{e.printStackTrace();
						errorcount++;continue;}
					}
					lastline=nextline;
				}
				System.out.println(totalcount+"  "+count+"   "+errorcount);
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}
		*/
		
	}
}
