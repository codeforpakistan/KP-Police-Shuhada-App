package com.example.kt.shudaapp.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifiModel {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("sid")
    @Expose
    private String sid;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("sname")
    @Expose
    private String sname;
    @SerializedName("member_id")
    @Expose
    private String member_id;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    @Override
    public String toString() {
        return "VerifiModel{" +
                "success=" + success +
                ", sid='" + sid + '\'' +
                ", path='" + path + '\'' +
                ", sname='" + sname + '\'' +
                ", member_id='" + member_id + '\'' +
                '}';
    }
}
