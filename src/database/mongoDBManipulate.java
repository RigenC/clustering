package database;
import com.mongodb.util.*;
import com.mongodb.*;
public class mongoDBManipulate {
	public static void main(String[] args) {
		Mongo m=new Mongo();
		DB db=m.getDB("test");
		for(String name:db.getCollectionNames()){
			System.out.println(name);
		}
	}
}
