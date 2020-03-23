package com.example.hasee.bluecalligrapher.utils;


import com.example.hasee.bluecalligrapher.main.MainActivity;

/**
 * Created by hasee on 2018/5/6.
 */

public class StringUtil {

    //判断是否为中文字符
    public static boolean isChinese(char c) {
        return c >= 0x4E00 &&  c <= 0x9FA5;// 根据字节码判断
    }

    public static boolean isPhonenumber(String text){
        int len=text.length();
        if(len!=11)return false;
        for(int i=0;i<len;i++){
            char c=text.charAt(i);
            if(c<'0'||c>'9')
                return false;
        }
        return true;
    }

    //判断是否为管理员
    public static boolean isManager(){
        if(null!= MainActivity.user&&(MainActivity.user.getPhoneNumber().equals("18913778019")||MainActivity.user.getPhoneNumber().equals("15369509561")||MainActivity.user.getPhoneNumber().equals("15931284022"))){
            return true;
        }
        return false;
    }

    //判断是否有敏感字词
    public static boolean check(String text){
        if(text.contains("傻逼")
                ||text.contains("傻屌")
                ||text.contains("操你妈")
                ||text.contains("草你妈")
                ||text.contains("你妈逼")){
            return false;
        }
        return true;
    }
}
