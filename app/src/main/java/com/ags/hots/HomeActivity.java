package com.ags.hots;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ags.hots.Transaksi.DaftarTransaksi;
import com.ags.hots.berita.AdapterBerita;
import com.ags.hots.berita.ModelBerita;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.SessionManager;
import com.ags.hots.kantor.DaftarKantor;
import com.ags.hots.kelas.AdapterKelas;
import com.ags.hots.kelas.DaftarKelas;
import com.ags.hots.kelas.ModelKelas;
import com.ags.hots.kelas.ProdukKelas;
import com.ags.hots.pendaftaran.ProfilActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipe_refresh;
    String username, kodeotp;
    SessionManager session;
    //recyclerekstory
    List<ModelKelas> ModelKelas;
    //recyclerekberita
    List<ModelBerita> ModelBerita;
    private ProgressDialog pDialog;
    private AdapterKelas mKelas;
    private AdapterBerita mBerita;

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int i = 0;
            while (i < children.length) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
                i++;
            }
        }

        assert dir != null;
        return dir.delete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //session
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);
        kodeotp = user.get(SessionManager.KEY_EMAIL);
        //swipe
        swipe_refresh = findViewById(R.id.swipe_refresh);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        //recycler kelas
        ModelKelas = new ArrayList<>();
        mKelas = new AdapterKelas(this, ModelKelas);
        RecyclerView recyclerView1 = findViewById(R.id.recycler_kelas);
        LinearLayoutManager kelas
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(kelas);
        recyclerView1.setAdapter(mKelas);
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
        loadIdentitas();
        loadKelas();
        loadBerita();

    }

    private void loadIdentitas() {

        AndroidNetworking.post(Config.host + "HalamanHome.php")
                .addBodyParameter("tag", "detail")
                .addBodyParameter("username", username)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipe_refresh.setRefreshing(false);
                        if (response.optString("remove").equals("N") && response.optString("keanggotaan").equals("Y")) {
                            if (!response.optString("kode_otp").equals(kodeotp)) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                                builder1.setMessage("Akun anda sedang dipakai diperangkat lain");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        "oke",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                logoutUser();

                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            } else {
                                if (!response.optString("versi").equals("1.1")) {
                                    popupDetail();
                                }
                            }


                        } else {
                            Toast.makeText(HomeActivity.this, "Anda sedang dinon-aktifkan admin",
                                    Toast.LENGTH_LONG).show();
                            logoutUser();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        swipe_refresh.setRefreshing(false);

                    }
                });
    }

    private void actionButton() {

        findViewById(R.id.lihatkelas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent intent = new Intent(HomeActivity.this, ProdukKelas.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                try {
                    Intent sendMsg = new Intent(Intent.ACTION_VIEW);
                    String url = "https://api.whatsapp.com/send?phone=" + "+628112672828" + "&text=" + URLEncoder.encode("Hallo saya dari user wadah pegawai", "UTF-8");
                    sendMsg.setPackage("com.whatsapp");
                    sendMsg.setData(Uri.parse(url));
                    if (sendMsg.resolveActivity(getPackageManager()) != null) {
                        startActivity(sendMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.m1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent intent = new Intent(HomeActivity.this, DaftarKelas.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.m2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent intent = new Intent(HomeActivity.this, DaftarTransaksi.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.m4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent intent = new Intent(HomeActivity.this, ProdukKelas.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.m5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent intent = new Intent(HomeActivity.this, DaftarKantor.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.m6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent intent = new Intent(HomeActivity.this, ProfilActivity.class);
                startActivity(intent);
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadIdentitas();
                loadKelas();
                loadBerita();
            }
        });

    }

    private void loadKelas() {

        AndroidNetworking.post(Config.host + "HalamanHome.php")
                .addBodyParameter("username", username)
                .addBodyParameter("tag", "kelas")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        swipe_refresh.setRefreshing(false);
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Tidak ada yang dapat ditampilkan", Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<ModelKelas> items = new Gson().fromJson(response.toString(), new TypeToken<List<ModelKelas>>() {
                        }.getType());

                        // adding items to cart list
                        ModelKelas.clear();
                        ModelKelas.addAll(items);

                        // refreshing recycler view
                        mKelas.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError error) {

                        swipe_refresh.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "Tidak ada kelas" + username, Toast.LENGTH_LONG).show();
                        // handle error
                    }
                });
    }

    private void loadBerita() {

        AndroidNetworking.post(Config.host + "HalamanHome.php")
                .addBodyParameter("username", username)
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

    private void popupDetail() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.updateaplikasi, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView btnfollow = dialogView.findViewById(R.id.update);

        btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.argorudip.lidocom")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.argorudip.lidocom")));
                }
            }
        });


        alertDialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        hideDialog();

    }

    private void logoutUser() {
        clearApplicationData();
        session.logoutUser();
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
