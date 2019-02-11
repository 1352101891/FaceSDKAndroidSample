package com.baidu.idl.face.sample.model;

import java.io.Serializable;

/**
 * Created by lvqiu on 2019/1/27.
 */

public class BatchBean implements Serializable{
    public String batchPicDir;
    public String txtDir;
    public String[] picFiles;

    public BatchBean(String batchPicDir, String txtDir, String[] picFiles) {
        this.batchPicDir = batchPicDir;
        this.txtDir = txtDir;
        this.picFiles = picFiles;
    }

    public String getBatchPicDir() {
        return batchPicDir;
    }

    public void setBatchPicDir(String batchPicDir) {
        this.batchPicDir = batchPicDir;
    }

    public String getTxtDir() {
        return txtDir;
    }

    public void setTxtDir(String txtDir) {
        this.txtDir = txtDir;
    }

    public String[] getPicFiles() {
        return picFiles;
    }

    public void setPicFiles(String[] picFiles) {
        this.picFiles = picFiles;
    }
}
