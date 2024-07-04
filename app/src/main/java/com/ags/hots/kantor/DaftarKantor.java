package com.ags.hots.kantor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ags.hots.R;
import com.ags.hots.helper.Config;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class DaftarKantor extends AppCompatActivity {
    public SwipeRefreshLayout swipe_refresh;
    //recyclerekberita
    List<ModelKantor> ModelKantor;
    LinearLayout ly0, ly1, ly2;
    int jumlah1 = 0;
    EditText pencarian;
    private AdapterKantor mBerita;
    private ShimmerLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_kantor);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        ly0 = findViewById(R.id.ly0);
        ly1 = findViewById(R.id.ly1);
        ly2 = findViewById(R.id.ly2);
        //shimmer
        shimmerLayout = findViewById(R.id.shimmer_layout);
        shimmerLayout.startShimmerAnimation();
        //recycler berita
        ModelKantor = new ArrayList<>();
        mBerita = new AdapterKantor(this, ModelKantor);
        RecyclerView recyclerView3 = findViewById(R.id.recycler_berita);
        LinearLayoutManager berita
                = new LinearLayoutManager(getApplicationContext());
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(berita);
        recyclerView3.setNestedScrollingEnabled(true);
        recyclerView3.setAdapter(mBerita);

        pencarian = findViewById(R.id.pencarian);

        actionButton();
        actionPencarian();
        loadKantor();

    }

    private void actionButton() {

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumlah1 = jumlah1 + 8;
                loadKantor();
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shimmerLayout.startShimmerAnimation();
                ly0.setVisibility(View.VISIBLE);
                ly1.setVisibility(View.GONE);
                ly2.setVisibility(View.GONE);
                loadKantor();
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void actionPencarian() {
        pencarian.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    jumlah1 = 0;
                    loadKantor();
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
                    loadKantor();
                }

                return false;
            }
        });

    }

    private void loadKantor() {

        AndroidNetworking.post(Config.host + "HalamanKantor.php")
                .addBodyParameter("key", pencarian.getText().toString().trim())
                .addBodyParameter("jumlah", String.valueOf(jumlah1))
                .addBodyParameter("tag", "kantor")
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
                        List<ModelKantor> items = new Gson().fromJson(response.toString(), new TypeToken<List<ModelKantor>>() {
                        }.getType());

                        // adding items to cart list
                        ModelKantor.clear();
                        ModelKantor.addAll(items);

                        // refreshing recycler view
                        mBerita.notifyDataSetChanged();
                        int count = mBerita.getItemCount();
                        if (count == 0) {
                            shimmerLayout.stopShimmerAnimation();
                            ly0.setVisibility(View.GONE);
                            ly1.setVisibility(View.GONE);
                            ly2.setVisibility(View.VISIBLE);
                            findViewById(R.id.detail).setVisibility(View.GONE);
                        } else {
                            shimmerLayout.stopShimmerAnimation();
                            ly0.setVisibility(View.GONE);
                            ly1.setVisibility(View.VISIBLE);
                            ly2.setVisibility(View.GONE);
                            if (count <= 8) {
                                findViewById(R.id.detail).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.detail).setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        swipe_refresh.setRefreshing(false);
                        // handle error
                    }
                });
    }
}
