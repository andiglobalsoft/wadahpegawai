package com.ags.hots.berita;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ags.hots.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterBerita extends RecyclerView.Adapter<AdapterBerita.MyViewHolder> {
    private Context context;
    private List<ModelBerita> ModelBerita;

    public AdapterBerita(Context context, List<ModelBerita> ModelBerita) {
        this.context = context;
        this.ModelBerita = ModelBerita;
    }

    @Override
    public AdapterBerita.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_berita, parent, false);

        return new AdapterBerita.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterBerita.MyViewHolder holder, final int position) {
        final ModelBerita item = ModelBerita.get(position);
        holder.nama.setText(item.getJudul());
        String des = Html.fromHtml(item.getDeskripsi()).toString();
        holder.keterangan.setText(Html.fromHtml(des));
        Glide.with(context)
                .load(item.getFoto())
                .error(R.drawable.logo2)
                .into(holder.foto);

        holder.keterangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(context, DetailBerita.class);
                a.putExtra("kode", item.getId_berita());
                context.startActivity(a);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ModelBerita.size();
    }

    public void removeItem(int position) {
        ModelBerita.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(ModelBerita item, int position) {
        ModelBerita.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama, keterangan;
        ImageView foto;

        MyViewHolder(View view) {
            super(view);
            nama = view.findViewById(R.id.nama);
            keterangan = view.findViewById(R.id.keterangan);
            foto = view.findViewById(R.id.foto);
        }
    }
}
