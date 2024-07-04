package com.ags.hots.kelas;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.SessionManager;
import com.ags.hots.pendaftaran.VerifikasiKtp;
import com.ags.hots.review.AdapterReview;
import com.ags.hots.review.ModelReview;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PendaftaranKelas extends AppCompatActivity {
    private static final int SELECT_FILE1 = 1;
    private static final int SELECT_FILE2 = 2;
    String selectedPath1 = "NONE";
    String selectedPath2 = "NONE";
    String username, kdkelas;
    SessionManager session;
    Uri selectedImageUri;
    EditText t1, t2, jurusan;
    Button b1, b2;
    TextView b3;
    List<ModelReview> ModelReview;
    private ProgressDialog pDialog;
    private AdapterReview mReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran_kelas);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //intent
        Intent intent = getIntent();
        kdkelas = intent.getStringExtra("kode");
        //session
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //deklarasi
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        jurusan = findViewById(R.id.jurusan);
        b1 = findViewById(R.id.Button1);
        b2 = findViewById(R.id.Button2);
        b3 = findViewById(R.id.upload);
        //recycler review
        ModelReview = new ArrayList<>();
        mReview = new AdapterReview(this, ModelReview);
        RecyclerView recyclerView2 = findViewById(R.id.recycler_review);
        LinearLayoutManager review
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(review);
        recyclerView2.setAdapter(mReview);
        loadReview();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(SELECT_FILE1);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(SELECT_FILE2);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedPath1.trim().equalsIgnoreCase("NONE")) && !(selectedPath2.trim().equalsIgnoreCase("NONE"))) {
                    doFileUpload();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select two files to upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestStoragePermission();

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void loadReview() {
        AndroidNetworking.post(Config.host + "HalamanUtama.php")
                .addBodyParameter("username", "")
                .addBodyParameter("tag", "review")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Tidak ada yang dapat ditampilkan", Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<ModelReview> items = new Gson().fromJson(response.toString(), new TypeToken<List<ModelReview>>() {
                        }.getType());

                        // adding items to cart list
                        ModelReview.clear();
                        ModelReview.addAll(items);

                        // refreshing recycler view
                        mReview.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getApplicationContext(), "tidak ada review", Toast.LENGTH_LONG).show();
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
            } else {
                selectedPath2 = getPath(selectedImageUri);
                t2.setText(selectedPath2);
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
        File file2 = new File(selectedPath2);
        pDialog.setMessage("Memuat Tampilan . .");
        showDialog();
        AndroidNetworking.upload("http://wadahpegawai.andiglobalsoft.com/json_desember/HalamanBerkas.php")
                .addMultipartFile("uploadedfile1", file1)
                .addMultipartFile("uploadedfile2", file2)
                .addMultipartParameter("jurusan", jurusan.getText().toString().trim())
                .addMultipartParameter("kdkelas", kdkelas)
                .addMultipartParameter("username", username)
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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(PendaftaranKelas.this);
                            builder1.setMessage("Yei, anda sudah terdaftar");
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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(PendaftaranKelas.this);
                            builder1.setMessage("Kamu belum verifikasi KTP");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Verifikasi",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent a = new Intent(PendaftaranKelas.this, VerifikasiKtp.class);
                                            startActivity(a);
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(PendaftaranKelas.this);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(PendaftaranKelas.this);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}