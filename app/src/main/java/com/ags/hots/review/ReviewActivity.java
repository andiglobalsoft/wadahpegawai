package com.ags.hots.review;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ags.hots.R;
import com.ags.hots.VideoActivity;
import com.ags.hots.helper.Config;

import org.json.JSONObject;

public class ReviewActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipe_refresh;
    String idreview, video;
    TextView keterangan;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent intent = getIntent();
        idreview = intent.getStringExtra("kode");
        swipe_refresh = findViewById(R.id.swipe_refresh);
        keterangan = findViewById(R.id.keterangan);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        loadReview();
        actionButton();
    }

    private void actionButton() {
        findViewById(R.id.gotovideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                if (video.isEmpty() || video.equals("null")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ReviewActivity.this);
                    builder1.setMessage("Video kosong");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    hideDialog();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    Intent a = new Intent(ReviewActivity.this, VideoActivity.class);
                    a.putExtra("kode", video);
                    startActivity(a);
                }
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadReview();
            }
        });
    }

    private void loadReview() {

        AndroidNetworking.post(Config.host + "HalamanUtama.php")
                .addBodyParameter("tag", "detailreview")
                .addBodyParameter("id_review", idreview)
                .addBodyParameter("username", "")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        swipe_refresh.setRefreshing(false);
                        keterangan.setText(response.optString("keterangan") + "\n \n" + response.optString("nama_siswa"));
                        video = response.optString("video");
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        swipe_refresh.setRefreshing(false);

                    }
                });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideDialog();

    }

}
