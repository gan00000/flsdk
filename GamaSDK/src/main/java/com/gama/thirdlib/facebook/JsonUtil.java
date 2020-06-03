package com.gama.thirdlib.facebook;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

	public static List<FriendProfile> parseInviteFriendsJson(JSONObject inviteFriendsJson){

		List<FriendProfile> friendProfiles = new ArrayList<FriendProfile>();
		if (inviteFriendsJson != null) {
			JSONArray jsonArray = inviteFriendsJson.optJSONArray("data");
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject friendsItem = jsonArray.optJSONObject(i);
					String name = friendsItem.optString("name", "");
					String id = friendsItem.optString("id", "");
					JSONObject friendsItemPicture = friendsItem.optJSONObject("picture");

					FriendPicture friendPicture = new FriendPicture();
					if (friendsItemPicture != null) {
						JSONObject friendsItemPictureData = friendsItemPicture.optJSONObject("data");
						if (friendsItemPictureData != null) {
							int height = friendsItemPictureData.optInt("height", 0);
							int width = friendsItemPictureData.optInt("width", 0);
							String url = friendsItemPictureData.optString("url", "");
							boolean is_silhouette = friendsItemPictureData.optBoolean("is_silhouette", false);

							friendPicture.setHeight(height);
							friendPicture.setWidth(width);
							friendPicture.setIs_silhouette(is_silhouette);
							friendPicture.setUrl(url);

						}
					}

					FriendProfile friendProfile = new FriendProfile();
					friendProfile.setName(name);
					friendProfile.setThirdId(id);
					friendProfile.setFriendPicture(friendPicture);

					friendProfiles.add(friendProfile);
				}
			}
		}
		return friendProfiles;
	}

	public static List<FriendProfile> parseMyFriendsJson(JSONObject myFriendsJson){

		List<FriendProfile> friendProfiles = new ArrayList<FriendProfile>();
		if (myFriendsJson != null) {
			JSONObject friendsJsonObject = myFriendsJson.optJSONObject("friends");

			if (friendsJsonObject == null){
				return friendProfiles;
			}

			JSONArray jsonArray = friendsJsonObject.optJSONArray("data");
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject friendsItem = jsonArray.optJSONObject(i);
					String name = friendsItem.optString("name", "");
					String id = friendsItem.optString("id", "");
					JSONObject friendsItemPicture = friendsItem.optJSONObject("picture");

					FriendPicture friendPicture = new FriendPicture();
					if (friendsItemPicture != null) {
						JSONObject friendsItemPictureData = friendsItemPicture.optJSONObject("data");
						if (friendsItemPictureData != null) {
							int height = friendsItemPictureData.optInt("height", 0);
							int width = friendsItemPictureData.optInt("width", 0);
							String url = friendsItemPictureData.optString("url", "");
							boolean is_silhouette = friendsItemPictureData.optBoolean("is_silhouette", false);

							friendPicture.setHeight(height);
							friendPicture.setWidth(width);
							friendPicture.setIs_silhouette(is_silhouette);
							friendPicture.setUrl(url);

						}
					}

					FriendProfile friendProfile = new FriendProfile();
					friendProfile.setName(name);
					friendProfile.setThirdId(id);
					friendProfile.setFriendPicture(friendPicture);

					friendProfiles.add(friendProfile);
				}
			}
		}
		return friendProfiles;
	}
}
