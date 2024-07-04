package com.ags.hots.kelas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.DataHelper;
import com.ags.hots.helper.MyGripView;
import com.ags.hots.helper.SessionManager;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class ProdukKelas extends AppCompatActivity {
    public static String username, tag;
    static DataHelper dbcenter;
    public SessionManager session;
    public SwipeRefreshLayout swipe_refresh;
    protected Cursor cursor;
    LinearLayout lini, ly00, ly11;
    EditText pencarian;
    String[] daftar;
    String mode = "1";
    int jumlah1 = 0;
    //recyclerekstory
    List<ModelKelas> ModelKelas2;
    private ShimmerLayout shimmerLayout;
    private MyGripView myGridView;
    private ProgressBar myProgressBar;
    private AdapterKelas mAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, "", tag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_kelas);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);
        dbcenter = new DataHelper(this);
        tag = "kelas.nama_kelas DESC";
        //edit
        pencarian = findViewById(R.id.pencarian);
        //shimmer
        shimmerLayout = findViewById(R.id.shimmer_layout);
        shimmerLayout.startShimmerAnimation();
        //refers
        swipe_refresh = findViewById(R.id.swipe_refresh);
        myGridView = findViewById(R.id.myGridView);
        myProgressBar = findViewById(R.id.myProgressBar);


        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);

        shimmerLayout.startShimmerAnimation();
        ly00.setVisibility(View.VISIBLE);
        ly11.setVisibility(View.GONE);
        findViewById(R.id.grid).setVisibility(View.GONE);
        pencarian.setText("");

        //recycler story
        ModelKelas2 = new ArrayList<>();
        mAdapter = new AdapterKelas(this, ModelKelas2);
        RecyclerView recycler_view = findViewById(R.id.recycler_view2);
        LinearLayoutManager model_buku
                = new LinearLayoutManager(getApplicationContext());
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(model_buku);
        recycler_view.setAdapter(mAdapter);

        new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, "", tag);

        actionGrid();
        actionPencarian();
        actionButton();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void actionPencarian() {
        pencarian.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    jumlah1 = 0;
                    new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, pencarian.getText().toString(), tag);
                    return true;
                }
                return false;
            }
        });

        pencarian.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    pencarian.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_delete, 0);
                } else {
                    pencarian.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });

        pencarian.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (pencarian.getText().length() > 0) {
                    pencarian.setText("");
                    jumlah1 = 0;
                    new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, "", tag);
                }

                return false;
            }
        });

    }

    private void actionGrid() {

        //scrool
        myGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean scrollEnabled;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (myGridView == null || myGridView.getChildCount() == 0) ?
                                0 : myGridView.getChildAt(0).getTop();

                boolean newScrollEnabled =
                        firstVisibleItem == 0 && topRowVerticalPosition >= 0;

                if (null != swipe_refresh && scrollEnabled != newScrollEnabled) {
                    // Start refreshing....
                    swipe_refresh.setEnabled(newScrollEnabled);
                    scrollEnabled = newScrollEnabled;
                }

            }
        });

    }

    private void actionButton() {

        findViewById(R.id.mode).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (mode.equals("1")) {
                    mode = "2";
                    findViewById(R.id.recycler_view2).setVisibility(View.VISIBLE);
                    findViewById(R.id.myGridView).setVisibility(View.GONE);
                } else {
                    mode = "1";
                    findViewById(R.id.recycler_view2).setVisibility(View.GONE);
                    findViewById(R.id.myGridView).setVisibility(View.VISIBLE);
                }

            }
        });

        findViewById(R.id.urutan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUrutan();
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumlah1 = jumlah1 + 8;
                new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, pencarian.getText().toString(), tag);
            }
        });

        findViewById(R.id.fab2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumlah1 = jumlah1 - 8;
                new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, pencarian.getText().toString(), tag);
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //akhir
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shimmerLayout.startShimmerAnimation();
                ly00.setVisibility(View.VISIBLE);
                ly11.setVisibility(View.GONE);
                findViewById(R.id.grid).setVisibility(View.GONE);
                pencarian.setText("");
                new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, "", tag);
            }
        });


    }

    private void selectUrutan() {

        final CharSequence[] items = {"Kelas Terbaru", "Kelas Terlama"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProdukKelas.this);
        builder.setTitle("Urutkan");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                jumlah1 = 0;
                if (items[item].equals("Kelas Terbaru")) {
                    dialog.dismiss();
                    tag = "kelas.id_kelas DESC";
                    pencarian.setText("");
                    new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, "", tag);
                } else if (items[item].equals("Kelas Terlama")) {
                    dialog.dismiss();
                    tag = "kelas.id_kelas ASC";
                    pencarian.setText("");
                    new JSONDownloader(ProdukKelas.this).retrieve(myGridView, myProgressBar, "", tag);
                }
            }
        });
        builder.show();
    }

    public void addName(String id_kelas, String nama_kelas, String foto_kelas) {
        SQLiteDatabase db = dbcenter.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM populer WHERE id_kelas ='" + id_kelas + "'", null);
        daftar = new String[cursor.getCount()];

        if (daftar.length == 0) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(DataHelper.COLUMN_IDKELAS, id_kelas);
            contentValues.put(DataHelper.COLUMN_NAMAKELAS, nama_kelas);
            contentValues.put(DataHelper.COLUMN_FOTOKELAS, foto_kelas);
            contentValues.put(DataHelper.COLUMN_STATKELAS, "0");

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(DataHelper.TABLE_POPULER, null, contentValues);
            if (newRowId != -1) {
                Toast.makeText(getApplicationContext(), "Berhasil ditambah ke daftar disukai", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Data gagal ditambahkan", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Sudah masuk daftar disukai", Toast.LENGTH_SHORT).show();
        }
    }

    public class GridViewAdapter extends BaseAdapter {
        Context c;
        ArrayList<ModelKelas> ModelKelas;

        private GridViewAdapter(Context c, ArrayList<ModelKelas> ModelKelas) {
            this.c = c;
            this.ModelKelas = ModelKelas;
        }

        @Override
        public int getCount() {
            return ModelKelas.size();
        }

        @Override
        public Object getItem(int pos) {
            return ModelKelas.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.row_product, viewGroup, false);
            }
            final ModelKelas pdf = (ModelKelas) this.getItem(pos);
            ImageView gambar = view.findViewById(R.id.imageView);
            TextView text_judul = view.findViewById(R.id.text_judul);
            TextView text_ket = view.findViewById(R.id.text_ket);
            Button pesan = view.findViewById(R.id.lihat);
            Glide.with(ProdukKelas.this)
                    .load(pdf.getFoto())
                    .error(R.drawable.logo2)
                    .into(gambar);
            text_judul.setText(pdf.getNama_kelas());
            text_ket.setText(pdf.getDeskripsi());


            if (pdf.getStatus().equals("1")) {
                pesan.setText("TERDAFTAR");
                pesan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProdukKelas.this);
                        builder1.setMessage("Anda sudah terdaftar");
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
            } else {
                pesan.setText("DAFTAR");
                pesan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent a = new Intent(c, PendaftaranKelas.class);
                        a.putExtra("kode", pdf.getId_kelas());
                        c.startActivity(a);
                    }
                });
            }

            return view;
        }
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
                    .inflate(R.layout.model_kelas2, parent, false);

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

            if (item.getStatus().equals("1")) {
                holder.daftar.setText("TERDAFTAR");
                holder.daftar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProdukKelas.this);
                        builder1.setMessage("Anda sudah terdaftar");
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
            } else {
                holder.daftar.setText("DAFTAR");
                holder.daftar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent a = new Intent(context, PendaftaranKelas.class);
                        a.putExtra("kode", item.getId_kelas());
                        context.startActivity(a);
                    }
                });
            }

            Glide.with(context)
                    .load(item.getFoto())
                    .error(R.drawable.logo2)
                    .into(holder.foto);

//             addName(item.id_kelas, item.nama_kelas, item.getFoto());

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

    private class JSONDownloader {

        private final Context c;
        GridViewAdapter adapter;

        private JSONDownloader(Context c) {
            this.c = c;
        }

        private void retrieve(final GridView gv, final ProgressBar myProgressBar, String data, String tag) {
            swipe_refresh.setRefreshing(false);
            final ArrayList<ModelKelas> ModelKelas = new ArrayList<>();
            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);

            //loading
            AndroidNetworking.post(Config.host + "HalamanKelas.php")
                    .addBodyParameter("tag", "produk")
                    .addBodyParameter("username", username)
                    .addBodyParameter("jumlah1", String.valueOf(jumlah1))
                    .addBodyParameter("key", data)
                    .addBodyParameter("filter", tag)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            if (response == null) {
                                Toast.makeText(getApplicationContext(), "Kosong", Toast.LENGTH_LONG).show();
                                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                                shimmerLayout.startShimmerAnimation();
                                ly00.setVisibility(View.GONE);
                                findViewById(R.id.ly11).setVisibility(View.VISIBLE);
                                findViewById(R.id.grid).setVisibility(View.GONE);

                                return;
                            } else {
                                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                                shimmerLayout.stopShimmerAnimation();
                                ly00.setVisibility(View.GONE);
                                findViewById(R.id.ly11).setVisibility(View.GONE);
                                findViewById(R.id.grid).setVisibility(View.VISIBLE);
                            }


                            List<ModelKelas> items = new Gson().fromJson(response.toString(), new TypeToken<List<ModelKelas>>() {
                            }.getType());
                            ModelKelas.clear();
                            ModelKelas.addAll(items);

                            adapter = new GridViewAdapter(c, ModelKelas);
                            gv.setAdapter(adapter);
                            myProgressBar.setVisibility(View.GONE);
                            // refreshing recycler view
                            ModelKelas2.clear();
                            ModelKelas2.addAll(items);
                            mAdapter.notifyDataSetChanged();
                            int count = mAdapter.getItemCount();
                            if (count == 0) {
                                shimmerLayout.startShimmerAnimation();
                                ly00.setVisibility(View.GONE);
                                ly11.setVisibility(View.VISIBLE);
                                findViewById(R.id.recycler_view2).setVisibility(View.GONE);
                                findViewById(R.id.myGridView).setVisibility(View.GONE);
                            } else {
                                shimmerLayout.stopShimmerAnimation();
                                ly00.setVisibility(View.GONE);
                                ly11.setVisibility(View.GONE);

                                mode = "1";
                                findViewById(R.id.recycler_view2).setVisibility(View.GONE);
                                findViewById(R.id.myGridView).setVisibility(View.VISIBLE);

                            }
                            if (jumlah1 < 8) {
                                if (adapter.getCount() < 8) {
                                    findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                                    findViewById(R.id.fab2).setVisibility(View.INVISIBLE);
                                } else {
                                    findViewById(R.id.fab).setVisibility(View.GONE);
                                    findViewById(R.id.fab2).setVisibility(View.INVISIBLE);
                                }

                            } else {
                                if (adapter.getCount() < 8) {
                                    findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                                    findViewById(R.id.fab2).setVisibility(View.VISIBLE);
                                } else {
                                    findViewById(R.id.fab).setVisibility(View.GONE);
                                    findViewById(R.id.fab2).setVisibility(View.VISIBLE);
                                }
                            }

                            findViewById(R.id.mainScrollView).

                                    scrollTo(0, 0);

                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            swipe_refresh.setRefreshing(false);
                            Snackbar snackbar = Snackbar
                                    .make(lini, "Sinyal anda lemah ..", Snackbar.LENGTH_LONG);

                            snackbar.show();

                        }
                    });
        }

    }
}