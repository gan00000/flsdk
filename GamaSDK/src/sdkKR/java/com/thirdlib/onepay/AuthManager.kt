package com.thirdlib.onepay

import android.app.Activity
import com.gaa.sdk.auth.GaaSignInClient
import com.gaa.sdk.auth.OnAuthListener
import com.gaa.sdk.base.ResultListener

class AuthManager(private val activity: Activity) {
    private var client: GaaSignInClient = GaaSignInClient.getClient(activity)

    fun silentSignLogin(listener: OnAuthListener) {
        client.silentSignIn(listener)
    }

    /**
     * 원스토어 로그인을 진행합니다.
     * 로그인이 되어 있지 않다면, 원스토어로 연결되어 로그인을 유도합니다.
     * 继续登录 One Store。
    如果您尚未登录，您将连接到 One Store 并提示您登录
     * @param listener
     */
    fun launchSignInFlow(listener: OnAuthListener) {
        client.launchSignInFlow(activity, listener)
    }

    /**
     * 원스토어 버전이 낮을 경우 업데이트/설치를 시도합니다.
     * 如果 One Store 版本较低，请尝试更新/安装。
     * @param listener
     */
    fun launchUpdateOrInstall(listener: ResultListener) {
        client.launchUpdateOrInstallFlow(activity, listener)
    }
}