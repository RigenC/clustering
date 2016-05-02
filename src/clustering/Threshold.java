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
	 * 分别是一小时转成的毫秒数和24小时转成的毫秒数.在会话抽取中用到，作为会话安全窗口时间，该窗口内的所有会话必须包含
	 */
	public final static long hour1Toms=3600000;
	/**
	 * 下面的常值变量是在将消息添加到会话时使用到，会话抽取边界时间
	 */
	public final static long maxduration=14400000;
	/**
	 * 在划分消息的会话归属时，若消息与会话的最新消息的时间差小于30s，则采用小阈值作为判断标准
	 */
	public final static long minduration=30000;
	/**
	 * 会话抽取算法中使用，K个最新会话
	 */
	public final static int k=6;
	/**
	 * 计算会话与消息的相似度时只考虑会话中的最新几条消息
	 */
	public final static int newsetvector=8;
	/**
	 * 小阈值，当消息与会话的最新消息的时间差小于30s时，采用lgama作为判断标准
	 */
	public final static double lgama=0.3;
	/**
	 * 消息与会话的相似度计算中用到的阈值，在SessionExtract中用到。对会话抽取结果影响很大，目前经验取值在0.3~0.5之间
	 */
	public final static double gama=0.45;
	/**
	 * 构建词语关系网络时，词语的词频阈值。词频高于该阈值的词语才参与词语关系网络的构建运算
	 */
	public final static double miu=0.00004;
	/**
	 * 频繁词集的选择阈值，代表词语的最低词频
	 */
	public final static double theta=0;
	/**
	 * 频繁项集初次聚类使用，每个聚簇包含的项集的数量，小于则判定为游离点
	 */
	public final static double t=2;
	
	public final static double z=2;
	/**
	 * 可视化阶段，每个聚簇选取的关键词个数。
	 */
	public final static int pointNum=25;
}
