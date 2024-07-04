package com.ags.hots.pendaftaran;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.SessionManager;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class ProfilActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipe_refresh;
    String username, kodeotp;
    TextView text_nama, text_alamat, text_telp, text_email, text_saldo;
    SessionManager session;

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
        setContentView(R.layout.activity_profil);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);
        kodeotp = user.get(SessionManager.KEY_EMAIL);
        //
        text_nama = findViewById(R.id.text_nama);
        text_alamat = findViewById(R.id.text_alamat);
        text_telp = findViewById(R.id.text_telp);
        text_email = findViewById(R.id.text_email);
        text_saldo = findViewById(R.id.text_saldo);
        //swipe
        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadIdentitas();
            }
        });

        findViewById(R.id.keluar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfilActivity.this);
                builder1.setMessage("Anda yakin akan keluar?");
                builder1.setCancelable(false);
                builder1.setPositiveButton(
                        "ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                logoutUser();
                            }
                        });
                builder1.setNegativeButton(
                        "tidak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        findViewById(R.id.tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ProfilActivity.this, VerifikasiKtp.class);
                startActivity(a);
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadIdentitas();
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
                            if (response.optString("kode_otp").equals(kodeotp)) {
                                text_nama.setText(response.optString("nama_siswa"));
                                text_alamat.setText(response.optString("tgl_lahir"));
                                text_telp.setText(response.optString("telepon_siswa"));
                                text_email.setText(response.optString("email_siswa"));
                                text_saldo.setText(response.optString("saldo"));

                                if (response.optString("tgl_lahir").equals("null")) {
                                    findViewById(R.id.tambah).setVisibility(View.VISIBLE);
                                } else {
                                    findViewById(R.id.tambah).setVisibility(View.GONE);
                                }

                            } else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfilActivity.this);
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
                            }


                        } else {
                            Toast.makeText(ProfilActivity.this, "Anda sedang dinon-aktifkan admin",
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
}
