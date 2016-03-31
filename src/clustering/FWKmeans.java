package clustering;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FWKmeans {
	//数据总数
	private int InstanceNumber;
	//聚簇数量
	private int ClassCount;
	//原始数据
	private List<MyList<String>> data=new ArrayList<MyList<String>>();
	//每个聚簇的均值中心
	private List<Map<String,Double>> classData=new ArrayList<Map<String,Double>>();
	//存放噪音
	private List<Integer> noises=new ArrayList<Integer>();
	//存放每次迭代的结果
	private List<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
	public FWKmeans(List<MyList<String>> data,int ClassCount) {
		this.data=data;
		this.ClassCount=ClassCount;
		this.InstanceNumber=data.size();
	}
	public FWKmeans(int ClassCount){
		this.ClassCount=ClassCount;
	}
	public void readData(){
		try {
			List<MyList<String>> data=new ArrayList<MyList<String>>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
			        "FrequentWord/0.txt"),"utf-8"));
			String line=reader.readLine();
			while(line!=null){
				String str=line.substring(1, line.length()-1);
				String[] split=str.split(",");
				MyList<String> list=new MyList<String>();
				for(String s:split){
					list.add(s.trim());
				}
				data.add(list);
				line=reader.readLine();
			}
			this.data=data;
			this.InstanceNumber=this.data.size();
			System.out.println("数据加载完毕，数据量为"+this.data.size());
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void cluster(){
		// 标记是否需要重新找初始点
		boolean needUpdataInitials = true;
		// 找初始点的迭代次数
		int times = 1;
		// 找初始点
		while (needUpdataInitials) {
			needUpdataInitials = false;
			result.clear();
//			System.out.println("Find Initials Iteration" + (times++) + "time(s)");
			// 一次找初始点的尝试和根据初始点的分类
			findInitials();
			firstCluster();
			// 如果某个分类的数目小于特定的阈值，则认为这个分类中的所有样本都是噪声点
			// 需要重新找初始点
			for (int i = 0; i < result.size(); i++) {
				System.out.println("result "+i+" 的size:"+result.get(i).size());
				if (result.get(i).size() < InstanceNumber / Math.pow(ClassCount, Threshold.t)) {
					needUpdataInitials = true;
					noises.addAll(result.get(i));
				}
			}
		}
//		System.out.println(InstanceNumber / Math.pow(ClassCount, Threshold.t));
//		System.out.println(classData.size()+"  "+noises.size());
		Adjust();
		int i=1;
		for(ArrayList<Integer> cluster:result){
			System.out.println("聚簇"+i++);
			for(Integer num:cluster){
				System.out.println(data.get(num).toString());
			}
		}
	}
	public void findInitials() {
		// a,b为标志距离最远的两个向量的索引
		int i, j, a, b;
		i = j = a = b = 0;
		// 最远距离
		double maxDis = 0.0;
		// 已经找到的初始点个数
		int alreadyCls = 2;
		// 存放已经标记为初始点的向量索引
		ArrayList<Integer> initials = new ArrayList<Integer>();
		// 从两个开始
		for (; i < InstanceNumber; i++) {
			// 噪声点
			if (noises.contains(i))
				continue;
			// long startTime = System.currentTimeMillis();
			j = i + 1;
			for (; j < InstanceNumber; j++) {
				// 噪声点
				if (noises.contains(j))
					continue;
				// 找出最大的距离并记录下来
				double newDis = calDis(data.get(i), data.get(j));
				if (maxDis==0||maxDis > newDis) {
					a = i;
					b = j;
					maxDis = newDis;
				}
			}
		}
		// 将前两个初始点记录下来
		initials.add(a);
		initials.add(b);
		addClassData(data.get(a));
		addClassData(data.get(b));
		// 在结果中新建存放某样本索引的对象，并把初始点添加进去
		ArrayList<Integer> resultOne = new ArrayList<Integer>();
		ArrayList<Integer> resultTwo = new ArrayList<Integer>();
		resultOne.add(a);
		resultTwo.add(b);
		result.add(resultOne);
		result.add(resultTwo);
		// 找到剩余的几个初始点
		while (alreadyCls < ClassCount) {
			i = j = 0;
			double minMax = 1;
			int newClass = -1;

			// 找最小值中的最大值
			for (; i < InstanceNumber; i++) {
				double max = 0.0;
				double newMin = 0.0;
				// 找和已有类的最小值
				if (initials.contains(i))
					continue;
				// 噪声点去除
				if (noises.contains(i))
					continue;
				for (j = 0; j < alreadyCls; j++) {
//					System.out.println(i+"   "+classData.get(j));
					newMin = calDis(data.get(i), classData.get(j));
					if (max == 0 || newMin > max)
						max = newMin;
				}
				// 新最小距离较大
				if (max < minMax) {
					minMax = max;
					newClass = i;
				}
			}
			// 添加到均值集合和结果集合中
			// System.out.println("NewClass"+newClass);
			initials.add(newClass);
			addClassData(data.get(newClass));
			alreadyCls++;
			ArrayList<Integer> rslt = new ArrayList<Integer>();
			rslt.add(newClass);
			result.add(rslt);
		}
	}
	/**
	 * 创建初始聚簇之后进行聚类计算
	 */
	public void firstCluster() {
		// 根据初始向量分类
		for (int i = 0; i < InstanceNumber; i++) {
			double max = 0.0;
			int clsId = -1;
			for (int j = 0; j < classData.size(); j++) {
				// 余弦相似度
				double sim = calDis(data.get(i),classData.get(j));
				if (clsId == -1 || sim > max) {
					clsId = j;
					max = sim;
				}
			}
			// 本身不再添加
			if (!result.get(clsId).contains(i))
				result.get(clsId).add(i);
		}
	}
	/**
	 * 调整，直到聚簇中心不再变化
	 */
	public void Adjust() {
		// 记录是否发生变化
		boolean change = true;

		// 循环的次数
		int times = 1;
		while (change) {
			// 复位
			change = false;
			System.out.println("Adjust Iteration" + (times++) + "time(s)");

			// 重新计算每个类的均值
			for (int i = 0; i < ClassCount; i++) {
				// 原有的数据
				ArrayList<Integer> cls = result.get(i);
				// 新的均值
				Map<String,Double> newMean=new HashMap<String,Double>();
				// 计算均值
				for(Integer index:result.get(i)){
					MyList<String> list=data.get(index);
					for(String word:list){
						if(newMean.containsKey(word)){
							newMean.put(word, newMean.get(word)+1.0);
						}
						else{
							newMean.put(word, 1.0);
						}
					}
				}
				for(Map.Entry<String,Double> entry:newMean.entrySet()){
					newMean.put(entry.getKey(), entry.getValue()/(double)result.get(i).size());
				}
				if (!compareMeans(newMean,classData.get(i))) {
					classData.set(i, newMean);
					change = true;
				}
			}
			// 清空之前的数据
			for (ArrayList<Integer> cls : result)
				cls.clear();

			// 重新分配
			for (int i = 0; i < InstanceNumber; i++) {
				double max = 0.0;
				int clsId = -1;
				for (int j = 0; j < classData.size(); j++) {
					double newMax = calDis(data.get(i),classData.get(j));
					if (clsId == -1 || newMax > max) {
						clsId = j;
						max = newMax;
					}
				}
				result.get(clsId).add(i);
			}
		}

	}
	public static void main(String[] args) {
		long startTime=new Date().getTime();
		FWKmeans means=new FWKmeans(5);
		means.readData();
		means.cluster();
		System.out.println(new Date().getTime()-startTime);
//		Map<String,Double> map1=new HashMap<String,Double>();
//		map1.put("1", 1.0);map1.put("2", 2.0);
//		Map<String,Double> map2=new HashMap<String,Double>();
//		map2.put("1", 1.0);map2.put("2", 2.0);
//		System.out.println(new FWKmeans(1).compareMeans(map1, map2));
	}
	/**
	 * 寻找初始聚簇中心，将原始数据添加作为聚簇中心
	 * @param list
	 */
	public void addClassData(List<String> list){
		Map<String,Double> map=new HashMap<String,Double>();
		for(String s:list){
			map.put(s, 1.0);
		}
		classData.add(map);
	}
	public boolean compareMeans(Map<String,Double> map1,Map<String,Double> map2){
		if(map1.size()!=map2.size()){
			return false;
		}
		for(Map.Entry<String, Double> entry:map1.entrySet()){
			if(map2.containsKey(entry.getKey())&&map2.get(entry.getKey()).equals(entry.getValue()))
				continue;
			else{
				return false;
			}
		}
		return true;
	}
	/**
	 * 计算数据与数据/聚簇中心得余弦相似度
	 * @param list1 数据
	 * @param o 数据list<String>或聚簇中心map<String,Double>
	 * @return 如果是list或者map，返回计算结果。如果不是，返回Nan
	 */
	public static double calDis(MyList<String> list1,Object o){
		if(o instanceof ArrayList){
			double num=0.0;
			ArrayList list2=(ArrayList)o;
			for(String str:list1){
				if(list2.contains(str))
					num++;
			}
			double result=num/(Math.sqrt((double)list1.size())*(Math.sqrt((double)list2.size())));
			return result;
		}
		else if(o instanceof Map){
			double num=0.0;
			Map<String,Double> map=(HashMap)o;
			for(String str:list1){
				if(map.keySet().contains(str)){
					num+=map.get(str);
				}
			}
			double num1=0.0;
			for(double d:map.values()){
				num1+=Math.pow(d, 2);
			}
			num1=Math.sqrt(num1);
			double result=num/(num1*Math.sqrt((double)Math.sqrt(list1.size())));
			return result;
		}
		return Double.NaN;
	}

}
