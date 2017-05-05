package com.example.wuzhihan.finalwork.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by Wuzhihan on 2017/4/13.
 */

public class Person extends BmobUser {

    private String imei;
    private String imsi;

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
