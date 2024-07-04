package com.ags.hots.review;

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

import java.util.List;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.MyViewHolder> {
    private Context context;
    private List<ModelReview> ModelReview;

    public AdapterReview(Context context, List<ModelReview> ModelReview) {
        this.context = context;
        this.ModelReview = ModelReview;
    }

    @Override
    public AdapterReview.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_review, parent, false);

        return new AdapterReview.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterReview.MyViewHolder holder, final int position) {
        final ModelReview item = ModelReview.get(position);
        holder.nama.setText(item.getNama_anggota());
        holder.keterangan.setText(item.getKeterangan());
        Glide.with(context)
                .load(item.getFoto())
                .error(R.drawable.logo2)
                .into(holder.foto);

        holder.lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(context, ReviewActivity.class);
                a.putExtra("kode", item.getId_review());
                context.startActivity(a);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ModelReview.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama, keterangan, lihat;
        ImageView foto;

        MyViewHolder(View view) {
            super(view);
            nama = view.findViewById(R.id.nama);
            keterangan = view.findViewById(R.id.keterangan);
            foto = view.findViewById(R.id.foto);
            lihat = view.findViewById(R.id.lihat);
        }
    }

}
