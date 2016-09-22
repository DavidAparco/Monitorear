package com.example.giovanny.monitorear.Device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.giovanny.monitorear.Censado;
import com.example.giovanny.monitorear.ConexionServer;
import com.example.giovanny.monitorear.GraficaryMQTT.GraficarActivityFragment;
import com.example.giovanny.monitorear.R;

import java.io.IOException;
import java.util.ArrayList;

public class InfoDevicesActivityFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static String LOG_TAG = "CardViewActivity2";
    private RecyclerView mRecyclerView;
    private DispositivoAdapter mDisAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Dispositivo> results;
    ArrayList<Censado> resultsCensado;
    int posicion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_fragment, container, false);
        rootView.setTag(TAG);
        results = getArguments().getParcelableArrayList("dispositivos");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_final);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDisAdapter = new DispositivoAdapter(results);
        mRecyclerView.setAdapter(mDisAdapter);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        (mDisAdapter).setOnItemClickListener(new DispositivoAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
                posicion=position;
                if(posicion!=0)
                    new cargarCensado ("consCen/"+results.get(position).getTipo_sensor()+"_"+results.get(position).getId()).execute();
            }
        });
    }

    private class cargarCensado extends AsyncTask<String, Void, String> {
        String url;
        public cargarCensado(String url){
            this.url=url;
        }

        @Override
        protected String doInBackground(String... urls) {
            String respues="...";
            try {
                ConexionServer cs= new ConexionServer();
                resultsCensado = cs.receiveJson(url);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return respues;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("respuesta", result);
            graficar();
        }
    }

    public void graficar(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("dispositivos", results);
        String tipo = results.get(posicion).getTipo_sensor();
        bundle.putSerializable("tiposensor",tipo.substring(0, 1).toUpperCase() + tipo.substring(1));
        bundle.putSerializable("idsensor",results.get(posicion).getId());
        bundle.putSerializable("unidad",results.get(posicion).getUnidad());
        bundle.putParcelableArrayList("censados",resultsCensado);
        GraficarActivityFragment fragment = new GraficarActivityFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

}
