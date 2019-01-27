package com.baidu.idl.sample.model;

import java.io.Serializable;

/**
 * Created by lvqiu on 2019/1/27.
 */

public class SingleBean implements Serializable{
    public String PicDir;
    public String personname;

    public SingleBean(String picDir, String personname) {
        PicDir = picDir;
        this.personname = personname;
    }

    public String getPicDir() {
        return PicDir;
    }

    public void setPicDir(String picDir) {
        PicDir = picDir;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }
}
