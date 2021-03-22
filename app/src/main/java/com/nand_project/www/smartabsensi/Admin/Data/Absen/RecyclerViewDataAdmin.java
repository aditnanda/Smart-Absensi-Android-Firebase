package com.nand_project.www.smartabsensi.Admin.Data.Absen;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nand_project.www.smartabsensi.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewDataAdmin extends RecyclerView.Adapter<RecyclerViewDataAdmin.ViewHolder> {

    private ArrayList<Model> models;
    private Context context;

    public RecyclerViewDataAdmin(ArrayList<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_data_absen_list, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_nama.setText(models.get(position).getNama());
        holder.tv_nbi.setText("NBI : "+models.get(position).getNbi());
        holder.tv_qr.setText("Kode QR : "+models.get(position).getQr());
        holder.tv_masuk.setText("Waktu Masuk : "+models.get(position).getMasuk());
        holder.tv_keluar.setText("Waktu Keluar : "+models.get(position).getKeluar());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setFilter(List<Model> dataFilter) {
        if (dataFilter != null){
            models = new ArrayList<>();
            models.addAll(dataFilter);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_nama, tv_nbi, tv_qr, tv_masuk, tv_keluar;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_nama = itemView.findViewById(R.id.nama_list);
            tv_nbi = itemView.findViewById(R.id.nbi_list);
            tv_qr = itemView.findViewById(R.id.qr_code);
            tv_masuk = itemView.findViewById(R.id.waktu_masuk);
            tv_keluar = itemView.findViewById(R.id.waktu_keluar);

        }
    }
}
