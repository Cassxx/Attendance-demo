package com.example.wuzhihan.finalwork.model;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by Wuzhihan on 2017/4/17.
 */

public class SignOut extends BmobObject{
    private String username;
    private String outTime;
    private String IP;
    private String MAC;
    private String YearAndMonth;
    private Date createAt;
    private Integer signState;

    public String getOutTime() {
        return outTime;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Integer getSignState() {
        return signState;
    }

    public void setSignState(Integer signState) {
        this.signState = signState;
    }

    public String getYearAndMonth() {
        return YearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        YearAndMonth = yearAndMonth;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
