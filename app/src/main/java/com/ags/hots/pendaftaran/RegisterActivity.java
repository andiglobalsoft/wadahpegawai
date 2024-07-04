package com.ags.hots.pendaftaran;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    public ProgressDialog pDialog;
    EditText kodereferal, username, nama, email, telepon, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //progress
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        //edittext
        kodereferal = findViewById(R.id.kodereferal);
        username = findViewById(R.id.username);
        nama = findViewById(R.id.nama);
        email = findViewById(R.id.email);
        telepon = findViewById(R.id.telepon);
        password = findViewById(R.id.password);

        actionButton();
    }

    private void actionButton() {
        findViewById(R.id.checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    findViewById(R.id.tk).setVisibility(View.VISIBLE);
                    kodereferal.setText("");
                } else {
                    findViewById(R.id.tk).setVisibility(View.GONE);
                    kodereferal.setText("0");
                }
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent a = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(a);
            }
        });

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText().toString().length() == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Username tidak boleh kosong");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    username.setError("Username tidak boleh kosong");
                } else if (nama.getText().toString().length() == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Nama tidak boleh kosong");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    nama.setError("Nama tidak boleh kosong");
                } else if (telepon.getText().toString().length() == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Telepon tidak boleh kosong");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    telepon.setError("Telepon tidak boleh kosong");
                } else if (email.getText().toString().length() == 0 && Config.isValidEmailAddress(email.getText().toString())) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Email tidak boleh kosong");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    email.setError("Email tidak boleh kosong");
                } else if (password.getText().toString().length() == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Kata sandi tidak boleh kosong");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    password.setError("Kata sandi tidak boleh kosong");
                } else if (kodereferal.getText().toString().length() == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Kode referal tidak boleh kosong");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    kodereferal.setError("Kode referal tidak boleh kosong");
                } else {

                    pDialog.setMessage("Loading..");
                    showDialog();
                    saveData();

                }

            }
        });
    }

    private void saveData() {
        //menampilkan progress dialog
        pDialog.setMessage("Loading..");
        showDialog();
        AndroidNetworking.post(Config.host + "HalamanLogin.php")
                .addBodyParameter("tag", "simpananggota")
                .addBodyParameter("username", username.getText().toString().trim())
                .addBodyParameter("nama", nama.getText().toString().trim())
                .addBodyParameter("email", email.getText().toString())
                .addBodyParameter("telepon", telepon.getText().toString())
                .addBodyParameter("katasandi", password.getText().toString())
                .addBodyParameter("kodereferal", kodereferal.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        hideDialog();
                        switch (response.optString("success")) {
                            case "1": {

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                                builder1.setMessage("Yeii, kamu sudah terdaftar !!!");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Oke",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                pDialog.setMessage("Memuat Tampilan . .");
                                                showDialog();
                                                Intent a = new Intent(RegisterActivity.this, LoginActivity.class);
                                                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(a);
                                                finish();

                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                                break;
                            }
                            case "2": {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                                builder1.setMessage("Data gagal dikirim");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Oke",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                                break;
                            }
                            case "3": {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                                builder1.setMessage("Email sudah terdaftar didatabase :(");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Oke",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                                break;

                            }
                            case "4": {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(RegisterActivity.this);
                                builder2.setMessage("Username sudah ada :(");
                                builder2.setCancelable(true);

                                builder2.setPositiveButton(
                                        "Oke",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                            }
                                        });

                                AlertDialog alert12 = builder2.create();
                                alert12.show();
                                break;

                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        hideDialog();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                        builder1.setMessage("Server sedang gangguan");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Oke",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        hideDialog();
    }

    private void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}
