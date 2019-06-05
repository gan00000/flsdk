package com.gama.pay.onestore.bean.req;

import android.content.Context;

import com.core.base.utils.SStringUtil;
import com.gama.base.utils.GamaUtil;
import com.gama.pay.onestore.constants.OneStoreContant;

public class PayReqBean extends BPayReqBean {

	private String userId;
	private String payFrom = OneStoreContant.PAY_FROM;

	private String serverCode;
	private String serverName;
	private String roleId = "";
	private String roleName = "";

	private String extra = "";

	private String roleLevel = "";
	/**
	 * 原厂订单号
	 */
	private String cpOrderId = "";

	public PayReqBean(Context context) {
		super(context);

		initm(context);

	}

	private void initm(Context context) {
		userId = GamaUtil.getUid(context);

		serverCode = GamaUtil.getServerCode(context);
		serverName = GamaUtil.getServerName(context);
		roleName = GamaUtil.getRoleName(context);
		roleId = GamaUtil.getRoleId(context);
		roleLevel = GamaUtil.getRoleLevel(context);
	}

	public boolean isInitOk(){
		return !SStringUtil.hasEmpty(userId,getGameCode(),serverCode,roleId);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getServerCode() {
		return serverCode;
	}

	public void setServerCode(String serverCode) {
		this.serverCode = serverCode;
	}


	public String getPayFrom() {
		return payFrom;
	}

	public void setPayFrom(String payFrom) {
		this.payFrom = payFrom;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(String roleLevel) {
		this.roleLevel = roleLevel;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * 返回原厂订单号
	 */
	public String getCpOrderId() {
		return cpOrderId;
	}

	public void setCpOrderId(String cpOrderId) {
		this.cpOrderId = cpOrderId;
	}

}
