package com.ahmadrezagh671.nasamediaexplorer.Adapters;

import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Display.setViewHeightAndWidth;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Utilities.setImageViewUtil;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Utilities.setTextUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.ahmadrezagh671.nasamediaexplorer.ItemViewerActivity;
import com.ahmadrezagh671.nasamediaexplorer.Models.Item;
import com.ahmadrezagh671.nasamediaexplorer.R;
import com.google.gson.Gson;

import java.util.List;

public class AdapterItems extends RecyclerView.Adapter<AdapterItems.ViewHolder> {

    private static final String TAG = "AdapterItems";
    List<Item> items;
    Context context;
    Activity activity;

    public AdapterItems(List<Item> items, Context context, Activity activity) {
        this.items = items;
        this.context = context;
        this.activity = activity;
    }



    @NonNull
    @Override
    public AdapterItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItems.ViewHolder holder, int position) {
        Item item = items.get(position);
        Log.i(TAG, "AdapterItems onBindViewHolder: "+ item.toString());

        holder.imageViewClick = v -> openItemViewerActivity(item);
        holder.view.setOnClickListener(v -> openItemViewerActivity(item));

        Zoomy.unregister(holder.imageView);

        if (item.getPreviewLink() != null) {
            setViewHeightAndWidth(holder.imageView,item.getPreviewLink().getHeight(),item.getPreviewLink().getWidth(),activity,0.5f,true);
            setImageViewUtil(activity, holder.imageView, item.getPreviewLink().getHref(), holder.imageViewClick);
        }else {
            if (item.getMedia_type().equals("audio"))
                setImageViewUtil(activity,holder.imageView,R.drawable.image_audio,holder.imageViewClick);
            else
                setImageViewUtil(activity,holder.imageView,R.drawable.image_no_image,holder.imageViewClick);
        }

        setTextUtil(holder.titleTV,item.getTitle(),"title",R.color.text_color_black,100);
        setTextUtil(holder.locationTV,item.getLocation(),"location",R.color.text_color_gray);
        setTextUtil(holder.dateTV,item.getDate_created(),"date",R.color.text_color_gray);
        setTextUtil(holder.mediaTypeTV,item.getMedia_type().toUpperCase(),"media",R.color.text_color_gray);
        setTextUtil(holder.descriptionTV,item.getDescription(),null,R.color.text_color_black,500);
        setTextUtil(holder.keywordsTV,item.getKeywordsString(),null,R.color.blue,300);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTV, locationTV, dateTV,mediaTypeTV, descriptionTV, keywordsTV;
        TapListener imageViewClick;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            imageView = view.findViewById(R.id.imageView);
            titleTV = view.findViewById(R.id.titleTV);
            locationTV = view.findViewById(R.id.locationTV);
            dateTV = view.findViewById(R.id.dateTV);
            mediaTypeTV = view.findViewById(R.id.mediaTypeTV);
            descriptionTV = view.findViewById(R.id.descriptionTV);
            keywordsTV = view.findViewById(R.id.keywordsTV);
        }
    }

    private void openItemViewerActivity(Item item){
        String data = new Gson().toJson(item);
        Log.i(TAG, "openItemViewerActivity: for: " + data);
        activity.startActivity(new Intent(activity, ItemViewerActivity.class)
                .putExtra("data",data)
        );
    }
}
