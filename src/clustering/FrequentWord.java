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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import database.DBmanipulate;
import database.mongoDBManipulate;
import preHandle.transformFromQQ;

public class FrequentWord {
	public static List<MyList<String>> allLks=new ArrayList<MyList<String>>();
	public static Map<MyList<String>,Integer> rankLk=new HashMap<MyList<String>,Integer>();
	public static void main(String[] args) throws IOException {
		Long starttime=new Date().getTime();
		findFrequentWordSet();
		System.out.println(new Date().getTime()-starttime);
			
	}
	public static void removeSingle(){
		
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
		allLks.addAll(c1);
//		printToFile(1, c1);
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
//			printToFile(i, Lk_1);
			Lk_1=Ck;
			allLks.addAll(Lk_1);
		}
		getMaxLks();
	}
	/**
	 * 直接删减策略
	 * @param Lk_1
	 * @param Ck
	 */
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
	/**
	 * 获取k-1维子集
	 * @param list
	 * @return 返回所有K-1阶子集
	 */
	public static List<MyList<String>> getChildList(MyList<String> list){
		List<MyList<String>> result=new ArrayList<MyList<String>>();
		if(list.size()<2)
			return result;
		for(String str:list){
			MyList<String> copy=new MyList<String>();
			copy.addAll(list);
			copy.remove(str);
			copy.sort();
			result.add(copy);
		}
		return result;
	}
	/**
	 * 获取1阶到K-1阶的所有子集
	 * @param list
	 * @return
	 */
	public static List<MyList<String>> getAllChildList(MyList<String> list){
		List<MyList<String>> result=new ArrayList<MyList<String>>();
		for(MyList<String> child:getChildList(list)){
			List<MyList<String>> childlist=getChildList(child);
			if(childlist.size()>0)
				result.addAll(childlist);
			else 
				return result;
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
	/**
	 * 计算完频繁项集后直接遍历所有计算最大频繁项集，对比用
	 * @throws IOException 
	 */
	public static void getMaxLks() throws IOException{
		mongoDBManipulate db=mongoDBManipulate.getInstance();
		Iterator<MyList<String>> it=allLks.iterator();
		List<MyList<String>> tobedeleted=new ArrayList<MyList<String>>();
		while(it.hasNext()){
			MyList<String> list=it.next();
			if(list.size()<4){
				tobedeleted.add(list);
			}
			List<MyList<String>> allchilds=getAllChildList(list);
			for(MyList<String> child:allchilds){
				if(allLks.contains(child))
					tobedeleted.add(child);
			}
		}
		allLks.removeAll(tobedeleted);
		for(MyList<String> list:allLks){
			int num=db.getWordFrequency(list);
			rankLk.put(list, num);
		}
		List<Map.Entry<MyList<String>, Integer>> demolist=new ArrayList(rankLk.entrySet());
		Collections.sort(demolist,new Comparator() {
			public int compare(Object o1, Object o2) {  
                Map.Entry obj1 = (Map.Entry) o1;  
                Map.Entry obj2 = (Map.Entry) o2;  
                return ((Integer) obj2.getValue()).compareTo((Integer)obj1.getValue());  
                }  
		});
		List<MyList<String>> printlist=new ArrayList<MyList<String>>();
		for(Map.Entry<MyList<String>, Integer> entry:demolist)
			printlist.add(entry.getKey());
//		printToFile(0, printlist);
	}
	/**
	 * 将频繁词集打印到文件中，测试阶段使用
	 * @param num
	 * @param set
	 * @throws IOException
	 */
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

