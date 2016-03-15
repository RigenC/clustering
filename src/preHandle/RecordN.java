package preHandle;

import java.util.Iterator;
import java.util.List;

public class RecordN{
	public String content;
	public String user_number;
	public String record_time;
	public int session_id;
	public String response_to;
	public RecordN(List<String> content,String user_number,String record_time,int session_id,String respons_to){
		this.user_number=user_number;
		this.record_time=record_time;
		this.session_id=session_id;
		StringBuilder sb=new StringBuilder();
		Iterator<String> it=content.iterator();
		this.content=content.toString();
		this.response_to=respons_to;
	}
}
