package com.example.wuzhihan.finalwork.model;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Wuzhihan on 2017/4/16.
 */

public class SignIn extends BmobObject{
    private String username;
    private String inTime;
    private String IP;
    private String MAC;
    private String YearAndMonth;
    private Date createAt;
    private Integer signState;
    private BmobFile photo;
    private String latitude;
    private String longitude;
    private String remarks;

    public BmobFile getPhoto() {
        return photo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }

    public Integer getSignState() {
        return signState;
    }

    public void setSignState(Integer signState) {
        this.signState = signState;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getYearAndMonth() {
        return YearAndMonth;
    }

    public void setYearAndMonth(String yearAndMonth) {
        YearAndMonth = yearAndMonth;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
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
