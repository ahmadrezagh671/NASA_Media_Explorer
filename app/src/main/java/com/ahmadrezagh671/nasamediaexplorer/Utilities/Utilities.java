package com.ahmadrezagh671.nasamediaexplorer.Utilities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Display.setViewHeightAndWidth;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.ahmadrezagh671.nasamediaexplorer.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class Utilities {

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static void setTextUtil(TextView textView, String text,String name,int color,int textLimit){
        Context context = textView.getContext();
        textView.setVisibility(VISIBLE);
        if (text == null || text.isEmpty()) {
            if (name == null || name.isEmpty()){
                textView.setVisibility(GONE);
            }else {
                textView.setText("No "+ name +" found");
                textView.setTextColor(context.getColor(R.color.red));
            }
        }else {
            if (textLimit != -1 && text.length() >= textLimit)
                text = text.substring(0,textLimit);
            textView.setText(text);
            textView.setTextColor(context.getColor(color));
        }
    }
    public static void setTextUtil(TextView textView, String text,String name,int color){
        setTextUtil(textView,text,name,color,-1);
    }

    public static void setImageViewUtil(Activity activity, ImageView imageView, String url, TapListener tapListener){
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.setColorSchemeColors(activity.getColor(R.color.text_color_black));
        circularProgressDrawable.start();

        Glide.with(activity).load(url).fitCenter()
                .placeholder(circularProgressDrawable)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        Drawable drawable = activity.getDrawable(R.drawable.image_failed_to_load);

                        int imageWidth = drawable.getIntrinsicWidth();
                        int imageHeight = drawable.getIntrinsicHeight();

                        setViewHeightAndWidth(imageView,imageHeight,imageWidth,activity,0.4f,true);
                        imageView.setImageResource(R.drawable.image_failed_to_load);
                        return true;
                    }
                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        new Zoomy.Builder(activity)
                                .target(imageView)
                                .enableImmersiveMode(false)
                                .animateZooming(false)
                                .tapListener(tapListener)
                                .register();
                        return false;
                    }
                })
                .into(imageView);
    }
    public static void setImageViewUtil(Activity activity, ImageView imageView, int imageId, TapListener tapListener){
        Drawable drawable = activity.getDrawable(imageId);

        int imageWidth = drawable.getIntrinsicWidth();
        int imageHeight = drawable.getIntrinsicHeight();

        setViewHeightAndWidth(imageView,imageHeight,imageWidth,activity,0.4f,true);

        imageView.setImageResource(imageId);

        /*new Zoomy.Builder(activity)
                .target(imageView)
                .enableImmersiveMode(false)
                .animateZooming(false)
                .tapListener(tapListener)
                .register();*/
    }

/*
    public static int getAppVersion(Context context){
        try{
            return (int) (context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).getLongVersionCode());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
*/

    public static String getAppVersionStr(Context context){
        try{
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static void openLink(String url, Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Unable to open link.", Toast.LENGTH_SHORT).show();
        }
    }


/*
    public static void downloadFile(String url, String fileName, Activity activity){
        Context context = activity.getApplicationContext();
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,getAppName(activity) +"/" + fileName);
        long reference = manager.enqueue(request);

        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
    }
*/

    public static String getAppName(Context context){
        return context.getResources().getString(R.string.app_name);
    }
}
