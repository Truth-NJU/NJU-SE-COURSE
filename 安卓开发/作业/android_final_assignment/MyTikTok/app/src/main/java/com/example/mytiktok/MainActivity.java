package com.example.mytiktok;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private VideoListAdapter listAdapter = new VideoListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycleVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        getData();
        recyclerView.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Main", "" + position);
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("feedurl", listAdapter.getVideoList().get(position).feedUrl);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://beiyou.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getVideos().enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                Log.d("retrofitSuccess", response.toString());
                if (response.body() != null) {
                    //System.err.println(response);

                    List<Video> videoList = response.body();

                    Log.d("retrofitSuccess", videoList.toString());

                    if (videoList.size() != 0) {
                        listAdapter.setData(videoList);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                //System.err.println(call.toString());
                Log.d("retrofitFail", t.getMessage());
            }
        });

    }
}