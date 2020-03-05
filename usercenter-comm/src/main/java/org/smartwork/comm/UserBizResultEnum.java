package org.smartwork.comm;
/***
 * UserBizResultEnum概要说明：业务系统错误代码
 * @author Huanghy
 */
public enum UserBizResultEnum {
	/***
	 * 004-用户中心
	 * 功能暂无-表示通用异常
	 * 001-为空判断
	 */
	MOBILE_FORMAT("004001001","手机号格式错误","%s手机号格式错误"),
	AUTH_CODE_EXISTS("004001002","验证码已发送，请稍后再试",""),
	PHONE_NOT_EXISTS("004001003","用户不存在","%s对应用户不存在"),
	LOGIN_CODE_EXISTS("004001004","短信已发送，请稍后再试",""),
	LOGIN_CODE_NOT_EXISTS("004001005","验证码过期，请重新发送",""),
	LOGIN_CODE_ERROR("004001006","验证码错误，请重新输入",""),
	PHONE_EXISTS("004001007","用户已存在","%s对应用户已存在"),
	EMAIL_FORMAT("004001008","email格式错误",""),
	GENDER_NOT_EXISTS("004001009","性别格式错误","");


	/**错误编码业务系统代码+功能编码+错误代码**/
	private String bizCode;
	/**错误描述****/
	private String bizMessage;
	/**带格式错误描述****/
	private String bizFormateMessage;

	/***
	 * 构造函数:
	 * @param bizCode
	 * @param bizMessage
	 * @param bizFormateMessage
	 */
	UserBizResultEnum(String bizCode, String bizMessage, String bizFormateMessage){
		this.bizCode = bizCode;
		this.bizMessage = bizMessage;
		this.bizFormateMessage = bizFormateMessage;
	}

	/** 
	 * @return bizCode 
	 */
	public String getBizCode() {
		return bizCode;
	}

	/** 
	 * @param bizCode 要设置的 bizCode 
	 */
	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	/** 
	 * @return bizMessage 
	 */
	public String getBizMessage() {
		return bizMessage;
	}

	/** 
	 * @param bizMessage 要设置的 bizMessage 
	 */
	public void setBizMessage(String bizMessage) {
		this.bizMessage = bizMessage;
	}

	/** 
	 * @return bizFormateMessage 
	 */
	public String getBizFormateMessage() {
		return bizFormateMessage;
	}

	/** 
	 * @param bizFormateMessage 要设置的 bizFormateMessage 
	 */
	public void setBizFormateMessage(String bizFormateMessage) {
		this.bizFormateMessage = bizFormateMessage;
	}
}
