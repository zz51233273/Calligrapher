package com.example.hasee.bluecalligrapher.bean;

/**
 * Created by hasee on 2018/3/30.
 */

public class User{
    //手机号
    private String phoneNumber;
    //用户名
    private String username;
    //密码
    private String password;
    //头像
    private byte[] head_img;
    //性别
    private String sex;
    //生日
    private String birth;
    //签到
    private boolean check_in;
    //任务完成情况
    private int mission1,mission2,mission3,mission4,mission5;
    //日练点
    private int day_score;
    //周练点
    private int week_score;
    //经验值
    private int exp;

    public User(){

    }
    public User(String phoneNumber,String username,String password){
        this.phoneNumber=phoneNumber;
        this.username=username;
        this.password=password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setUserName(String username){
        this.username=username;
    }
    public String getUserName(){
        return username;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getPassword(){
        return password;
    }

    public byte[] getHead_img() {
        return head_img;
    }

    public void setHead_img(byte[] head_img) {
        this.head_img = head_img;
    }

    public boolean isCheck_in() {
        return check_in;
    }

    public void setCheck_in(boolean check_in) {
        this.check_in = check_in;
    }

    public int getMission1() {
        return mission1;
    }

    public void setMission1(int mission1) {
        this.mission1 = mission1;
    }

    public int getMission2() {
        return mission2;
    }

    public void setMission2(int mission2) {
        this.mission2 = mission2;
    }

    public int getMission3() {
        return mission3;
    }

    public void setMission3(int mission3) {
        this.mission3 = mission3;
    }

    public int getMission4() {
        return mission4;
    }

    public void setMission4(int mission4) {
        this.mission4 = mission4;
    }

    public int getMission5() {
        return mission5;
    }

    public void setMission5(int mission5) {
        this.mission5 = mission5;
    }

    public int getDay_score() {
        return day_score;
    }

    public void setDay_score(int day_score) {
        this.day_score = day_score;
    }

    public int getWeek_score() {
        return week_score;
    }

    public void setWeek_score(int week_score) {
        this.week_score = week_score;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
}
