package clustering;

import java.util.ArrayList;

public class MyList<T extends Comparable> extends ArrayList<T> implements Comparable{

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		ArrayList<T> set=(ArrayList)arg0;
		if(this.size()!=set.size()){
			return this.size()-set.size();
		}
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
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		ArrayList<T> set=(ArrayList)o;
		if(this.size()!=set.size())
			return false;
		for(int i=0;i<set.size();i++){
			if(!this.get(i).equals(set.get(i)))
				return false;
		}
		return true;
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