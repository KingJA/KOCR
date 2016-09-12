使用环境：
1.将 libimageengine.so 和 libIDCardengine.so 文件放置 /libs/armeabi 目录中。
2.将 idcard_engine.jar	文件放置 /libs 目录中。

API:
OcrEngine.class
引擎常量：
识别结果状态：
	public static final int RECOG_OK = 1;			//识别成功其他值为识别失败

recognize()
证件图像进行识别方法。

	/**
	 * 识别
	 *
	 * @param context
	 * @param imgBuffer		图像数据 正面  JPG格式
* @param imgBuffer		图像数据 背面  JPG格式  可设为null
(同时识别身份证正面、背面需要这两个参数，如单独识别正面或者背面，请使用第一个参数)
	 * @param imgpath			    头像保存路径   可以为空字符串
	 * @return						证件信息
	 */
	public IDCard recognize(Context context, byte[] imgBuffer, byte[] imgBuffer, String  imgpath);


IDCard.class
	/**
	 * @return	识别结果标识
	 */
	public int getRecogStatus() ;

	/**
	 * @return	姓名
	 */
	public String getName() ;

	/**
	 * @return	证件号
	 */
	public String getCardNo() ;

	/**
	 * @return	性别
	 */
	public String getSex() ;

	/**
	 * @return	民族
	 */
	public String getEthnicity() ;

	/**
	 * @return	出生
	 */
	public String getBirth() ;

	/**
	 * @return	住址
	 */
	public String getAddress() ;

	/**
	 * @return	签发机关
	 */
	public String getAuthority() ;

	/**
	 * @return	有效期限
	 */
	public String getPeriod() ;

	/**
	 * @return	备注
	 */
	public String getMemo() ;

