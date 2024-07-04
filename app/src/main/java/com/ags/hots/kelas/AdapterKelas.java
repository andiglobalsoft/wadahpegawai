package com.ags.hots.kelas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ags.hots.R;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
                Intent a = new Intent(context, PendaftaranKelas.class);
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
