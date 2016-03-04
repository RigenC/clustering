package preHandle;

import java.util.Iterator;
import java.util.List;

public class RecordN{
	public String content;
	public String user_number;
	public String record_time;
	public RecordN(List<String> content,String user_number,String record_time){
		this.user_number=user_number;
		this.record_time=record_time;
		StringBuilder sb=new StringBuilder();
		Iterator<String> it=content.iterator();
		
		this.content=content.toString();
	}
}
