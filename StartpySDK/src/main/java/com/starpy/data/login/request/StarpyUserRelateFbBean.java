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
public class StarpyUserRelateFbBean extends SGameBaseRequestBean {

	public StarpyUserRelateFbBean(Context context) {
		super(context);
	}

	private String fbId = "";

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}
}
