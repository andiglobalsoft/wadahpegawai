package com.ags.hots.pendaftaran;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ags.hots.MainActivity;
import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.SessionManager;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    SessionManager session;
    private String username, kodeotp2;
    private Button btnLogin;
    private TextView btnForgotPass;
    private EditText inputEmail, inputPassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEmail = findViewById(R.id.lTextEmail);
        inputPassword = findViewById(R.id.lTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgotPass = findViewById(R.id.btnForgotPassword);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // session manager
        session = new SessionManager(getApplicationContext());
        // check user is already logged in
        if (session.isLoggedIn()) {
            // get user data from session
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

        init();
    }


    private void init() {
        // Login button Click Event
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent a = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(a);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_login = inputEmail.getText().toString().trim();
                String pass_login = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email_login.isEmpty() && !pass_login.isEmpty()) {
                    loginProcess(email_login, pass_login);
                } else {
                    // Prompt user to enter credentials
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("Mohon masukan semua data");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "oke",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

            }
        });

        // Forgot Password Dialog
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordDialog();
            }
        });
    }

    private void forgotPasswordDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.reset_password, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Lupa Password");
        dialogBuilder.setCancelable(false);

        final TextInputLayout mEditEmail = dialogView.findViewById(R.id.editEmail);

        dialogBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();

        mEditEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEditEmail.getEditText().getText().length() > 0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(false);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = mEditEmail.getEditText().getText().toString();

                        if (!email.isEmpty()) {
                            if (Config.isValidEmailAddress(email)) {
                                resetPassword(email);
                                dialog.dismiss();
                            } else {
                                android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(LoginActivity.this);
                                builder1.setMessage("Email yang anda masukan salah");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "oke",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                android.app.AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        } else {
                            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(LoginActivity.this);
                            builder1.setMessage("Masukan semua data");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "oke",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            android.app.AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                    }
                });
            }
        });

        alertDialog.show();
    }

    private void resetPassword(final String email) {

        AndroidNetworking.post(Config.host + "HalamanLogin.php")
                .addBodyParameter("tag", "forgot_pass")
                .addBodyParameter("email_login", email)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(LoginActivity.this);
                        builder1.setMessage(response.optString("response"));
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "oke",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        android.app.AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        builder1.setMessage("Sinyal anda lemah");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "oke",
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

    private void loginProcess(final String email_login, final String pass_login) {
        // Tag used to cancel the request
        pDialog.setMessage("Logging in ...");
        showDialog();
        AndroidNetworking.post(Config.host + "HalamanLogin.php")
                .addBodyParameter("tag", "login")
                .addBodyParameter("username", email_login)
                .addBodyParameter("pass_login", pass_login)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int status = response.optInt("response");
                        if (status == 1) {
                            if (response.optString("status_rmv_anggota").equals("N")) {
                                username = response.optString("username_siswa");
                                kodeotp2 = response.optString("kode_otp");

                                session.createLoginSession(username, kodeotp2);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            } else {

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                builder1.setMessage("Anda telah dinon-aktifkan");
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
                            }
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            builder1.setMessage("Kegagalan sistem..");
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
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        builder1.setMessage("Sinyal anda lemah");
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

