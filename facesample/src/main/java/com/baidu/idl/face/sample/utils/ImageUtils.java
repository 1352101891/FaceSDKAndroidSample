package com.baidu.idl.face.sample.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static Bitmap getBitmapFromSDCard(String sdCardPath) {
        FileInputStream fis = null;
        Bitmap bm = null;
        try {
            fis = new FileInputStream(sdCardPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8; // 图片的长宽都是原来的1/8
            BufferedInputStream bis = new BufferedInputStream(fis);
            bm = BitmapFactory.decodeStream(bis, null, options);
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 裁剪无黑边图片
     */
    public static Bitmap noBlackBoundImgCrop(int[] landmarks, int rows, int cols, int[] argbImg) {
        // 定义最大最小横纵坐标均为第一个值
        int right = landmarks[0];   // x方向上最大值
        int bottom = landmarks[1];  // y方向上最大值
        int left = landmarks[0];    // x方向上最小值
        int top = landmarks[1];     // y方向上最小值

        for (int i = 0; i < landmarks.length / 2; i++) {
            if (landmarks[2 * i] > right) {
                right = landmarks[2 * i];
            }
            if (landmarks[2 * i] < left) {
                left = landmarks[2 * i];
            }
            if (landmarks[2 * i + 1] > bottom) {
                bottom = landmarks[2 * i + 1];
            }
            if (landmarks[2 * i + 1] < top) {
                top = landmarks[2 * i + 1];
            }
        }

        Log.e(TAG, "left:" + left + ",right:" + right + ",top:" + top
                + ",bottom:" + bottom);

        int faceWidth = right - left;   // 获取人脸宽度
        Log.e(TAG, "faceWidth:" + faceWidth);

        int minDistance;
        int cropWidth;
        int cropHeight;

        // 不满足倍数抠图,按最窄边抠图
        if (left < faceWidth || top < faceWidth || (cols - right) < faceWidth
                || (rows - bottom) < faceWidth) {
            if (left <= (cols - right)) {
                if (top <= (rows - bottom)) {
                    if (left <= top) {
                        minDistance = left;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    } else {
                        minDistance = top;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    }
                } else {
                    if (left <= (rows - bottom)) {
                        minDistance = left;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    } else {
                        minDistance = rows - bottom;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    }
                }
            } else {
                if (top <= (rows - bottom)) {
                    if ((cols - right) <= top) {
                        minDistance = cols - right;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    } else {
                        minDistance = top;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    }
                } else {
                    if ((cols - right) <= (rows - bottom)) {
                        minDistance = cols - right;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    } else {
                        minDistance = rows - bottom;
                        cropWidth = 2 * minDistance + faceWidth;
                        cropHeight = 2 * minDistance + faceWidth;
                    }
                }
            }
        } else {  // 满足倍数抠图,按人脸3倍抠图
            cropWidth = 3 * faceWidth;
            cropHeight = 3 * faceWidth;
            minDistance = faceWidth;
        }
        Log.e(TAG, "cropWidth:" + cropWidth + ",cropHeight:" + cropHeight);

        if (cropWidth <= 0 || cropHeight <= 0) {
            return null;
        }

        int[] newImgPixs = new int[cropWidth * cropHeight];
        int newIndex = 0;
        for (int i = top - minDistance; i < top - minDistance + cropHeight; i++) {
            for (int j = left - minDistance; j < left - minDistance + cropWidth; j++) {
                if (newIndex == cropWidth * cropHeight) {
                    newIndex = cropWidth * cropHeight - 1;
                }
                if (cols * i + j >= cols * rows) {
                    newImgPixs[newIndex] = argbImg[cols * rows - 1];
                } else {
                    newImgPixs[newIndex] = argbImg[cols * i + j];
                }
                newIndex++;
            }
        }

        Bitmap verBmap = Bitmap.createBitmap(cropWidth, cropHeight, Bitmap.Config.ARGB_8888);
        verBmap.setPixels(newImgPixs, 0, cropWidth, 0, 0, cropWidth, cropHeight);
        return verBmap;
    }

    public static void resize(Bitmap bitmap, File outputFile, int maxWidth, int maxHeight) {
        try {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 图片大于最大高宽，按大的值缩放
            if (bitmapWidth > maxHeight || bitmapHeight > maxWidth) {
                float widthScale = maxWidth * 1.0f / bitmapWidth;
                float heightScale = maxHeight * 1.0f / bitmapHeight;

                float scale = Math.min(widthScale, heightScale);
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            }

            // save image
            FileOutputStream out = new FileOutputStream(outputFile);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap RGB2Bitmap(byte[] bytes, int width, int height) {
        // use Bitmap.Config.ARGB_8888 instead of type is OK
        Bitmap stitchBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        byte[] rgba = new byte[width * height * 4];
        for (int i = 0; i < width * height; i++) {
            byte b1 = bytes[i * 3 + 0];
            byte b2 = bytes[i * 3 + 1];
            byte b3 = bytes[i * 3 + 2];
            // set value
            rgba[i * 4 + 0] = b1;
            rgba[i * 4 + 1] = b2;
            rgba[i * 4 + 2] = b3;
            rgba[i * 4 + 3] = (byte) 255;
        }
        stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(rgba));
        return stitchBmp;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 图片按比例大小压缩方法
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap getimage(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        int rate=1;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        while (w*h>500*500){
            w= (w/2);
            h= (h/2);
            rate=rate*2;
        }

        if (rate <= 0)
            rate = 1;
        newOpts.inSampleSize = rate;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        return BitmapFactory.decodeFile(srcPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
    }

}
