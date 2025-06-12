package com.ahmadrezagh671.nasamediaexplorer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.UrlApiUtil.getApiUrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ahmadrezagh671.nasamediaexplorer.Adapters.AdapterItems;
import com.ahmadrezagh671.nasamediaexplorer.Models.Item;
import com.ahmadrezagh671.nasamediaexplorer.Models.ItemData;
import com.ahmadrezagh671.nasamediaexplorer.Models.ItemLink;
import com.ahmadrezagh671.nasamediaexplorer.Settings.AboutUsActivity;
import com.ahmadrezagh671.nasamediaexplorer.Utilities.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";

    RequestQueue requestQueue;

    RecyclerView recyclerView;
    AdapterItems adapterItems;

    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;

    View filtersLayout;
    Button[] filterButtons = new Button[3]; // imageFilterButton, videoFilterButton, audioFilterButton
    boolean[] filterStates = new boolean[3]; // isImageOn, isVideoOn, isAudioOn

    Button tryAgainButton;
    TextView errorTV;

    List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        items = new ArrayList<>();

        tryAgainButton = findViewById(R.id.tryAgainButton);
        tryAgainButton.setOnClickListener(v -> reload());

        errorTV = findViewById(R.id.errorTV);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        filtersLayout = findViewById(R.id.filtersLayout);
        filterButtons[0] = findViewById(R.id.imageFilterButton);
        filterButtons[1] = findViewById(R.id.videoFilterButton);
        filterButtons[2] = findViewById(R.id.audioFilterButton);
        filterButtons[0].setOnClickListener(this::filterClick);
        filterButtons[1].setOnClickListener(this::filterClick);
        filterButtons[2].setOnClickListener(this::filterClick);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final int[] state = new int[1];

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                state[0] = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if(firstVisibleItem == 0){
                    topItemsVisible(true);
                } else if (dy > 50){
                    if (state[0] == 0 || state[0] == 2){
                        topItemsVisible(false);
                    }
                }else if (dy < -160){
                    if (state[0] == 0 || state[0] == 2){
                        topItemsVisible(true);
                    }
                }
            }
        });

        requestPostsFromApi("");
    }



    private void requestPostsFromApi(String search) {
        refreshing(true);

        if (search == null) search = "";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getApiUrl(search,filterStates), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArrayItems = response.getJSONObject("collection").getJSONArray("items");

                    Gson gson = new Gson();

                    items.clear();

                    for (int i = 0; i < jsonArrayItems.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArrayItems.getJSONObject(i);

                            JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
                            ItemData tempItemData = gson.fromJson(data.toString(), ItemData.class);

                            List<ItemLink> tempItemLinks;
                            if (tempItemData.getMedia_type().equals("audio")){
                                tempItemLinks = null;
                            }else {
                                JSONArray links = jsonObject.getJSONArray("links");
                                tempItemLinks = gson.fromJson(links.toString(), new TypeToken<List<ItemLink>>(){}.getType());
                            }

                            String href = jsonObject.getString("href");

                            Item item = new Item(tempItemData, tempItemLinks,href);

                            items.add(item);
                        }catch (JSONException e) {
                            Log.e(TAG, "JSONException: Item " + i + " " + e.getMessage());
                        }
                    }

                    if (items.isEmpty()) {
                        errorTV.setText("Nothing Found");
                        errorTV.setVisibility(VISIBLE);
                    }

                    if(adapterItems == null) {
                        adapterItems = new AdapterItems(items, MainActivity.this, MainActivity.this);
                        recyclerView.setAdapter(adapterItems);
                    }else {
                        recyclerView.scrollToPosition(0);
                        adapterItems.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    //Log.e(TAG, "JSONException: " + e.getMessage());
                    //Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                    if (items.isEmpty()) {
                        errorTV.setText("Something is wrong");
                        errorTV.setVisibility(VISIBLE);
                    }
                }
                refreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage());
                if(adapterItems != null) {
                    items.clear();
                    adapterItems.notifyDataSetChanged();
                }

                refreshing(false);

                tryAgainButton.setVisibility(VISIBLE);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void refreshing(boolean state){
        swipeRefreshLayout.setRefreshing(state);
    }

    private void reload(){
        requestPostsFromApi(getSearchViewQuery());
        tryAgainButton.setVisibility(GONE);
        errorTV.setVisibility(GONE);
    }

    private void topItemsVisible(boolean b) {
        if (b){
            searchView.setVisibility(VISIBLE);
        }else {
            searchView.setVisibility(GONE);
            filtersLayout.setVisibility(GONE);
        }
    }

    public String getSearchViewQuery(){
        return searchView.getQuery().toString().replaceAll("&","");
    }

    @Override
    public void onRefresh() {
        reload();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        reload();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            reload();
        }
        return false;
    }

    public void optionsIB(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.filters){
                    filtersLayout.setVisibility(VISIBLE);
                    searchView.setVisibility(VISIBLE);
                }else if (id == R.id.refresh) {
                    reload();
                }else if (id == R.id.about){
                    startActivity(new Intent(MainActivity.this, AboutUsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                return true;
            }
        });

        popup.show();
    }

    private void filterClick(View view) {
        for (int i = 0; i < filterButtons.length; i++) {
            if (view == filterButtons[i]){
                filterStates[i] = !filterStates[i];
                filterButtons[i].setBackground(filterStates[i] ? getDrawable(R.drawable.shape_border_gradient) : getDrawable(R.drawable.shape_border));
            }else {
                filterStates[i] = false;
                filterButtons[i].setBackground(getDrawable(R.drawable.shape_border));
            }
        }
        reload();
    }
}