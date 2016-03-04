package clustering;

public final class Threshold {
	/**
	 * 义原相似度计算部分用到的参数，在Sememe.sememeSimilarity中用到
	 */
	public final static double alfa=1.6;
	/**
	 * 动态文本向量扩充用到的阈值，当词语相似度大于beta时添加到动态向量中。在DynamicTextVector.getDynamicSimilarity中用到
	 */
	public final static double beta=0.3;
	/**
	 * 分别是一小时转成的毫秒数和24小时转成的毫秒数
	 */
	public final static long hour1Toms=3600000;
	/**
	 * 下面的常值变量是在将消息添加到会话时使用到，判断消息与会话的最新消息之间的时间差，个人感觉可以适当调小
	 */
	public final static long maxduration=28800000;
	/**
	 * 在划分消息的会话归属时，若消息与会话的最新消息的时间差小于30s，则采用小阈值作为判断标准
	 */
	public final static long minduration=20000;
	/**
	 * 小阈值，当消息与会话的最新消息的时间差小于30s时，采用lgama作为判断标准
	 */
	public final static double lgama=0.25;
	/**
	 * 消息与会话的相似度计算中用到的阈值，在SessionExtract中用到。对会话抽取结果影响很大，目前经验取值在0.3~0.5之间
	 */
	public final static double gama=0.4;
	/**
	 * 构建词语关系网络时，词语的词频阈值。词频高于该阈值的词语才参与词语关系网络的构建运算
	 */
	public final static double miu=0.00004;
	/**
	 * 频繁词集的选择阈值，代表词语的最低词频
	 */
	public final static double theta=0;
}
