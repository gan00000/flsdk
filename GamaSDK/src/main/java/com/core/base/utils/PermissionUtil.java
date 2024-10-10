package com.core.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

/*

权限请求时
一  设备api>=23
1.targetVerison >=23 默认权限关闭，动态请求弹出权限请求对话框，（AndroidMenifest.xml必须要配置相应的权限，不然请求无效，不会弹出对话框）
2.targetVerison < 23 默认权限开启，不能动态请求权限；但可在应用程序进行开关设置

二  设备api<23

targetVerison为任何值都不需要请求，请求也无效
*/


public class PermissionUtil {

	private static final String TAG = "PermissionUtil";
	private static final String per_file = "SDK_Permission_File.xml";
	private static final String per_key = "SDK_Permission_Key";

	public PermissionUtil() {
	}

	/**
	 * Requests the Camera permission. If the permission has been denied
	 * previously, a SnackBar will prompt the user to grant the permission,
	 * otherwise it is requested directly.
	 */
	public static void requestPermission(Activity activity, String permission, int requestCode) {

		if (hasSelfPermission(activity, permission)){
			return;
		}

		String permissionKey = permission.replace(".","_").toLowerCase();
		boolean isFirstRequest = true;
		if (SStringUtil.isEmpty(SPUtil.getSimpleString(activity, per_file, permissionKey))){
			isFirstRequest = true;
		}else {
			isFirstRequest = false;
		}
		boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
		PL.d("shouldShowRequestPermissionRationale=" + shouldShow);
		String[] p = new String[]{permission};
		if(!shouldShow && isFirstRequest){
			//1、从来没有申请过，第一次申请
			PL.d("first request permission=" + permission);
			ActivityCompat.requestPermissions(activity, p, requestCode);
			SPUtil.saveSimpleInfo(activity, per_file, permissionKey,"1000");
			return ;
		}

		if(shouldShow){
			//2、拒绝后，再申请提示,【注意：鸿蒙系统没有这步,google手机也没这步，首次拒绝后后面直接返回false了。。。】
			PL.d("shouldShow true request permission=" + permission);
			ActivityCompat.requestPermissions(activity, p, requestCode);
			SPUtil.saveSimpleInfo(activity, per_file, permissionKey,"1000");
			return ;
		}

		if (!shouldShow && !isFirstRequest){
			//3、拒绝了且不在提醒后，再次申请，弹窗提示，确定去系统设置中设置 权限
//			showPermissionDialog("需要您的相机权限", "当前操作需要使用相机权限，不开启权限将无法使用此功能", "去设置",new OnButtomListener() {
//						@Override
//						public void onCancel() {
//						}
//
//						@Override
//						public void onEnter() {
//							//跳转设置
//							goAppSetting();
//						}
//					});
//			return ;
		}

	}
	
//	public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
//
//		//ActivityCompat.requestPermissions(activity, permissions, requestCode);
//		checkAndroidRequestPermissions(activity, permissions, requestCode);
//	}
	
//	private static boolean checkAndroidRequestPermissions(Activity activity, String[] permissions, int requestCode) {
//
//		String[] p = PermissionUtil.hasSelfPermissionFilter(activity, permissions);
//		Log.d(TAG, "has not permissions length:" + p.length);
//		if (p.length > 0) {
//			ActivityCompat.requestPermissions(activity, p, requestCode);
//			return false;
//		}
//		return true;
//	}
	
	
//	final static String[] PERMISSIONS_PHONE = new String[]{ Manifest.permission.READ_PHONE_STATE};
//
//	final static String[] PERMISSIONS_STORAGE = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE };
//	final static String[] PERMISSIONS_ACCOUNTS = new String[]{ Manifest.permission.GET_ACCOUNTS };
//
//	final static String[] PERMISSIONS_PHONE_STORAGE = new String[]{
//			Manifest.permission.READ_PHONE_STATE,
//			Manifest.permission.WRITE_EXTERNAL_STORAGE };
//
//	final static String[] PERMISSIONS_PHONE_STORAGE_CONTACTS = new String[]{
//			Manifest.permission.READ_PHONE_STATE,
//			Manifest.permission.WRITE_EXTERNAL_STORAGE,
//			Manifest.permission.GET_ACCOUNTS};
	
	/**
	 * <p>Description: 请求权限，并且自动检测是否已拥有权限</p>
	 * @param activity
	 * @param requestCode
	 * @return
	 * @date 2015年10月19日
	 */
//	public static boolean requestPermissions_PHONE(Activity activity,int requestCode){
//
//		return requestPermission(activity, PERMISSIONS_PHONE, requestCode);
//	}
//
//	public static boolean requestPermissions_STORAGE(Activity activity,int requestCode){
//		return checkAndroidRequestPermissions(activity, PERMISSIONS_STORAGE, requestCode);
//	}
//
//	public static boolean requestPermissions_CONTACTS(Activity activity,int requestCode){
//		return checkAndroidRequestPermissions(activity, PERMISSIONS_ACCOUNTS, requestCode);
//	}
//
//
//	public static boolean requestPermissions_PHONE_STORAGE(Activity activity,int requestCode){
//
//		return checkAndroidRequestPermissions(activity, PERMISSIONS_PHONE_STORAGE, requestCode);
//	}
//
//	public static boolean requestPermissions_PHONE_STORAGE_CONTACTS(Activity activity,int requestCode){
//		return checkAndroidRequestPermissions(activity, PERMISSIONS_PHONE_STORAGE_CONTACTS, requestCode);
//	}
//
	
	 /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the Activity has access to all given permissions.
     * Always returns true on platforms below M.
     *
     * @see Activity#checkSelfPermission(String)
     */
//    @SuppressLint("NewApi")
//	public static boolean hasSelfPermission(Activity activity, String[] permissions) {
//        // Below Android M all permissions are granted at install time and are already available.
//        if (!moreThan23()) {
//            return true;
//        }
//
//        // Verify that all required permissions have been granted
//        for (String permission : permissions) {
//            if (ActivityCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
    
    @SuppressLint("NewApi")
//	public static String[] hasSelfPermissionFilter(Activity activity, String[] permissions) {
//		// Below Android M all permissions are granted at install time and are
//		// already available.
//
//		if (!moreThan23()) {
//			return new String[] {};
//		}
//
//		// Verify that all required permissions have been granted
//		ArrayList<String> a = new ArrayList<String>();
//		for (String permission : permissions) {
//			if (ActivityCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED) {
//				if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
//					PL.i("permission shouldShowRequestPermissionRationale false:" + permission);
//					continue;
//				}
//				a.add(permission);
//			}
//		}
//
//		if (a.isEmpty()) {
//			return new String[] {};
//		}
//
//		String[] contents = new String[a.size()];
//		return a.toArray(contents);
//
//	}

    /**
     * Returns true if the Activity has access to a given permission.
     * Always returns true on platforms below M.
     *
     * @see Activity#checkSelfPermission(String)
     */
    public static boolean hasSelfPermission(Context context, String permission) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!moreThan23()) {
            return true;
        }

        return ActivityCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED;
    }

   /* public static boolean isMNC() {
        
         TODO: In the Android M Preview release, checking if the platform is M is done through
         the codename, not the version code. Once the API has been finalised, the following check
         should be used: 
        // return Build.VERSION.SDK_INT == Build.VERSION_CODES.M

        return "MNC".equals(Build.VERSION.CODENAME);
    }*/
	
    public static boolean moreThan23(){
		PL.i("Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);//设备系统api版本
    	if (Build.VERSION.SDK_INT < 23) {
    		return false;
    	}
    	return true;
    }
    
}
