package com.example.usuari.casopractico;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    static TextView tvclock;
    static LatLng sydney;
    public static Handler manejador;
    static ArrayList<LatLng> db= new ArrayList<LatLng>();
    static int counter =0;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tvclock = this.findViewById(R.id.tvText);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

public void Iniciar(View v){ //INICIAR TIMER

    startService(new Intent(this,Reloj.class));
    //HANDLER TIMER
    manejador = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String resultado = (String) msg.obj;
            tvclock.setText(resultado);
            Servidor com = new Servidor();
            com.execute("10.0.2.2","9998","1");
        }
    };

}

public void Detener(View v){ //DETENER TIMER
    stopService(new Intent(this,Reloj.class));
}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    //COMUNICACION SERVIDOR
    private class Servidor extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) { //MANEJO DE JSON
            try {
                System.out.println(s);
                String[] separated = s.split(";");
                JSONArray jarray = new JSONArray(separated[0]);
                JSONArray jarray2 = new JSONArray(separated[1]);
                 Double[] latitud = new Double[jarray.length()];
                for (int i=0;i<jarray.length();i++){
                    latitud[i]=jarray.getDouble(i);
                }
                Double[] longitud = new Double[jarray2.length()];
                for (int i=0;i<jarray2.length();i++){
                    longitud[i]=jarray2.getDouble(i);
                }
                sydney= new LatLng(latitud[0], longitud[0]);
                //db.add(sydney);
                if (db.contains(sydney)==false){
                    db.add(sydney);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker map "+counter++));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }else {
                    System.out.println("Existe: " + db.contains(sydney));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) { //SOLICITUD DE DATOS
            Socket sk = null;
            String res="";
            try {
                sk = new Socket(strings[0], Integer.parseInt(strings[1]));
                PrintStream ps = new PrintStream(sk.getOutputStream());
                ps.println(strings[2]);
                ps.flush();
                InputStream is = sk.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
                res = bf.readLine();
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (sk!= null){
                    try {
                        sk.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return res;
        }
    }
}