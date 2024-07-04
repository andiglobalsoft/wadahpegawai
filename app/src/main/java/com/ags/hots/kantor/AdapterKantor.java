package com.ags.hots.kantor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ags.hots.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterKantor extends RecyclerView.Adapter<AdapterKantor.MyViewHolder> {
    private Context context;
    private List<ModelKantor> ModelKantor;

    AdapterKantor(Context context, List<ModelKantor> ModelKantor) {
        this.context = context;
        this.ModelKantor = ModelKantor;
    }

    @Override
    public AdapterKantor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_transaksi, parent, false);

        return new AdapterKantor.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AdapterKantor.MyViewHolder holder, final int position) {
        final ModelKantor item = ModelKantor.get(position);
        holder.nama.setText(item.getJudul());
        holder.status.setText("Lihat peta");
        holder.keterangan.setText(item.deskripsi);
        Glide.with(context)
                .load(item.getFoto())
                .error(R.drawable.logo2)
                .into(holder.foto);

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + item.getLatitude() + "," + item.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                } catch (Exception e) {
                    Log.d("OpenPDFError", e.getMessage());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ModelKantor.size();
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
