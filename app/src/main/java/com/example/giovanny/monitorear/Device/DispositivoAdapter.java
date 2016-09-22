package com.example.giovanny.monitorear.Device;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giovanny.monitorear.R;

import java.util.ArrayList;

/**
 * Created by giovanny on 20/06/16.
 */
public class DispositivoAdapter extends RecyclerView.Adapter<DispositivoAdapter.DispositivoViewHolder>{

    private ArrayList<Dispositivo> mDispositivoSet;
    private static MyClickListener myClickListener;

    public DispositivoAdapter(ArrayList<Dispositivo> mDispositivoSet) {
        this.mDispositivoSet = mDispositivoSet;
    }

    public void setmHistorialSet(ArrayList<Dispositivo> mDispositivoSet) {
        this.mDispositivoSet = mDispositivoSet;
        notifyDataSetChanged();
    }

    public static class DispositivoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TextView tDevice;
        TextView tIdDevice;
        TextView tdModelo;
        TextView tFecha;
        ImageView Idispositivo;

        public DispositivoViewHolder(View itemView) {
            super(itemView);
            Idispositivo= (ImageView) itemView.findViewById(R.id.Idispositivo);
            tDevice=(TextView)itemView.findViewById(R.id.tDevice);
            tIdDevice=(TextView)itemView.findViewById(R.id.tIdDevice);
            tdModelo=(TextView)itemView.findViewById(R.id.tdModelo);
            tFecha=(TextView)itemView.findViewById(R.id.tFecha);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DispositivoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_device, parent, false);

        return new DispositivoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DispositivoViewHolder holder, int position) {
        holder.Idispositivo.setImageResource(mDispositivoSet.get(position).getImagen());
        String tipo =mDispositivoSet.get(position).getTipo_sensor() ;
        if(mDispositivoSet.get(position).getImagen()==R.drawable.cansat){
            holder.tDevice.setText("Cansat");
        }else holder.tDevice.setText(tipo.substring(0, 1).toUpperCase() + tipo.substring(1));
        holder.tIdDevice.setText(mDispositivoSet.get(position).getId());
        holder.tdModelo.setText(mDispositivoSet.get(position).getModelo());
        holder.tFecha.setText(mDispositivoSet.get(position).getFecha());
    }

    @Override
    public int getItemCount() {
        return mDispositivoSet.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
