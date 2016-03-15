package preHandle;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLibrary extends Library {

		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"NLPIR", CLibrary.class);

		// printf函数声明
		public boolean NLPIR_Init(byte[] sDataPath, int encoding,
				byte[] sLicenceCode);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		
		public String NLPIR_GetKeyWords(String sLine,int nMaxKeyLimit,boolean bWeightOut);
		
		
		public void NLPIR_Exit();
}

