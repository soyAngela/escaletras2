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

public class BajarFotoPhp extends Worker {

    public BajarFotoPhp(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String usuario = getInputData().getString("usuario");
        Log.d("angelaa", "BajarFoto/ doWork() usuario: "+usuario);

        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agonzalez488/WEB/bajar_foto.php";
        HttpURLConnection urlConnection = null;

        try{
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("usuario", usuario);

            String parametros = builder.build().getEncodedQuery();
            Log.d("angela", "BajarFoto/ parametros: "+parametros);

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");


            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            Log.d("angela", "BajarFoto/ statusCode: "+statusCode);
            Log.d("angela", "BajarFoto/ message: "+ urlConnection.getResponseMessage());
            if (statusCode == 200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
                Log.d("angela","BajarFoto/ result: "+result);
                Data resultado = new Data.Builder()
                        .putString("resultado", result)
                        .build();
                Log.d("angela","BajarFoto/ resultado: "+resultado);
                return Result.success(resultado);

            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return Result.failure();
    }
}

