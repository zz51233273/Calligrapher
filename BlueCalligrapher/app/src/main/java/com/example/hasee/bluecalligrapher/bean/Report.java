package com.example.hasee.bluecalligrapher.bean;

/**
 * Created by hasee on 2018/5/21.
 */

public class Report {
    String reporter;    //举报人
    String reason;      //举报原因
    String time;        //举报时间
    int dyId;        //举报动态的id

    public Report(String reporter, String reason, String time, int dyId) {
        this.reporter = reporter;
        this.reason = reason;
        this.time = time;
        this.dyId = dyId;
    }

    public int getDyId() {
        return dyId;
    }

    public void setDyId(int dyId) {
        this.dyId = dyId;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
