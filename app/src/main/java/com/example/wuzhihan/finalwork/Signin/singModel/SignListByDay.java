package com.example.wuzhihan.finalwork.Signin.singModel;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by pc on 2016/5/26.
 */
public class SignListByDay extends BmobObject{
    private String username;
    private String outTime;
    private String signInTime;
    private String signInType;
    private String signOutType;
    private String signOutTime;
    private String addressid;
    private String remark;
    private String addressName;
    private String addressLongitude;
    private String addressLatitude;
    private String maxDistance;
    private String signType;
    private List<SignImgList> signImgList;
    private String userId;
    public SignListByDay() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getSignInType() {
        return signInType;
    }

    public void setSignInType(String signInType) {
        this.signInType = signInType;
    }

    public String getSignOutType() {
        return signOutType;
    }

    public void setSignOutType(String signOutType) {
        this.signOutType = signOutType;
    }

    public String getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    public String getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(String signOutTime) {
        this.signOutTime = signOutTime;
    }

    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(String addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public String getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(String addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public String getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(String maxDistance) {
        this.maxDistance = maxDistance;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public List<SignImgList> getSignImgList() {
        return signImgList;
    }

    public void setSignImgList(List<SignImgList> signImgList) {
        this.signImgList = signImgList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
