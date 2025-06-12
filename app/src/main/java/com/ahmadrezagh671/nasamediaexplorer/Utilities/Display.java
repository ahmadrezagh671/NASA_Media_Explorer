package com.ahmadrezagh671.nasamediaexplorer.Utilities;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class Display {

    private static DisplayMetrics getDisplayMetrics(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int getDisplayHeight(Activity activity){
        return getDisplayMetrics(activity).heightPixels;
    }
    public static int getDisplayWidth(Activity activity){
        return getDisplayMetrics(activity).widthPixels;
    }

    public static void setViewHeightAndWidth(View imageView, int height, int width, Activity activity, float maxHeightPercentage, boolean fillWidth){
        int displayHeight = getDisplayHeight(activity);
        int maxHeight = (int) (displayHeight*maxHeightPercentage);
        int displayWidth = getDisplayWidth(activity);

        ViewGroup.LayoutParams params = imageView.getLayoutParams();

        float ratio = (float) width / displayWidth;
        int final_height = (int) (height / ratio);
        int finalwidth = displayWidth;

        if (final_height > maxHeight){
            if (!fillWidth){
                float ratio2 = (float) final_height / maxHeight;
                finalwidth = (int) (finalwidth / ratio2);
            }
            final_height = maxHeight;
        }

        params.height = final_height;
        params.width = finalwidth;

        imageView.setLayoutParams(params);
    }

    public static void setViewFullscreen(View imageView, Activity activity){
        int displayHeight = getDisplayHeight(activity);
        int displayWidth = getDisplayWidth(activity);

        ViewGroup.LayoutParams params = imageView.getLayoutParams();

        params.height = displayHeight;
        params.width = displayWidth;

        imageView.setLayoutParams(params);
    }

    public static void enterFullScreenMode(AppCompatActivity appCompatActivity) {
        // Hide the action bar if present
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().hide();
        }

        // Enable immersive full-screen mode
        appCompatActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        // Set the activity to fullscreen
        appCompatActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void exitFullScreenMode(AppCompatActivity appCompatActivity) {
        // Show the action bar if present
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().show();
        }

        // Exit immersive full-screen mode
        appCompatActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        // Exit the activity to fullscreen
        appCompatActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
