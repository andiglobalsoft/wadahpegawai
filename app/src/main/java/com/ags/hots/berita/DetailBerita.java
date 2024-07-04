package com.ags.hots.berita;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class DetailBerita extends AppCompatActivity {
    String id_berita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_berita);
        //intent
        Intent intent = getIntent();
        id_berita = intent.getStringExtra("kode");

//        String des= Html.fromHtml().toString();
//        keterangan.setText(Html.fromHtml(des));
        loadBerita();
    }

    private void loadBerita() {

        AndroidNetworking.post(Config.host + "HalamanUtama.php")
                .addBodyParameter("tag", "detailberita")
                .addBodyParameter("id_berita", id_berita)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
//                        swipe_refresh.setRefreshing(false);
//                        keterangan.setText(response.optString("keterangan") + "\n \n" + response.optString("nama_siswa"));
//                        video = response.optString("video");
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
//                        swipe_refresh.setRefreshing(false);

                    }
                });
    }
}
