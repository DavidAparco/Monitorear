package com.example.giovanny.monitorear.GraficaryMQTT;

import android.content.Intent;
import android.support.v4.app.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.giovanny.monitorear.Censado;
import com.example.giovanny.monitorear.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class GraficarActivityFragment extends Fragment {

    ArrayList<Censado> results;
    LineChart lineChart ;
    TextView tipo;
    TextView id;
    private final String GioDBug = "GioDBug";
    private ArrayList<Entry> entries;
    ArrayList<String> labels;
    String topicos [] = new String[]{"temperatura","presion","monoxido","dioxido","amoniaco","altura"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_graficar, container, false);

        tipo = (TextView) rootView.findViewById(R.id.edTipoSensor);
        id = (TextView) rootView.findViewById(R.id.edIdSensor);

        tipo.setText(getArguments().getString("tiposensor"));
        id.setText(getArguments().getString("idsensor"));

        results = getArguments().getParcelableArrayList("censados");
        lineChart = (LineChart) rootView.findViewById(R.id.chart);
        graficar(getArguments().getString("unidad"));
        creoClienteMQTT(getArguments().getString("tiposensor"));

        return rootView;
    }

    private void graficar(String unidad){
        entries = new ArrayList<>();
        Censado cen;
        labels = new ArrayList<>();
        for(int i=0;i<results.size();i++){
            cen=results.get(i);
            entries.add(new Entry(cen.getValue(), i));
            labels.add(cen.getHora());
        }
        LineDataSet dataset = new LineDataSet(entries, unidad);
        LineData data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart
        lineChart.setDescription("Seminario Tesis I");  // set the description
    }

    private void refrescar(Censado censado){
        results.add(censado);
        entries.add(new Entry(censado.getValue(), results.size()-1));
        labels.add(censado.getHora());

        LineData data = new LineData(labels, new LineDataSet(entries, "ºC"));
        lineChart.setData(data); // set the data and list of lables into chart
        lineChart.invalidate(); // refresh
        Log.d(GioDBug,"Refrescado!");
    }

    private void creoClienteMQTT(final String sub){
        final MqttAndroidClient mqttAndroidClient
                = new MqttAndroidClient(getActivity().getApplicationContext(), "tcp://52.10.199.174:1883", "GioID");
        mqttAndroidClient.setCallback(new MiCallBackMQTT() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(GioDBug,"Llego del topic " + topic + ": " + new String(message.getPayload()));
                Censado cen = converJson(new String(message.getPayload()));
                refrescar(cen);
            }
        });

        try {
            mqttAndroidClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(GioDBug, "Connection Success!");
                    try {
                        Log.d(GioDBug,"Subscribing to "+sub.toLowerCase());
                        mqttAndroidClient.unsubscribe(topicos);
                        mqttAndroidClient.subscribe(sub.toLowerCase(), 0);
                    } catch (MqttException ex) { }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(GioDBug,"Connection Failure!");
                }
            });
        } catch (MqttException ex) {

        }
    }

}
