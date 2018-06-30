package com.example.ricardopazdemiquel.moviles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;


public class cobranza extends AppCompatActivity{

    private TextView tv_total;
    private Button btn_cobranza;
    String carrera;
    private JSONObject obj_carrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranza);

        tv_total = findViewById(R.id.tv_total);
        btn_cobranza = findViewById(R.id.btn_cobrar);



        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        carrera = preferencias.getString("carrera", "");

        if(carrera.length()>0){
            try {
                obj_carrera = new JSONObject(carrera);
                tv_total.setText(obj_carrera.getInt("costo_final")+" Bs.");
                btn_cobranza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            new Cobrar_Carrera(obj_carrera.getInt("id")).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }else{
        }
    }

    //asyncTask Cobrar carrera
    private class Cobrar_Carrera extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id_carrera;

        public Cobrar_Carrera(int id_carrera) {
            this.id_carrera = id_carrera;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(cobranza.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "cobrar_carrera");
            parametros.put("id_carrera",id_carrera+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if(resp.equals("falso")){
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                return;
            }
            else if(resp.equals("exito")){
                //new MapCarrera.buscar_carrera().execute();
                SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                SharedPreferences.Editor  edit = preferencias.edit();
                edit.remove("carrera");

                Intent intent = new Intent(cobranza.this, MainActivityConductor.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

}
