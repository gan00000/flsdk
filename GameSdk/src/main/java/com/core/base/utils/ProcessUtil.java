package com.core.base.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessUtil {

    public static String ping(String ip){

        ip = ip.replace("https://", "").replace("http://", "");
        //向 IP 地址 发送最多 5 个 Ping 包，但整个测试过程最多只等待 10 秒钟，10 秒后立即停止并显示结果。
        return exec("ping -c 5 -w 10 " + ip);
    }

    public static String exec(String cmd){
        try {
            PL.i("start cmd:" + cmd);
            if (TextUtils.isEmpty(cmd)){
                return "";
            }
            Process process = Runtime.getRuntime().exec(cmd);
            if (process != null){
                InputStream is = process.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while (null != (line = bufferedReader.readLine())){
                    sb.append(line);
                    sb.append("\n");
                }
                PL.i("end cmd result:" + sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
