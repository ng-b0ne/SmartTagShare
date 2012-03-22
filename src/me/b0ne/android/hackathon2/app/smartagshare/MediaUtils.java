package me.b0ne.android.hackathon2.app.smartagshare;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import com.aioisystems.imaging.DisplayPainter;

public class MediaUtils {

    /**
     * スマートタグのためのBitmapを作成
     * 
     * @param bitmap
     * @return newBitmap
     */
    public static Bitmap editBitmapForTag(Bitmap bitmap) {
        Bitmap newBitmap = bitmap;
        DisplayPainter painter = new DisplayPainter();
        painter.putImage(newBitmap, 0, 0, true);
        newBitmap.recycle();
        newBitmap = painter.getPreviewImage();
        return newBitmap;
    }

    /**
     * UriからBitmapを取得
     * 
     * @param context
     * @param contentUri
     * @return bitmap
     */
    public static Bitmap loadBitmapFromuri(Context context, Uri contentUri) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getContentResolver().openInputStream(contentUri);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Bitmapサイズ修正
     * 
     * @param src
     * @param width
     * @param height
     * @return result
     */
    public static Bitmap resizeBitamp(Bitmap src, int width, int height,
            boolean autoRotate) {

        int srcWidth = src.getWidth(); // 元画像のwidth
        int srcHeight = src.getHeight(); // 元画像のheight

        // 画面サイズを取得する
        Matrix matrix = new Matrix();

        float widthScale = (float)width / srcWidth;
        float heightScale = (float)height / srcHeight;
        if (widthScale > heightScale) { //縦の場合
            heightScale = (float)height / srcWidth;
            matrix.postScale(heightScale, heightScale);
        } else { //横の場合
            matrix.postScale(widthScale, widthScale);
        }

        // 回転
        if (autoRotate && (srcHeight > srcWidth && width > height)
                || (srcWidth > srcHeight && height > width)) {
            matrix.postRotate(-90);
        }

        // リサイズ
        Bitmap result = Bitmap.createBitmap(
                src, 0, 0,
                src.getWidth(), src.getHeight(),
                matrix, true);

        return result;
    }

}
