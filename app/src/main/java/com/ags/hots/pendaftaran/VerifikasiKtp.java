package com.ags.hots.pendaftaran;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class VerifikasiKtp extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final int SELECT_FILE1 = 1;
    public ProgressDialog pDialog;
    public SessionManager session;
    TextView textinput_counter, t1;
    EditText input_nama, input_nik, input_ttl;
    String selectedPath1 = "NONE", username, tanggal;
    Uri selectedImageUri;
    private RelativeLayout lini;
    private Button btn_simpan;
    private TextView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_ktp);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //session id
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);

        //Textview
        textinput_counter = findViewById(R.id.textinput_counter);
        t1 = findViewById(R.id.t1);
        close = findViewById(R.id.close);
        //liniear
        lini = findViewById(R.id.lini);
        //button
        btn_simpan = findViewById(R.id.btn_simpan);
        //dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        //edittext
        input_nama = findViewById(R.id.input_nama);
        input_nama.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        input_nik = findViewById(R.id.input_nik);
        input_ttl = findViewById(R.id.input_alamat);
        input_ttl.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        //tambahan
        actionButton();
        requestStoragePermission();
        checkAndroidVersion();

    }

    private void actionButton() {
        input_ttl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowKalender();
            }
        });

        findViewById(R.id.Button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(SELECT_FILE1);
            }
        });

        input_nik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textinput_counter.setText(16 - s.toString().length() + "/16");

            }
        });
        //menuPendaftaran
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_nik.getText().toString().length() < 16) {
                    if (input_nik.getText().toString().length() == 0) {
                        Snackbar snackbar = Snackbar
                                .make(lini, "NIK tidak boleh kosong", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        input_nik.setError("NIK tidak boleh kosong");
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(lini, "NIK tidak boleh kurang dari 16 angka", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        input_nik.setError("NIK tidak boleh kurang dari 16 angka");

                    }
                } else if (input_nama.getText().toString().length() == 0) {
                    Snackbar snackbar = Snackbar
                            .make(lini, "Nama tidak boleh kosong", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    input_nama.setError("Nama tidak boleh kosong");
                } else if (input_ttl.getText().toString().length() == 0) {
                    Snackbar snackbar = Snackbar
                            .make(lini, "Tanggal lahir tidak boleh kosong", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    input_ttl.setError("Tanggal lahir tidak boleh kosong");
                } else if (t1.getText().toString().length() == 0) {
                    Snackbar snackbar = Snackbar
                            .make(lini, "Foto Harus Dirubah", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    doFileUpload();
                }

            }
        });
    }

    public void openGallery(int req_code) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file to upload "), req_code);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (requestCode == SELECT_FILE1) {
                selectedPath1 = getPath(selectedImageUri);
                t1.setText(selectedPath1);
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void doFileUpload() {

        File file1 = new File(selectedPath1);
        pDialog.setMessage("Loading..");
        showDialog();
        AndroidNetworking.upload(Config.host + "HalamanLogin.php")
                .addMultipartFile("uploadedfile1", file1)
                .addMultipartParameter("nik", input_nik.getText().toString().trim())
                .addMultipartParameter("nama", input_nama.getText().toString().trim())
                .addMultipartParameter("ttl", tanggal)
                .addMultipartParameter("username", username)
                .addMultipartParameter("tag", "editanggota")
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int sukses = response.optInt("success");
                        if (sukses == 1) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifikasiKtp.this);
                            builder1.setMessage("KTP ANDA TELAH DIVERIFIKASI");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "oke",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else if (sukses == 2) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifikasiKtp.this);
                            builder1.setMessage("Foto gagal diupload");
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
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifikasiKtp.this);
                            builder1.setMessage("Coba lagi yah, yang tadi gagal");
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
                        hideDialog();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifikasiKtp.this);
                        builder1.setMessage("Yah koneksi anda sedang buruk");
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
                        hideDialog();
                    }
                });
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 123) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
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

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();

        }
    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(VerifikasiKtp.this,
                Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(VerifikasiKtp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(VerifikasiKtp.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(VerifikasiKtp.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void ShowKalender() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                String date = "You picked the following date range: \n"
                        + "From " + dayOfMonth + "/" + (++monthOfYear)
                        + "/" + year;

                tanggal = year + "-" + (monthOfYear) + "-" + dayOfMonth;
                input_ttl.setText("Tanggal : " + dayOfMonth + "-" + (monthOfYear) + "-" + year);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}
