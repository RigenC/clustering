package sementicAccurate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.Threshold;

public class Sememe {
    public static Map<Integer, Sememe> ALLPRIMITIVES = new HashMap<Integer, Sememe>();
    public static Map<String, Integer> PRIMITIVESID = new HashMap<String, Integer>();
    /**
     * TREEDEPTH中存放的是义原树的根节点的ID及该树的深度
     */
    public static Map<Integer ,Integer> TREEDEPTH= new HashMap<Integer ,Integer>();
//    public static double alfa=1.6;
    /**
     * 加载义原文件。
     */
    static {
        String line = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                    "dict/WHOLE.DAT"),"GB2312"));
            line = reader.readLine();

            while (line != null) {
            	int root=-1;
                line = line.trim().replaceAll("\\s+", " ");

                String[] strs = line.split(" ");
                int id = Integer.parseInt(strs[0]);
                String[] words = strs[1].split("\\|");
                String english = words[0];
                String chinese = strs[1].split("\\|")[1];
                int parentId = Integer.parseInt(strs[2]);
                if(id==parentId){
                	root=id;
                	TREEDEPTH.put(id,1);
                }
                else{
                	int depth=1;
                	Sememe parent= new Sememe(id,chinese,parentId);
                	while(!parent.isTop()){
                		depth++;
                		parent=ALLPRIMITIVES.get(parent.getParentId());
                	}
                	root=parent.getId();
                	if(TREEDEPTH.get(root)<depth)
                		TREEDEPTH.replace(root,depth);
                	ALLPRIMITIVES.get(parentId).updateChildNum();
                }
                
                ALLPRIMITIVES.put(id, new Sememe(id, chinese, parentId,root));
                //ALLPRIMITIVES.put(id, new Primitive(id, english, parentId));
                PRIMITIVESID.put(chinese, id);
                PRIMITIVESID.put(english, id);
                // System.out.println("add: " + primitive + " " + id + " " + parentId);
                line = reader.readLine();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(line);
            e.printStackTrace();
        }
    }

    private String primitive;

    /**
     * id number
     */
    private int id;
    private int parentId;
    private int treeroot;
    private int childnum=0;

    /**
     * Creates a new Primitive object.
     * 
     * @param id
     *            DOCUMENT ME!
     * @param primitive
     *            DOCUMENT ME!
     * @param parentId
     *            DOCUMENT ME!
     */
    public Sememe(int id, String primitive, int parentId) {
        this.id = id;
        this.parentId = parentId;
        this.primitive = primitive;
    }
    
    public Sememe(int id ,String primitive, int parentId , int treeroot){
    	this.id=id;
    	this.parentId=parentId;
    	this.primitive=primitive;
    	this.treeroot=treeroot;
    }
    public void setTreeId(int treeroot){
    	this.treeroot=treeroot;
    }
    
    public void updateChildNum(){
    	this.childnum++;
    	Sememe parent=ALLPRIMITIVES.get(parentId);
    	if(this.id!=parentId){
    		parent.updateChildNum();
    	}
    }
    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getPrimitive() {
        return primitive;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getId() {
        return id;
    }

    public double getChildNum(){
    	return childnum+1;
    }
    
    public int getRoot(){
    	return treeroot;
    }
    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isTop() {
        return id == parentId;
    }
    /**
     * 获取义项在义项树中的节点深度
     * @param 
     * @return 节点深度
     */
    public int getDepth(){
    	int depth=1;
    	Sememe parent= this;
    	while(!parent.isTop()){
    		depth++;
    		parent=ALLPRIMITIVES.get(parent.getParentId());
    	}
    	return depth;
    }
    /**
     * 获取两个义项的最小公共父节点
     * @param s1 第一个节点
     * @param s2 第二个节点
     * @return 最小公共父节点
     */
    public static Sememe getCommonParent(Sememe s1,Sememe s2){
    	Sememe CommonParent=null;
    	List<Integer> list=new ArrayList<Integer>();
    	Sememe ancestor1=s1;
    	Sememe ancestor2=s2;
    	list.add(s1.getId());
    	while(!ancestor1.isTop()){
    		ancestor1=ALLPRIMITIVES.get(ancestor1.getParentId());
    		list.add(ancestor1.id);
    		
    	}
    	if(list.contains(s2.getId()))
    		return s2;
    	while(!ancestor2.isTop()){
    		ancestor2=ALLPRIMITIVES.get(ancestor2.getParentId());
    		if(list.contains(ancestor2.getId())){
    			return ancestor2;
    		}
    			
    	}
    	return CommonParent;
    }
    /**
     * 获得一个义原的所有父义原，直到顶层位置。
     * 
     * @param primitive
     * @return 如果查找的义原没有查找到，则返回一个空list
     */
    public static List<Integer> getParents(String primitive) {
        List<Integer> list = new ArrayList<Integer>();

        // get the id of this primitive
        Integer id = PRIMITIVESID.get(primitive);

        if (id != null) {
        	Sememe parent = ALLPRIMITIVES.get(id);
            list.add(id);
            while (!parent.isTop()) {
                list.add(parent.getParentId());
                parent = ALLPRIMITIVES.get(parent.getParentId());
            }
        }

        return list;
    }
    /**
     * 
     * @param primitive
     * @return
     */
    public static boolean isSememe(String primitive){
        return PRIMITIVESID.containsKey(primitive);
    }
    
    /**
     * 计算两个义项的义项相似度
     * @param s1 第一个义项
     * @param s2 第二个义项
     * @return 两个义项的义项相似度
     */
    public static double sememeSimilrity(String str1,	String str2){
    	if(str1.equals(str2))
    		return 1;
    	Sememe s1=ALLPRIMITIVES.get(PRIMITIVESID.get(str1));
    	Sememe s2=ALLPRIMITIVES.get(PRIMITIVESID.get(str2));
    	if(s1==null){
//    		System.out.println();
    	}
    	if(!(s1.getRoot()==s2.getRoot())){
    		return 0.0001;
    	}
    	double beta=(s1.getDepth()>s2.getDepth()?s2.getDepth():s1.getDepth())/TREEDEPTH.get(s1.getRoot()).doubleValue();
    	Sememe common=getCommonParent(s1, s2);
    	double dist=(s1.getDepth()-common.getDepth())>(s2.getDepth()-common.getDepth())?(s1.getDepth()-common.getDepth()):(s2.getDepth()-common.getDepth());
    	double h=TREEDEPTH.get(s1.getRoot());
    	double totalchildnum=ALLPRIMITIVES.get(s1.getRoot()).getChildNum();
    	double result=(Threshold.alfa*beta/(Threshold.alfa*beta+dist))*(2*Math.log(common.getChildNum()/totalchildnum)/(Math.log(s1.getChildNum()/totalchildnum)+Math.log(s2.getChildNum()/totalchildnum)));
//    	if(((Double)result).isNaN())
//    		System.out.println("result:"+result);
    	return result;
    }
    /**
     * DOCUMENT ME!
     * 
     * @param args
     *            DOCUMENT ME!
     */
    public static void main(String[] args) {
//    	System.out.println(Sememe.getCommonParent(ALLPRIMITIVES.get(PRIMITIVESID.get("买")), ALLPRIMITIVES.get(PRIMITIVESID.get("保存"))));
    	System.out.println(ALLPRIMITIVES.get(PRIMITIVESID.get("文书")));

    }
}
