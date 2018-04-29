package com.happylich.bridge.game.res;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;

import com.happylich.bridge.R;

import java.util.ArrayList;

/**
 * Created by wangt on 2017/11/10.
 */

public class CardImage {
    // 卡牌矩阵
//    Image = BitmapFactory.decodeResource(context.getResources(), CardImage.cardImages[cards.get(i)]);
    public static int[] cardImages = {
            R.drawable.lord_card_club_2,
            R.drawable.lord_card_club_3,
            R.drawable.lord_card_club_4,
            R.drawable.lord_card_club_5,
            R.drawable.lord_card_club_6,
            R.drawable.lord_card_club_7,
            R.drawable.lord_card_club_8,
            R.drawable.lord_card_club_9,
            R.drawable.lord_card_club_10,
            R.drawable.lord_card_club_j,
            R.drawable.lord_card_club_q,
            R.drawable.lord_card_club_k,
            R.drawable.lord_card_club_1,
            R.drawable.lord_card_diamond_2,
            R.drawable.lord_card_diamond_3,
            R.drawable.lord_card_diamond_4,
            R.drawable.lord_card_diamond_5,
            R.drawable.lord_card_diamond_6,
            R.drawable.lord_card_diamond_7,
            R.drawable.lord_card_diamond_8,
            R.drawable.lord_card_diamond_9,
            R.drawable.lord_card_diamond_10,
            R.drawable.lord_card_diamond_j,
            R.drawable.lord_card_diamond_q,
            R.drawable.lord_card_diamond_k,
            R.drawable.lord_card_diamond_1,
            R.drawable.lord_card_heart_2,
            R.drawable.lord_card_heart_3,
            R.drawable.lord_card_heart_4,
            R.drawable.lord_card_heart_5,
            R.drawable.lord_card_heart_6,
            R.drawable.lord_card_heart_7,
            R.drawable.lord_card_heart_8,
            R.drawable.lord_card_heart_9,
            R.drawable.lord_card_heart_10,
            R.drawable.lord_card_heart_j,
            R.drawable.lord_card_heart_q,
            R.drawable.lord_card_heart_k,
            R.drawable.lord_card_heart_1,
            R.drawable.lord_card_spade_2,
            R.drawable.lord_card_spade_3,
            R.drawable.lord_card_spade_4,
            R.drawable.lord_card_spade_5,
            R.drawable.lord_card_spade_6,
            R.drawable.lord_card_spade_7,
            R.drawable.lord_card_spade_8,
            R.drawable.lord_card_spade_9,
            R.drawable.lord_card_spade_10,
            R.drawable.lord_card_spade_j,
            R.drawable.lord_card_spade_q,
            R.drawable.lord_card_spade_k,
            R.drawable.lord_card_spade_1,
    };

    // 叫牌矩阵
    public static int[] callImages = {
            R.drawable.res_1c,
            R.drawable.res_1d,
            R.drawable.res_1h,
            R.drawable.res_1s,
            R.drawable.res_1n,
            R.drawable.res_2c,
            R.drawable.res_2d,
            R.drawable.res_2h,
            R.drawable.res_2s,
            R.drawable.res_2n,
            R.drawable.res_3c,
            R.drawable.res_3d,
            R.drawable.res_3h,
            R.drawable.res_3s,
            R.drawable.res_3n,
            R.drawable.res_4c,
            R.drawable.res_4d,
            R.drawable.res_4h,
            R.drawable.res_4s,
            R.drawable.res_4n,
            R.drawable.res_5c,
            R.drawable.res_5d,
            R.drawable.res_5h,
            R.drawable.res_5s,
            R.drawable.res_5n,
            R.drawable.res_6c,
            R.drawable.res_6d,
            R.drawable.res_6h,
            R.drawable.res_6s,
            R.drawable.res_6n,
            R.drawable.res_7c,
            R.drawable.res_7d,
            R.drawable.res_7h,
            R.drawable.res_7s,
            R.drawable.res_7n,
            R.drawable.res_pass,
    };

    public static int backImage = R.drawable.lord_card_back;
    public static int passImage = R.drawable.pass;

    public static ArrayList<Bitmap> cardBitmapImages = new ArrayList<>();
    public static ArrayList<Bitmap> callBitmapImages = new ArrayList<>();
    public static Bitmap backBitmapImage;
    public static Bitmap passBitmapImage;


    /**
     * 将所有Image加载到内存中
     * @param context
     */
    public static void getResource(Context context) {

        Bitmap image;
        for (int i = 0; i < callImages.length; i++) {
//            image = (Bitmap) context.getResources().getDrawable(callImages[i]);
//            Bitmap bitmap = bitmapDrawable.getBitmap();
            image = decodeSampledBitmapFromResource(context.getResources(), callImages[i], 180, 240);
            callBitmapImages.add(image);
        }
        for (int i = 0; i < cardImages.length; i++) {
            image = decodeSampledBitmapFromResource(context.getResources(), cardImages[i], 180, 180);
            cardBitmapImages.add(image);
        }
        backBitmapImage = decodeSampledBitmapFromResource(context.getResources(), backImage, 180, 180);
        passBitmapImage = decodeSampledBitmapFromResource(context.getResources(), passImage, 720, 134);
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
