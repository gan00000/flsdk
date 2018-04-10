package com.starpy.thirdlib.facebook;

import android.text.TextUtils;

public class FriendProfile {

	public FriendProfile() {
		// TODO Auto-generated constructor stub
	}
	
	
	private String name;//fb好友头昵称
	private String id; //fb id（已完游戏） 或者fb token(未玩游戏)

	private String iconUrl;//fb好友头像url
	private String userId;//好友在starpy平台的账号id,即好友在玩该游戏的starpy平台的账号id，如果fb好友没有玩，此字段为NULL
	private String gender;//性别，有可能为空

	private FriendPicture friendPicture;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the friendPicture
	 */
	public FriendPicture getFriendPicture() {
		return friendPicture;
	}

	/**
	 * @param friendPicture the friendPicture to set
	 */
	public void setFriendPicture(FriendPicture friendPicture) {
		this.friendPicture = friendPicture;
		this.iconUrl = friendPicture.getUrl();
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isStarpyUser() {
		return !TextUtils.isEmpty(userId);
	}


	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
