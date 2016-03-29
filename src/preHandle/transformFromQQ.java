package preHandle;

import java.io.*;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

import database.DBmanipulate;
import database.mongoDBManipulate;;

public class transformFromQQ {
	private static String pattern="\\d{4}-[0-1]\\d-[0-3]\\d \\d{1,2}:[0-6]\\d:[0-6]\\d .*\\(\\d{1,13}_\\d{1,4}\\)";
	private static String timestamppattern="\\d{4}-[0-1]\\d-[0-3]\\d \\d{1,2}:[0-6]\\d:[0-6]\\d ";
	public static Map<String,Set<String>> ALLUSERNAME=new HashMap<String,Set<String>>();
	public static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
	transformFromQQ(){
		
	}
	public static void main(String[] args) throws IOException {
//		System.out.println(ripeBiaodian("今天我，终于站在这年轻的“战场”；、"));
		Transform("标注.txt");
//		String line="jerry是的";
//		Pattern ptime=Pattern.compile("@\\S+");
//		Matcher matche=ptime.matcher(line);
//		;
//		System.out.println(matche.find());
	}
	private static String ripeBiaodian(String str){
		str=str.replaceAll("[表情]", "");
		str=str.replaceAll("[图片]", "");
		String Biaodian="[,./'\";@:><?\\|\\[\\]~!#$%^&*()_+-=，。、《》？；‘’：“”{}【】~！#￥%……&*（）——]";
		str=str.replaceAll(Biaodian, "");
		return str;
	}
	private static String ripeUserName(String content){
		for(Entry<String, Set<String>> entry:ALLUSERNAME.entrySet()){
			for(String name:entry.getValue()){
				if(content.contains("@"+name)){
					return name;
				}
			}
		}
		return null;
	}
	public static void Transform(String filepath) throws IOException{
		SplitWord.loadStopWords();
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<RecordN> list=new ArrayList<RecordN>();
		int recordid=1;
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
						title=null;
						continue;
					}
					else if(title==null&&Pattern.matches(pattern, nextline)){
						title=nextline;
						continue;
					}
					else if(title!=null&&!Pattern.matches(pattern, nextline)){
						
						content.append(nextline);
					}
					else if(title!=null&&Pattern.matches(pattern, title)&&Pattern.matches(pattern, nextline)&&content.equals("")){
						title=null;
						continue;
					}
					else if (title!=null&&Pattern.matches(pattern, title)&&Pattern.matches(pattern, nextline)&&!content.equals(""))
					{
						count++;
						try{
							String dbcontent=content.toString();
							Pattern ptime=Pattern.compile(timestamppattern);
							Pattern pnumber=Pattern.compile("\\(\\d{5,12}_\\d{1,4}\\)");
//							Pattern presponseTo=Pattern.compile("@\\S+ ");
//							Matcher mresponseTo=presponseTo.matcher(content);
							String responseTo=ripeUserName(dbcontent);
							if(responseTo!=null){
								dbcontent=dbcontent.replaceAll("@"+responseTo, "");
								responseTo=responseTo.replace("@", "");
							}
//							if(mresponseTo.find()){
//								responseTo=mresponseTo.group();
//								dbcontent=dbcontent.replace(responseTo, "");
//								responseTo=responseTo.substring(1, responseTo.length()-1);
//							}
							Matcher mtime=ptime.matcher(title);
							Matcher mnumber=pnumber.matcher(title);
							String date=null;
							mtime.find();
							date=mtime.group();
							String str=null;
							while(mnumber.find()){
								str=mnumber.group();
							}
							String nickname=title.replaceAll(timestamppattern, "");
							nickname=nickname.replaceAll("\\(\\d{5,12}_\\d{1,4}\\)", "");
							nickname=nickname.replaceFirst("【.*?】", "");
							str=str.replace("(", "");
							str=str.replace(")", "");
							String number=str.split("_")[0];
							String sessionnumber=str.split("_")[1];
							dbcontent=dbcontent.replaceAll("\'", "\'\'");//sql语句中不能直接包含'
							dbcontent=dbcontent.replaceAll("\\\\", "");
							dbcontent=ripeBiaodian(dbcontent);
							//将用户的昵称保存到Map中
							if(ALLUSERNAME.containsKey(number)){
								Set<String> nameset=ALLUSERNAME.get(number);
								nameset.add(nickname);
							}
							else{
								if(!nickname.equals("")){
									Set<String> nameset=new HashSet<String>();
									nameset.add(nickname);
									ALLUSERNAME.put(number, nameset);
								}
							}
//							System.out.println(date+"  "+number+"  "+content);
							//以下是分词的步骤
							if(!dbcontent.contains("🏻🏻")&&!number.equals("10000")){
								List<SegToken> segtokens=segmenter.process(dbcontent, SegMode.SEARCH);
								List<String> afterSplit=new ArrayList<String>();
								for(SegToken st:segtokens){
									afterSplit.add(st.word);
								}
								System.out.print("分词结果："+afterSplit.toString());
								List<String> afterripe=SplitWord.ripeStopWord(afterSplit);
								System.out.println("====去停用词后结果："+afterripe.toString());
								if(!afterSplit.isEmpty()){
									RecordN rn=new RecordN(recordid++,afterripe, number, date,Integer.parseInt(sessionnumber),responseTo);
									list.add(rn);
								}
							}
							title=nextline;
							content.delete(0, content.length());
						}catch(java.util.regex.PatternSyntaxException e)
						{e.printStackTrace();
						errorcount++;continue;}
					}
					lastline=nextline;
				}
				mongoDBManipulate.getInstance().insertIntoChatRecord(list);
//				System.out.println(ALLUSERNAME.size());
				mongoDBManipulate.getInstance().insertUserName();
				System.out.println(totalcount+"  "+count+"   "+errorcount);
				mongoDBManipulate.getInstance().removeIllegal();
			}
			else{
				System.out.println("文件不存在");
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
