package com.example.wuzhihan.finalwork.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Wuzhihan on 2017/5/3.
 */

public class PhotoUrl extends BmobObject {
    private String URL;
    private String Time;

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
