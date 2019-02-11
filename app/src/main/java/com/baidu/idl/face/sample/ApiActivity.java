package com.baidu.idl.face.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.idl.facesdk.FaceAuth;
import com.baidu.idl.facesdk.callback.AuthCallback;
import com.baidu.idl.face.sample.common.GlobalSet;
import com.baidu.idl.face.sample.db.DBManager;
import com.baidu.idl.face.sample.listener.OnImportListener;
import com.baidu.idl.face.sample.manager.FaceSDKManager;
import com.baidu.idl.face.sample.manager.ImportFileManager;
import com.baidu.idl.face.sample.ui.IminectPassActivity;
import com.baidu.idl.face.sample.ui.OrbbecMiniPassActivity;
import com.baidu.idl.face.sample.ui.OrbbecProPassActivity;
import com.baidu.idl.face.sample.ui.PassActivity;
import com.baidu.idl.face.sample.utils.FileUtils;
import com.baidu.idl.face.sample.utils.ToastUtils;

import static com.baidu.idl.face.sample.common.GlobalSet.LICENSE_ONLINE;

/**
 * Created by lvqiu on 2019/1/27.
 */

public class ApiActivity extends Activity implements View.OnClickListener,OnImportListener {
    ImportFileManager fileManager;
    FaceAuth faceAuth;
    LinearLayout layout_register,layout_manager,layout_pass,layout_settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.baidu.idl.face.sample.R.layout.activity_api);
        initData();
        initView();
    }

    private void initData() {
        //初始化导入管理器
        fileManager=ImportFileManager.getInstance();
        fileManager.setOnImportListener(this);
        //sdk校验初始化
        faceAuth = new FaceAuth();
        // 建议3288板子flagsThreads设置2,3399板子设置4
        faceAuth.setAnakinThreadsConfigure(2, 0);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == com.baidu.idl.face.sample.R.id.layout_settings) {
            String key=  "DLIX-1WPH-HKD7-0VHX" ;
            initLicenseOnLine(key);
            return;
        }
        if ( GlobalSet.FACE_AUTH_STATUS == 0){
            if (i == com.baidu.idl.face.sample.R.id.layout_register) {
                registerPerson();
            }else if (i == com.baidu.idl.face.sample.R.id.layout_manager) {
            }else if (i == com.baidu.idl.face.sample.R.id.layout_pass) {
                Intent intent=null;
                if (GlobalSet.getLiveStatusValue() == GlobalSet.LIVE_STATUS.RGB_DEPTH) {
                    if (GlobalSet.getStructuredLightValue() == GlobalSet.STRUCTURED_LIGHT.OBI_ASTRA_PRO) {
                        intent = new Intent(this, OrbbecProPassActivity.class);
                    } else if (GlobalSet.getStructuredLightValue() == GlobalSet.STRUCTURED_LIGHT.OBI_ASTRA_MINI) {
                        intent = new Intent(this, OrbbecMiniPassActivity.class);
                    } else if (GlobalSet.getStructuredLightValue() == GlobalSet.STRUCTURED_LIGHT.HUAJIE_AMY_MINI) {
                        intent = new Intent(this, IminectPassActivity.class);
                    }
                } else {
                    intent = new Intent(this, PassActivity.class);
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        }
    }


    /**
     * 在线校验sdk
     * @param key
     */
    private void initLicenseOnLine(final String key) {
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(this, "序列号不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        faceAuth.initLicenseOnLine(this, key, new AuthCallback() {
            @Override
            public void onResponse(final int code, final String response, String licenseKey) {
                if (code == 0) {
                    GlobalSet.FACE_AUTH_STATUS = 0;
                    // 初始化人脸
                    FaceSDKManager.getInstance().initModel(ApiActivity.this);
                    // 初始化数据库
                    DBManager.getInstance().init(getApplicationContext());
                    // 加载feature 内存
                    FaceSDKManager.getInstance().setFeature();
                    GlobalSet.setLicenseOnLineKey(key);
                    GlobalSet.setLicenseStatus(LICENSE_ONLINE);
                    ToastUtils.toast(ApiActivity.this, "校验成功，可以进行其他操作!");
                } else {
                    ToastUtils.toast(ApiActivity.this, code + "  " + response);
                }
            }
        });
    }

    /**
     * 注册人员信息
     */
    public void registerPerson(){
        // 复制assets下文件到sdcard目录
        if (!GlobalSet.getIsImportSample()) {
            FileUtils.copyAssetsFiles2SDCard(this, "ImportSrc",
                    Environment.getExternalStorageDirectory().getPath());
            GlobalSet.setIsImportSample(true);
        }
        fileManager.batchImport();//"/storage/emulated/0/吕秋.jpg","吕秋"

    }

    private void initView() {
        layout_register=findViewById(com.baidu.idl.face.sample.R.id.layout_register);
        layout_manager=findViewById(com.baidu.idl.face.sample.R.id.layout_manager);
        layout_pass=findViewById(com.baidu.idl.face.sample.R.id.layout_pass);
        layout_settings=findViewById(com.baidu.idl.face.sample.R.id.layout_settings);
        layout_register.setOnClickListener(this);
        layout_manager.setOnClickListener(this);
        layout_pass.setOnClickListener(this);
        layout_settings.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileManager.release();
    }

    @Override
    public void startImport() {

    }

    @Override
    public void onImporting(float progress) {

    }

    @Override
    public void endImport(int totalCount, int successCount, int failureCount) {

    }

    @Override
    public void showToastMessage(String message) {
        ToastUtils.toast(this, message);
    }

}
