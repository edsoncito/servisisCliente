package com.example.ricardopazdemiquel.moviles;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.Contexto;
import utiles.Token;

public class PidiendoSiete extends AppCompatActivity {
    private String latInicio;
    private String lngInicio;
    private String latFin;
    private String lngFin;
    private String token;
    private String id_usr;
    private String tipoCarrera;
    private String tipo_pago;
    private String productos;
    private JSONArray array;
    private static final int TIPO_TOGO = 2;
    private int tipo;
    private BroadcastReceiver broadcastReceiverConfirmoCarrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pidiendo_siete);

        Intent intent= getIntent();

        tipoCarrera = intent.getStringExtra("tipo");
        tipo = Integer.valueOf(tipoCarrera);
        if(tipo == TIPO_TOGO){
            latFin=intent.getStringExtra("latFin");
            lngFin=intent.getStringExtra("lngFin");
            token=intent.getStringExtra("token");
            id_usr=intent.getStringExtra("id_usr");
            productos = intent.getStringExtra("productos");
            tipo_pago = intent.getStringExtra("tipo_pago");
            try {
                array = new JSONArray(productos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            latInicio=intent.getStringExtra("latInicio");
            lngInicio=intent.getStringExtra("lngInicio");
            latFin=intent.getStringExtra("latFin");
            lngFin=intent.getStringExtra("lngFin");
            token=intent.getStringExtra("token");
            id_usr=intent.getStringExtra("id_usr");
            tipo_pago = intent.getStringExtra("tipo_pago");
        }
        new Get_ActualizarToken(id_usr).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class Get_ActualizarToken extends AsyncTask<Void, String, String>{
        private String id;
        public Get_ActualizarToken(String id){
            this.id=id;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "actualizar_token");
            parametros.put("id_usr",id);
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(tipo == TIPO_TOGO){
                new buscar_carrera_togo().execute();
            }else{
                new buscar_carrera().execute();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private class buscar_carrera extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","buscar_carrera");
            param.put("latInicio",latInicio);
            param.put("lngInicio",lngInicio);
            param.put("latFin",latFin);
            param.put("lngFin",lngFin);
            param.put("token", token);
            param.put("id",id_usr);
            param.put("tipo",tipoCarrera);
            param.put("tipo_pago",tipo_pago);

            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String pacientes) {
            super.onPostExecute(pacientes);
            if (pacientes.equals("falso")) {
                finish();
                return;
            }else{
                try {
                    JSONObject obj = new JSONObject(pacientes);
                    Intent inte = new Intent(PidiendoSiete.this,EsperandoConductor.class);
                    inte.putExtra("obj_carrera",obj.toString());
                    startActivity(inte);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    private class buscar_carrera_togo extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","buscar_carrera_togo");
            param.put("latFin",latFin);
            param.put("lngFin",lngFin);
            param.put("token", token);
            param.put("id",id_usr);
            param.put("tipo",tipoCarrera);
            param.put("tipo_pago",tipo_pago);
            param.put("productos" , array.toString());
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String pacientes) {
            super.onPostExecute(pacientes);
            if (pacientes.equals("falso")) {
                finish();
                return;
            }else{
                try {
                    JSONObject obj = new JSONObject(pacientes);
                    Intent inte = new Intent(PidiendoSiete.this,Inicio_viaje_togo.class);
                    inte.putExtra("obj_carrera",obj.toString());
                    startActivity(inte);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }


}
