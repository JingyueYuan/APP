package com.jkf.graduateproject;

import java.io.Serializable;

public class SingleInfom implements Serializable {

    private static final long serialVersionUID = -7000212889670489525L;

    private String crackTime;
    private String crackLength;
    private String crackArea;
    private String crack_maxWidth;
    private String crackPath; //处理之后图片路径
    private String crakOriginalPath;  //原图
    private String kind;  //用于区分大气或者水下
    private String description;   //自己描述的话语
    private String crack_meanWidth;   //平均宽度
    private String crack_realWidth;   //实际宽度

    public SingleInfom() {

    }

    public SingleInfom(String crackTime, String crackLength, String crackPath, String crakOriginalPath, String kind) {
        this.crackTime = crackTime;
        this.crackLength = crackLength;
        this.crackPath = crackPath;
        this.crakOriginalPath = crakOriginalPath;
        this.kind = kind;
    }

    public String getCrack_meanWidth() {
        return crack_meanWidth;
    }

    public void setCrack_meanWidth(String crack_meanWidth) {
        this.crack_meanWidth = crack_meanWidth;
    }

    public String getCrack_realWidth() {
        return crack_realWidth;
    }

    public void setCrack_realWidth(String crack_realWidth) {
        this.crack_realWidth = crack_realWidth;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCrackTime() {
        return crackTime;
    }

    public void setCrackTime(String crackTime) {
        this.crackTime = crackTime;
    }

    public String getCrackLength() {
        return crackLength;
    }

    public void setCrackLength(String crackLength) {
        this.crackLength = crackLength;
    }

    public String getCrackPath() {
        return crackPath;
    }

    public void setCrackPath(String crackPath) {
        this.crackPath = crackPath;
    }

    public String getCrakOriginalPath() {
        return crakOriginalPath;
    }

    public void setCrakOriginalPath(String crakOriginalPath) {
        this.crakOriginalPath = crakOriginalPath;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCrackArea() {
        return crackArea;
    }

    public void setCrackArea(String crackArea) {
        this.crackArea = crackArea;
    }

    public String getCrack_maxWidth() {
        return crack_maxWidth;
    }

    public void setCrack_maxWidth(String crack_maxWidth) {
        this.crack_maxWidth = crack_maxWidth;
    }

}
