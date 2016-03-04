package dynamicText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.Threshold;

public class DynamicTextVector {
//	private static final double beta=0.5;
	/**
	 * 计算两个文档向量在扩充之前的相似度，即原始向量的相似度
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double originalTFIDF(Map<String,Double> v1,Map<String,Double> v2){
		Map<String,Double> map1=new HashMap<String, Double>();
		map1.putAll(v1);
		Map<String,Double> map2=new HashMap<String,Double>();
		map2.putAll(v2);
		double mole=0;
		double deno1=0;
		double deno2=0;
		for(String key1:map1.keySet()){
			deno1+=Math.pow(map1.get(key1), 2);
			if(map2.containsKey(key1)){
				mole+=map1.get(key1)*map2.get(key1);
			}
		}
		deno1=Math.sqrt(deno1);
		for(Double value:map2.values()){
			deno2+=Math.pow(value, 2);
		}
		deno2=Math.sqrt(deno2);
		double result=mole/(deno1*deno2);
		return result;
	}
	private static List<String> getJTS(Map<String,Double> v1,Map<String, Double> v2){
		List<String> list=new ArrayList<String>();
		for(String key:v1.keySet()){
			if(v2.containsKey(key))
				list.add(key);
		}
		return list;
	}
	private static List<String> getOTS(Map<String,Double> v1,Map<String,Double> v2){
		List<String> result=new ArrayList<String>();
		for(String key:v1.keySet()){
			if(!v2.containsKey(key))
				result.add(key);
		}
		return result;
	}
	public static double getDynamicSimilarity(Map<String,Double> v1,Map<String,Double> v2){
		Map<String,Double> originalVector1=v1;
		Map<String,Double> originalVector2=v2;
		Map<String,Double> relativeVector1=new HashMap<String,Double>();
		Map<String,Double> relativeVector2=new HashMap<String,Double>();
		relativeVector1.putAll(originalVector1);
		relativeVector2.putAll(originalVector2);
		List<String> OTSv1=getOTS(originalVector1, originalVector2);
		List<String> OTSv2=getOTS(originalVector2, originalVector1);
		while(true){
			double max=0;
			String v1key=null;
			String v2key=null;
			for(String key1:OTSv1){
				for(String key2:OTSv2){
					double similarity=WordSimilarityNet.getSimilarity(key1, key2);
					if(similarity>max){
						max=similarity;
						v1key=key1;
						v2key=key2;
					}
				}
			}
			if(max>Threshold.beta){
				double similarity=WordSimilarityNet.getSimilarity(v1key, v2key);
//				System.out.println(v1key+" 与 "+v2key+"的相似度大于beta，为："+similarity);
				relativeVector1.put(v2key, originalVector1.get(v1key)*similarity);
				relativeVector2.put(v1key, originalVector2.get(v2key)*similarity);
				OTSv1.remove(v1key);
				OTSv2.remove(v2key);
			}
			else break;
		}
		double dynamicresult=originalTFIDF(relativeVector1, relativeVector2);
		double originalresult=originalTFIDF(originalVector1, originalVector2);
//		System.out.println(dynamicresult);
		if(dynamicresult>originalresult)
			return dynamicresult;
		else return originalresult;
	}
	
	public static void main(String[] args) throws ParseException {
//		TextVector vector1=new TextVector(1,"[老师, 采购, 物资, 合同, 订单, 入账, 分, 录, 做]", "2015-10-23 13:14:39", "756257469");
//		TextVector vector2=new TextVector(2,"[物资, 入库,购买]", "2015-10-23 13:14:39", "756257469");
//		System.out.println(originalTFIDF(vector1.getTfidfVector(), vector2.getTfidfVector()));
//		System.out.println(getDynamicSimilarity(vector1.getTfidfVector(),vector2.getTfidfVector()));
		
	}
}
