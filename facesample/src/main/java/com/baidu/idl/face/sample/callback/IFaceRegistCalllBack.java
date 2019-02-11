package com.baidu.idl.face.sample.callback;

import android.graphics.Bitmap;

import com.baidu.idl.face.sample.model.LivenessModel;

public interface IFaceRegistCalllBack {
    /**
     * 注册结果回调
     * @param code 0：注册成功 1:注册超时
     * @param livenessModel
     */
    public void onRegistCallBack(int code, LivenessModel livenessModel, Bitmap cropBitmap);
}
