package com.baidu.idl.face.sample.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Loggger {
    private static Context context;
    public static void init(Context mcontext){
        context=mcontext;
    }

    private static String getRootDir(Context context)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // 优先获取SD卡根目录[/storage/sdcard0]
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else
        {
            // 应用缓存目录[/data/data/应用包名/cache]
            return context.getCacheDir().getAbsolutePath();
        }
    }

    private static boolean OutLogFile = true;	// 输出log信息到文件

    /** 输出log信息到文件中 */
    public static String p(String TAG, String info)
    {
        if (OutLogFile)
        {
            String crashPath = getRootDir(context);

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(new Date());

            DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
            String time = formatter2.format(new Date()) + "  ";

            String fileName = context.getPackageName()+"-" + date + ".txt";
            info = "\r\n" + time +"----"+TAG+":"+ info;

            try
            {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    // String path = "/sdcard/com.hm.pay.demo/crash/";
                    File dir = new File(crashPath);
                    if (!dir.exists())
                    {
                        dir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(crashPath + File.separator+fileName, true);
                    fos.write(info.getBytes());
                    fos.close();
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "an error occured while writing file log...", e);
            }
            return crashPath + fileName;
        } else return "";
    }

}