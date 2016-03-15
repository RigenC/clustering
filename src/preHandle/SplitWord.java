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
	private static HashSet<String> stopwordset=new HashSet<String>();
	private static List<Record> Record=new ArrayList<Record>();
	public static void main(String[] args) throws Exception {
		String argu = "";
		String system_charset = "utf-8";
		int charset_type = 1;
		loadStopWords();
		executeSplit();
		Iterator<preHandle.Record> it=Record.iterator();
		if (!CLibrary.Instance.NLPIR_Init(argu.getBytes(system_charset),
				charset_type, "0".getBytes(system_charset))) {
			System.err.println("初始化失败！");
		}
		List<RecordN> list=new ArrayList<RecordN>();
		while(it.hasNext()){
			Record record=it.next();
//			System.out.println(record.content);
			String afterSplit=CLibrary.Instance.NLPIR_ParagraphProcess(record.content, 0);
			List<String> afterripe=ripeStopWord(afterSplit);
			System.out.println("分词结果："+afterSplit+"====去停用词后结果："+afterripe);
			if(!afterripe.isEmpty()){
				RecordN rn=new RecordN(afterripe, record.user_number, record.record_time,record.session_id,record.response_To);
				list.add(rn);
			}
		}
		DBmanipulate.getInstance().insertRecordN(list);
	}
	private static List<String> ripeStopWord(String str){
		String[] afterSplit=str.split(" ");
		List<String> afterripe=new ArrayList<String>();
		for(int i=0;i<afterSplit.length;i++){
			boolean flag=true;
			Iterator<String> it=stopwordset.iterator();
			while(it.hasNext()){
				if(afterSplit[i].equals(it.next())){
					flag=false;
				}
			}
			if(flag&&!afterSplit[i].equals("")&&!afterSplit[i].equals(" "))
				afterripe.add(afterSplit[i]);
			else continue;
		}
		return afterripe;
	}
	private static void executeSplit() throws Exception{
		DBmanipulate db=DBmanipulate.getInstance();
		ResultSet rs=db.selectAll();
		while(rs.next()){
			Record r=new Record(rs.getInt("id"), rs.getString("content"), rs.getString("user_number"), rs.getString("record_time"),rs.getInt("session_id"),rs.getString("response_to"));
			Record.add(r);
		}
	}
	private static void loadStopWords() throws IOException{
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
class Record{
	int id;
	String content;
	String user_number;
	String record_time;
	int session_id;
	String response_To;
	public Record(int id,String content,String user_number,String record_time,int session_id,String response_To) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.content=content;
		this.user_number=user_number;
		this.record_time=record_time;
		this.session_id=session_id;
		this.response_To=response_To;
	}
}
