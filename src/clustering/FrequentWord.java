package clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import database.DBmanipulate;
import database.mongoDBManipulate;
import preHandle.transformFromQQ;

public class FrequentWord {
	public static void main(String[] args) throws IOException {
		System.out.println(transformFromQQ.sdf.format(new Date()));
		findFrequentWordSet();
		System.out.println(transformFromQQ.sdf.format(new Date()));
			
	}
	public static void findFrequentWordSet() throws IOException{
		mongoDBManipulate db=mongoDBManipulate.getInstance();
		Set<String> allword=db.getAllWord();
		Iterator<String> it=allword.iterator();
		List<MyList<String>> c1=new ArrayList<MyList<String>>();
		while(it.hasNext()){
			MyList<String> set=new MyList<String>();
			String word=it.next();
			set.add(word);
			int num=db.getWordFrequency(set);
			if(num<5)
				continue;
			else{
				c1.add(set);
			}
		}
		printToFile(1, c1);
		//以上部分是找到一阶频繁项集，以下是迭代过程
		//根据Lk-1计算Ck
		//对Ck中的每个元素计算其k-1阶子集，查看其是否在Lk-1中，修剪Ck
		//查看Ck中的元素的支持度是否大于最小值
		List<MyList<String>> Lk_1=c1;
		for(int i=2;Lk_1.size()!=0;i++){
			List<MyList<String>> Ck=calCandidate(Lk_1);
			deleteInvalid(Lk_1,Ck);
			Iterator<MyList<String>> iterator=Ck.iterator();
			while(iterator.hasNext()){
				MyList<String> list=iterator.next();
				int num=db.getWordFrequency(list);
				if(num<4){
					iterator.remove();
				}
			}
			//目前Ck是k阶频繁项集，Lk_1是k-1阶频繁项集，以下是寻找Lk-1中的最大频繁项集
			//对Ck中的元素求k-1阶子集，如果某个k-1阶子集在Lk-1中存在，则删除Lk-1中的这个元素，因为不是最大频繁项集
			Iterator<MyList<String>> iterator2=Lk_1.iterator();
			while(iterator2.hasNext()){
				MyList<String> list=iterator2.next();
				List<MyList<String>> childLists=getChildList(list);
				for(MyList<String> childList:childLists){
					if(Lk_1.contains(childList))
						iterator2.remove();
				}
			}
			Collections.sort(Ck);
			printToFile(i, Lk_1);
			Lk_1=Ck;
		}
	}
	public static void deleteInvalid(List<MyList<String>> Lk_1,List<MyList<String>> Ck){
		List<MyList<String>> todelete=new ArrayList<MyList<String>>();
		Iterator<MyList<String>> it=Ck.iterator();
		while(it.hasNext()){
			MyList<String> list=it.next();
			List<MyList<String>> childLists=getChildList(list);
			for(MyList<String> childList:childLists){
				if(!Lk_1.contains(childList))
					todelete.add(list);
			}
		}
		for(MyList<String> list:todelete){
			Lk_1.remove(list);
		}
	}
	public static List<MyList<String>> getChildList(MyList<String> list){
		List<MyList<String>> result=new ArrayList<MyList<String>>();
		for(String str:list){
			MyList<String> copy=new MyList<String>();
			copy.addAll(list);
			copy.remove(str);
			copy.sort();
			result.add(copy);
		}
		return result;
	}
	public static List<MyList<String>> calCandidate(List<MyList<String>> Lk){
		List<MyList<String>> result=new ArrayList<MyList<String>>();
		for(int i=0;i<Lk.size();i++){
			for(int j=i+1;j<Lk.size();j++){
				MyList<String> candidate=Lk.get(i).calAssociate(Lk.get(j));
				if(candidate!=null){
					result.add(candidate);
				}
			}
		}
		return result;
	}
	public static void printToFile(int num,List<MyList<String>> set) throws IOException{
		File file=new File("FrequentWord\\"+num+".txt");
		BufferedWriter fw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
		for(Collection<String> words:set){
			fw.append(words.toString());
			fw.newLine();
		}
		fw.flush();
		fw.close();
	}
}
class MyList<T extends Comparable> extends ArrayList<T> implements Comparable{

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		ArrayList<T> set=(ArrayList)arg0;
		for(int i=0;i<set.size();i++){
			try{
			if(!this.get(i).equals(set.get(i)))
				return this.get(i).compareTo(set.get(i));
			}
			catch(java.lang.IndexOutOfBoundsException e){
				e.printStackTrace();
			}
		}
		return 0;
	}
	public void sort(){
		java.util.Collections.sort(this);
	}
	public MyList<String> calAssociate(MyList<String> list){
		if(this.size()!=list.size())
			return null;
		MyList<String> MyList=new MyList<String>();
		for(int i=0;i<this.size()-1;i++){
			if(this.get(i).equals(list.get(i))){
				MyList.add((String)this.get(i));
			}
		}
		if(!this.get(this.size()-1).equals(list.get(list.size()-1))){
			MyList.add((String)this.get(this.size()-1));
			MyList.add(list.get(list.size()-1));
			MyList.sort();
			if(MyList.size()!=list.size()+1)
				return null;
			else
			return MyList;
		}
		else{
			return null;
		}
	}
}
