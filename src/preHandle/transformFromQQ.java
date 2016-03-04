package preHandle;

import java.io.*;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.DBmanipulate;;

public class transformFromQQ {
	private static String pattern="\\d{4}-[0-1]\\d-[0-3]\\d \\d{1,2}:[0-6]\\d:[0-6]\\d .*\\(\\d{1,13}\\)";
	private static String timestamppattern="\\d{4}-[0-1]\\d-[0-3]\\d \\d{1,2}:[0-6]\\d:[0-6]\\d ";
	public static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
	transformFromQQ(){
		
	}
	public static void main(String[] args) {
//		System.out.println(ripeBiaodian("今天我，终于站在这年轻的“战场”；、"));
		Transform("C:\\Users\\蔡立坤\\Desktop\\四大国际会计师事务所.txt");
	}
	private static String ripeBiaodian(String str){
		str=str.replaceAll("[表情]", "");
		str=str.replaceAll("[图片]", "");
		String Biaodian="[,./'\";:><?\\|\\[\\]~!@#$%^&*()_+-=，。、《》？；‘’：“”{}【】~！@#￥%……&*（）——]";
		str=str.replaceAll(Biaodian, "");
		return str;
	}
	public static void Transform(String filepath){
		DBmanipulate db=DBmanipulate.getInstance();
		int totalcount=0;
		int count=0;
		int errorcount=0;
		try{
			File file=new File(filepath);
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
						//db.insertGroup(nextline.split(":")[1], 0);
						System.out.println(nextline.split(":")[1]);
					}
					else if (nextline.contains("[QQ红包]")){
						continue;
					}
					else if(title==null&&Pattern.matches(pattern, nextline)){
						title=nextline;
						continue;
					}
					else if(title!=null&&!Pattern.matches(pattern, nextline)){
						
						content.append(ripeBiaodian(nextline));
					}
					else if (title!=null&&Pattern.matches(pattern, title)&&Pattern.matches(pattern, nextline)&&!content.equals(""))
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
							String dbcontent=content.toString();
							dbcontent=dbcontent.replaceAll("\'", "\'\'");
							dbcontent=dbcontent.replaceAll("\\\\", "");
							
							System.out.println(date+"  "+number+"  "+content);
							
							if(!dbcontent.contains("🏻🏻")&&!number.equals("10000"))
								db.insertShortText(dbcontent,date, number);
							title=nextline;
							
							content.delete(0, content.length());
							
						}catch(java.util.regex.PatternSyntaxException e)
						{e.printStackTrace();
						errorcount++;continue;}
					}
					lastline=nextline;
				}
				System.out.println(totalcount+"  "+count+"   "+errorcount);
				db.removeIllegal();
			}
			else{
				System.out.println("文件不存在");
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
