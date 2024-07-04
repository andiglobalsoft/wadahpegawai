package com.ags.hots;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ags.hots.berita.ModelBerita;
import com.ags.hots.berita.AdapterBerita;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.SessionManager;
import com.ags.hots.kelas.ModelKelas;
import com.ags.hots.pendaftaran.LoginActivity;
import com.ags.hots.pendaftaran.RegisterActivity;
import com.ags.hots.review.AdapterReview;
import com.ags.hots.review.ModelReview;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public SwipeRefreshLayout swipe_refresh;
    String username;
    //recyclerekstory
    List<ModelKelas> ModelKelas;
    //recyclerekstory
    List<ModelReview> ModelReview;
    //recyclerekberita
    List<ModelBerita> ModelBerita;
    SessionManager session;
    private AdapterKelas mKelas;
    private AdapterReview mReview;
    private AdapterBerita mBerita;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // session manager
        session = new SessionManager(getApplicationContext());
        // check user is already logged in
        if (session.isLoggedIn()) {
            // get user data from session
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
        //swipe
        swipe_refresh = findViewById(R.id.swipe_refresh);
        //recycler kelas
        ModelKelas = new ArrayList<>();
        mKelas = new AdapterKelas(this, ModelKelas);
        RecyclerView recyclerView1 = findViewById(R.id.recycler_kelas);
        LinearLayoutManager kelas
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(kelas);
        recyclerView1.setAdapter(mKelas);
        //recycler review
        ModelReview = new ArrayList<>();
        mReview = new AdapterReview(this, ModelReview);
        RecyclerView recyclerView2 = findViewById(R.id.recycler_review);
        LinearLayoutManager review
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(review);
        recyclerView2.setAdapter(mReview);
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
        loadKelas();
        loadReview();
        loadBerita();
    }

    private void actionButton() {

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadKelas();
                loadReview();
                loadBerita();
            }
        });

        findViewById(R.id.lihatkelas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent a = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(a);
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent a = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(a);
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog.setMessage("Memuat Tampilan . .");
                showDialog();
                Intent a = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(a);
            }
        });

    }

    private void loadKelas() {

        AndroidNetworking.post(Config.host + "HalamanUtama.php")
                .addBodyParameter("username", username)
                .addBodyParameter("tag", "kelas")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        hideDialog();
                        swipe_refresh.setRefreshing(false);
                        if (response == null) {
                            findViewById(R.id.sk).setVisibility(View.GONE);
                            return;
                        } else {
                            findViewById(R.id.sk).setVisibility(View.VISIBLE);
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
//                        hideDialog();
                        swipe_refresh.setRefreshing(false);
                        // handle error
                    }
                });
    }

    private void loadReview() {
        AndroidNetworking.post(Config.host + "HalamanUtama.php")
                .addBodyParameter("username", username)
                .addBodyParameter("tag", "review")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            findViewById(R.id.ss).setVisibility(View.GONE);
                            return;
                        } else {
                            findViewById(R.id.ss).setVisibility(View.VISIBLE);
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
                    }
                });
    }

    private void loadBerita() {
        AndroidNetworking.post(Config.host + "HalamanUtama.php")
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

    @Override
    protected void onStop() {
        super.onStop();
        hideDialog();

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public class AdapterKelas extends RecyclerView.Adapter<AdapterKelas.MyViewHolder> {
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        private Context context;
        private List<ModelKelas> ModelKelas;

        public AdapterKelas(Context context, List<ModelKelas> ModelKelas) {
            this.context = context;
            this.ModelKelas = ModelKelas;
        }

        @Override
        public AdapterKelas.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.model_kelas, parent, false);

            return new AdapterKelas.MyViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(AdapterKelas.MyViewHolder holder, final int position) {
            final ModelKelas item = ModelKelas.get(position);
            if (item.getStatus().equals("1")) {
                holder.judulharga.setVisibility(View.GONE);
                holder.harga.setVisibility(View.GONE);
            }
            holder.nama_kelas.setText(item.getNama_kelas());
            holder.deskripsi.setText(item.getDeskripsi());
            holder.harga.setText("Rp." + rupiahFormat.format(Double.parseDouble(item.getHarga())));
            holder.daftar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a = new Intent(context, LoginActivity.class);
                    context.startActivity(a);
                }
            });

            Glide.with(context)
                    .load(item.getFoto())
                    .error(R.drawable.logo2)
                    .into(holder.foto);

        }

        @Override
        public int getItemCount() {
            return ModelKelas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView nama_kelas, deskripsi, harga, daftar, judulharga;
            ImageView foto;

            MyViewHolder(View view) {
                super(view);
                nama_kelas = view.findViewById(R.id.nama_kelas);
                judulharga = view.findViewById(R.id.judulharga);
                deskripsi = view.findViewById(R.id.deskripsi);
                harga = view.findViewById(R.id.harga);
                daftar = view.findViewById(R.id.daftar);
                foto = view.findViewById(R.id.foto);
            }
        }

    }

}
