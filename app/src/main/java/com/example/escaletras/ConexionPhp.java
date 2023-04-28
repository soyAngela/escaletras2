package com.example.escaletras;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConexionPhp extends Worker {

    public ConexionPhp(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String usuario = getInputData().getString("usuario"); //Se recogen los parametros de la llamada
        String contra = getInputData().getString("contrasena");
        String url = getInputData().getString("url");
        Log.d("angela", "usu: "+usuario);
        Log.d("angela", "pass: "+contra);
        Log.d("angela", "url: "+url);

        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/"+url; //La direccion con un parametro de url para diferenciar entre validar y registrar
        HttpURLConnection urlConnection = null;
        try{
            URL destino = new URL(direccion); //Construimos la url
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("usuario", usuario)
                    .appendQueryParameter("contrasena", contra);

            String parametros = builder.build().getEncodedQuery();
            Log.d("angela", "parametros: "+parametros);

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");


            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            Log.d("angela", "statusCode: "+statusCode);
            Log.d("angela", "message: "+ urlConnection.getResponseMessage());
            if (statusCode == 200){ //Comprobar que la respuesta es correcta
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line = bufferedReader.readLine();
                inputStream.close();
                Log.d("angela","line: "+line);
                if(line.equals("1")){
                    Data resultado = new Data.Builder()
                            .putInt("resultado", 1)
                            .build();
                    Log.d("angela","Exito");
                    return Result.success(resultado);
                }else{
                    Data resultado = new Data.Builder()
                            .putInt("resultado", 0)
                            .build();
                    Log.d("angela", "Fallo");
                    return Result.success(resultado);
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return Result.failure();
    }
}
