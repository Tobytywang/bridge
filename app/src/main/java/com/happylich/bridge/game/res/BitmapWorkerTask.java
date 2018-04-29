package com.happylich.bridge.game.res;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by lich on 2018/4/27.
 */

public class BitmapWorkerTask{
//public class BitmapWorkerTask extends AsyncTask {
//    private final WeakReference<ImageView> imageViewWeakReference;
//
//    private int data = 0;
//
//    public BitmapWorkerTask(ImageView imageView) {
//        imageViewWeakReference = new WeakReference<ImageView>(imageView);
//    }
//
//    @Override
//    protected Bitmap doInBackground(Params... params) {
//        data = params[0];
//        return decodeSampleBitmapFromResource(getResource(), data, 100, 100);
//    }
//
//    @Override
//    protected void onPostExecute(Bitmap bitmap) {
//        if (imageViewWeakReference != null && bitmap != null) {
//            final ImageView imageView = imageViewWeakReference.get();
//            if (imageView != null) {
//                imageView.setImageBitmap(bitmap);
//            }
//        }
//    }
}
