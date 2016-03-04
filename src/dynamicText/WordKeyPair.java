package dynamicText;

public class WordKeyPair {
	private String word1;
	private String word2;
	public String getWord1() {
		return word1;
	}
	public void setWord1(String word1) {
		this.word1 = word1;
	}
	public String getWord2() {
		return word2;
	}
	public void setWord2(String word2) {
		this.word2 = word2;
	}
	public WordKeyPair(String word1,String word2){
		this.word1=word1;
		this.word2=word2;
	}
	/**
	 * 返回两个词语的哈希码之和
	 */
	public int hashCode(){
		return word1.hashCode()+word2.hashCode();
	}
	public boolean equals(Object e){
		if(WordKeyPair.class.isInstance(e)){
			WordKeyPair d1=(WordKeyPair)e;
			if(word1.equals(d1.word1)&&word2.equals(d1.word2)){
				return true;
			}
			else if(word1.equals(d1.word2)&&word2.equals(d1.word1)){
				return true;
			}
			else
				return false;
		}
		else return false;
	}
}
