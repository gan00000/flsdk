package com.starpy.data.login.request;

import android.content.Context;

import com.starpy.base.bean.SGameBaseRequestBean;

/**
* <p>Title: AdsRequestBean</p>
* <p>Description: 接口请求参数实体</p>
* <p>Company:GanYuanrong</p>
* @author GanYuanrong
* @date 2014年8月22日
*/
public class QueryFbToStarpyUserIdBean extends SGameBaseRequestBean {

	public QueryFbToStarpyUserIdBean(Context context) {
		super(context);
	}

	private String fbIds = "";

	public String getFbIds() {
		return fbIds;
	}

	public void setFbIds(String fbIds) {
		this.fbIds = fbIds;
	}
}
