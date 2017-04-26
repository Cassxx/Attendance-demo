package com.example.wuzhihan.finalwork.Signin.singModel;

/**
 * Created by pc on 2016/5/26.
 */
public class SignListByMonth {
    private String SignDate;
    private String CheckDate;
    private String SignTime;
    private Integer signStatus;
    private String username;

    public SignListByMonth() {
    }

    public String getCheckDate() {
        return CheckDate;
    }

    public void setCheckDate(String checkDate) {
        CheckDate = checkDate;
    }

    public String getSignTime() {
        return SignTime;
    }

    public void setSignTime(String signTime) {
        SignTime = signTime;
    }

    public String getSignDate() {
        return SignDate;
    }

    public void setSignDate(String signDate) {
        SignDate = signDate;
    }

//    public String getOutTime() {
//        return outTime;
//    }
//
//    public void setOutTime(String outTime) {
//        this.outTime = outTime;
//    }


    public Integer getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(Integer signStatus) {
        this.signStatus = signStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
