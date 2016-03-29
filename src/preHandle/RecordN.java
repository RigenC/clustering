package preHandle;

import java.util.Iterator;
import java.util.List;

public class RecordN{
	public int record_id;
	public List<String> content;
	public String user_number;
	public String record_time;
	public int session_id;
	public String response_to;
	public RecordN(int record_id,List<String> content,String user_number,String record_time,int session_id,String respons_to){
		this.record_id=record_id;
		this.user_number=user_number;
		this.record_time=record_time;
		this.session_id=session_id;
		this.content=content;
		this.response_to=respons_to;
	}
}
