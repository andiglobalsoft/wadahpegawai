package com.ags.hots.kelas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ags.hots.R;
import com.ags.hots.berita.AdapterBerita;
import com.ags.hots.berita.ModelBerita;
import com.ags.hots.helper.Config;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MenuKelas extends AppCompatActivity {
    //recyclerekberita
    List<ModelBerita> ModelBerita;
    private AdapterBerita mBerita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_kelas);

        //recycler berita
        ModelBerita = new ArrayList<>();
        mBerita = new AdapterBerita(this, ModelBerita);
        RecyclerView recyclerView3 = findViewById(R.id.recycler_berita);
        LinearLayoutManager berita
                = new LinearLayoutManager(getApplicationContext());
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(berita);
        recyclerView3.setNestedScrollingEnabled(true);
        recyclerView3.setAdapter(mBerita);

        actionButton();
        loadBerita();
    }

    private void loadBerita() {

        AndroidNetworking.post(Config.host + "HalamanHome.php")
                .addBodyParameter("username", "")
                .addBodyParameter("tag", "berita")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Tidak ada yang dapat ditampilkan", Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<ModelBerita> items = new Gson().fromJson(response.toString(), new TypeToken<List<ModelBerita>>() {
                        }.getType());

                        // adding items to cart list
                        ModelBerita.clear();
                        ModelBerita.addAll(items);

                        // refreshing recycler view
                        mBerita.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private void actionButton() {

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.m1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDetail("TKP", "(Test Karakteristik Pribadi)");
            }
        });
        findViewById(R.id.m2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDetail("TIU", "(Test Intelegensia Umum)");
            }
        });
        findViewById(R.id.m3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDetail("TWK", "(Test Wawasan Kebangsaan)");
            }
        });
    }

    private void popupDetail(String jdul, String alamat) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MenuKelas.this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.popup_menu, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView nama = dialogView.findViewById(R.id.nama);
        TextView email = dialogView.findViewById(R.id.email);
        TextView txtclose = dialogView.findViewById(R.id.txtclose);
        LinearLayout hapusku = dialogView.findViewById(R.id.hapusku);
        LinearLayout riwataku = dialogView.findViewById(R.id.riwataku);
        LinearLayout detailkus = dialogView.findViewById(R.id.detailkus);
        nama.setText(jdul);
        email.setText(alamat);

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
}
