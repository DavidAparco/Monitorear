package com.example.giovanny.monitorear;

import android.util.Log;

import com.example.giovanny.monitorear.Device.Dispositivo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by giovanny on 19/06/16.
 */
public class ConexionServer {
    String urlp="http://52.10.199.174:8081/";

    public ArrayList<Censado> receiveJson(String func)throws IOException {

        InputStream is = null;
        int len = 1500;
        ArrayList<Censado> censados=new ArrayList<>();
        try {
            HttpURLConnection conn = AbroConexion(urlp+func);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            Log.d("respuesta", contentAsString);
            JSONObject jsonObject = new JSONObject(contentAsString);
            JSONArray jsonArray = jsonObject.getJSONArray("array_json");

            Log.d("respuesta", "Tamano json: " + jsonArray.length());

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject json_data = jsonArray.getJSONObject(i);
                Censado censado= new Censado(json_data.getString("id_cansat"),
                        json_data.getString("id_sensor"),
                        (float)json_data.getDouble("value"),
                        json_data.getString("fecha") ,
                        json_data.getString("hora") );
                censados.add(censado);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return censados;
    }

    public ArrayList<Dispositivo> receiveJsonDispositivo(String func)throws IOException {

        InputStream is = null;
        int len = 1500;
        ArrayList<Dispositivo> dispositivos=new ArrayList<>();

        try {
            HttpURLConnection conn = AbroConexion(urlp + func);

            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);

            JSONObject jsonObject = new JSONObject(contentAsString);
            JSONArray jsonArray = jsonObject.getJSONArray("array_json");

            Log.d("respuesta", "Tamano json: " + jsonArray.length());
            JSONObject json_data;
            for(int i=0; i<jsonArray.length(); i++){
                json_data = jsonArray.getJSONObject(i);

                Dispositivo dispositivo = new Dispositivo(R.drawable.cansat,
                                json_data.getString("id_cansat"),
                                json_data.getString("modelo"),
                                json_data.getString("f_install"),
                                "U",
                                "cansat"
                                );
                JSONArray jsonArraySensores = json_data.getJSONArray("sensores");
                dispositivos.add(dispositivo);

                for(int j=0; j<jsonArraySensores.length(); j++){
                    json_data = jsonArraySensores.getJSONObject(j);
                    Dispositivo sensor = new Dispositivo(R.drawable.sensor,
                            json_data.getString("id_sensor"),
                            json_data.getString("t_sensor"),
                            json_data.getString("unidad"),
                            json_data.getString("modelo"),
                            json_data.getString("f_install")
                            );
                    dispositivos.add(sensor);
                    Log.d("sensor",sensor.toString());
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return dispositivos;
    }

    private HttpURLConnection AbroConexion(String urlp){
        HttpURLConnection conn = null;
        try {
            URL url= new URL(urlp);
            Log.d("respuesta", urlp);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setDoInput(true);
        // Starts the query
            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
