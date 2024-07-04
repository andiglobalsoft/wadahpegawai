package com.ags.hots.Transaksi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.ags.hots.helper.SessionManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class DaftarTransaksi extends AppCompatActivity {
    public ProgressDialog pDialog;
    public SwipeRefreshLayout swipe_refresh;
    ProgressBar myProgressBar;
    SessionManager session;
    String data = "", username;
    LinearLayout ly11, ly00;
    EditText pencarian;
    RecyclerView recyclerView;
    List<ModelTransaksi> ModelTransaksi;
    int jumlah1 = 0;
    private ShimmerLayout shimmerLayout;
    private AdapterData mAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        private boolean scrollEnabled;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            final int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            int topRowVerticalPosition =
                    recyclerView.getChildCount() == 0 ?
                            0 : recyclerView.getChildAt(0).getTop();

            boolean newScrollEnabled =
                    firstVisibleItemPosition == 0 && topRowVerticalPosition >= 0;

            if (null != swipe_refresh && scrollEnabled != newScrollEnabled) {
                // Start refreshing....
                swipe_refresh.setEnabled(newScrollEnabled);
                scrollEnabled = newScrollEnabled;
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        hideDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_transaksi);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        username = user.get(SessionManager.KEY_NAME);

        //shimmer
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //refers
        swipe_refresh = findViewById(R.id.swipe_refresh);
        shimmerLayout = findViewById(R.id.shimmer_layout);
        shimmerLayout.startShimmerAnimation();
        myProgressBar = findViewById(R.id.myProgressBar);

        pencarian = findViewById(R.id.pencarian);

        ly11 = findViewById(R.id.ly11);
        ly00 = findViewById(R.id.ly00);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recycler_view);
        ModelTransaksi = new ArrayList<>();
        mAdapter = new AdapterData(this, ModelTransaksi);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.setNestedScrollingEnabled(false);

        actionButton();
        actionPencarian();
        loadData(jumlah1);


    }

    @SuppressLint("ClickableViewAccessibility")
    private void actionPencarian() {
        pencarian.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    data = pencarian.getText().toString();
                    jumlah1 = 0;
                    loadData(jumlah1);
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
                    data = "";
                    jumlah1 = 0;
                    loadData(jumlah1);
                }

                return false;
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

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data = "";
                pencarian.setText("");
                shimmerLayout.startShimmerAnimation();
                ly00.setVisibility(View.VISIBLE);
                ly11.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                loadData(jumlah1);
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumlah1 = jumlah1 + 8;
                loadData(jumlah1);
            }
        });

        findViewById(R.id.fab2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumlah1 = jumlah1 - 8;
                loadData(jumlah1);
            }
        });
    }

    private void loadData(final Integer jumlah1) {

        swipe_refresh.setRefreshing(false);
        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(Config.host + "HalamanTransaksi.php")
                .addBodyParameter("tag", "list")
                .addBodyParameter("username", username)
                .addBodyParameter("jumlah1", String.valueOf(jumlah1))
                .addBodyParameter("key", data)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        myProgressBar.setVisibility(View.GONE);
                        List<ModelTransaksi> items = new Gson().fromJson(response.toString(), new TypeToken<List<ModelTransaksi>>() {
                        }.getType());

                        // adding items to cart list
                        ModelTransaksi.clear();
                        ModelTransaksi.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        findViewById(R.id.mainScrollView).scrollTo(0, 0);
                        int count = mAdapter.getItemCount();
                        if (count == 0) {
                            shimmerLayout.startShimmerAnimation();
                            ly00.setVisibility(View.GONE);
                            ly11.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            shimmerLayout.stopShimmerAnimation();
                            ly00.setVisibility(View.GONE);
                            ly11.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                        if (jumlah1 <= 6) {
                            if (count <= 6) {
                                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                            } else {
                                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                            }
                            findViewById(R.id.fab2).setVisibility(View.INVISIBLE);
                        } else {
                            if (count <= 6) {
                                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                            } else {
                                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                            }
                            findViewById(R.id.fab2).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        hideDialog();
                        // handle error
                        myProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "3" + username, Toast.LENGTH_LONG).show();
                    }
                });
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

    public class AdapterData extends RecyclerView.Adapter<AdapterData.MyViewHolder> {
        List<ModelTransaksi> ModelTransaksi;
        private Context context;

        AdapterData(Context context, List<ModelTransaksi> ModelTransaksi) {
            this.context = context;
            this.ModelTransaksi = ModelTransaksi;
        }

        @NonNull
        @Override
        public AdapterData.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.model_transaksi, parent, false);

            return new AdapterData.MyViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final AdapterData.MyViewHolder holder, final int position) {
            final ModelTransaksi item = ModelTransaksi.get(position);
            holder.nama.setText(item.getNama_kelas());
            holder.keterangan.setText(item.getDeskripsi());
            if (item.getHarga().equals("M")) {
                holder.status.setText("Menunggu");
            } else if (item.getHarga().equals("Y")) {
                holder.status.setText("Terverifikasi");
            } else {
                holder.status.setText("DiTolak");
            }


            Glide.with(context)
                    .load(item.getFoto())
                    .error(R.drawable.logo)
                    .into(holder.foto);


        }

        @Override
        public int getItemCount() {
            return ModelTransaksi.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView nama, keterangan, status;
            ImageView foto;
            CardView cardview;

            MyViewHolder(View view) {
                super(view);
                nama = view.findViewById(R.id.nama);
                keterangan = view.findViewById(R.id.keterangan);
                status = view.findViewById(R.id.status);
                foto = view.findViewById(R.id.foto);
                cardview = view.findViewById(R.id.cardview);
            }
        }

    }


}


