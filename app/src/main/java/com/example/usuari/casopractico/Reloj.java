package com.example.usuari.casopractico;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by usuari on 05/04/2018.
 */

public class Reloj extends Service{
    Timer timer;
    public Reloj() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IniciarServicio();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
    private void IniciarServicio(){
        timer = new Timer();
        timer.schedule(new MiTimer(),0,5000);
    }
    private  class MiTimer extends TimerTask {

        @Override
        public void run() {
            Date d = new Date();
            DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            String resultado = df.format(d);
            MapsActivity.manejador.obtainMessage(0,0,0,resultado).sendToTarget();
        }
    }
}
