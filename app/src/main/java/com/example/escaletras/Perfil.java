package com.example.escaletras;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Perfil extends AppCompatActivity {

    TextView textNombre;
    Button botonCamara, botonGaleria, botonVolver;
    ImageView fotoView;
    private Activity actividadPerfil = this;
    String currentPhotoPath;
    Uri photoURI;
    String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        usuario = getIntent().getExtras().getString("usuario");
        textNombre = findViewById(R.id.textNombre);
        botonCamara = findViewById(R.id.botonCamara);
        botonGaleria = findViewById(R.id.botonGaleria);
        botonVolver = findViewById(R.id.botonVolver);
        fotoView = findViewById(R.id.fotoView);

        textNombre.setText(usuario); //Muestra el nombre de usuario
        bajarFotoPerfil(usuario); //Se muestra la foto de perfil si tiene

        botonCamara.setOnClickListener(new View.OnClickListener() { //Listener del boton que activa la actividad de la camara
            @Override
            public void onClick(View view) {
                permisosCamara();
            }
        });

        botonGaleria.setOnClickListener(new View.OnClickListener() { //Listener del boton que activa la actividad de la galeria
            @Override
            public void onClick(View view) {
                Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                actividadPerfil.startActivityForResult(galeria, 2);
            }
        });

        botonVolver.setOnClickListener(new View.OnClickListener() { //Listener del boton para volver a la actividad principal
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void permisosCamara(){ //Metodo que se asegura de que los permisos necesarios estan concedidos
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{ //Si los permisos estan concedidos
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Metodo que recoge los resultados de la actividad despues de pedir los permisos necesarios
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { //Si se han concedido
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else { //No se han concedido
                Toast.makeText(this, "Se necesitan los permisos para utilizar la camara.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Metodo que recoge la foto una vez se ha tomado con la camara o elegido de la galeria
        super.onActivityResult(requestCode, resultCode, data);
        String photoPath = currentPhotoPath;
        if (requestCode == 1) { //Si se ha hecho la foto con la camara
            if (resultCode == Activity.RESULT_OK) {

                File f = new File(currentPhotoPath);
                fotoView.setImageURI(Uri.fromFile(f)); //Se muestra en la pantalla

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent); //Se guarda en la galeria
            }
        }

        if(requestCode == 2){ //Si se ha elegido la foto de la galeria
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                    photoPath = image.getAbsolutePath();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                fotoView.setImageURI(contentUri); //Se muestra en la pantalla
            }
        }
        subirFotoBD(usuario, photoPath);
    }

    private String getFileExt(Uri contentUri) { //Devuelve la extension del archivo
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException { //Crea una archivo con las caracteristicas necesarias
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() { //Lanza la actividad de la camara
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                actividadPerfil.startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    public void subirFotoBD(String usuario, String photoPath){
        //Metodo para subir la foto a la base de datos

        String fotoen64 = null;

        try {
            File file = new File(photoPath); //Cambiar el tamañao del bitmap
            Bitmap originalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth() / 100, originalBitmap.getHeight() / 100, false);
            fotoView.setImageBitmap(resizedBitmap);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //Convertir de Bitmap a byte[]
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            byte[] imagenRedimensionada = outputStream.toByteArray();

            fotoen64 = Base64.encodeToString(imagenRedimensionada,Base64.DEFAULT); //Codificar en base64

        } catch (Exception e) {
            e.printStackTrace();
        }

        Data data = new Data.Builder()
                .putString("usuario",usuario)
                .putString("foto",fotoen64)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(SubirFotoPhp.class).setInputData(data).build(); //Construir tarea del WorkBuilder
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) { //Cuando recibe la respuesta de la peticion
                        if(workInfo != null && workInfo.getState().isFinished()){
                            Log.d("angela", "subirFotoBD()/ resultado="+workInfo.getOutputData().getInt("resultado", 2));

                            if(workInfo.getOutputData().getInt("resultado", 2) == 1){ //Si se ha subido correctamente termina la actividad
                                actividadPerfil.finish();
                            }else{
                                Toast.makeText(Perfil.this, "No se ha podido subir la foto de perfil.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);

    }

    public void bajarFotoPerfil(String usuario){ //Metodo que descarga la foto de la base de datos
        Data data = new Data.Builder()
                .putString("usuario",usuario)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(BajarFotoPhp.class).setInputData(data).build(); //Construir tarea del WorkBuilder
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) { //Cuando recibe la respuesta de la peticion
                        if(workInfo != null && workInfo.getState().isFinished()){
                            Log.d("angelaa", "bajarFoto/ resultado="+workInfo.getOutputData().getString("resultado"));

                            if(workInfo.getOutputData().getString("resultado") != null){ //Si recibe una foto
                                String base64String = workInfo.getOutputData().getString("resultado"); //Decodificar la foto en base64
                                Log.d("angela", "bajarFoto()/ base64String: " + base64String);
                                byte[] imageData = Base64.decode(base64String, Base64.DEFAULT); //Convertir en byte[]
                                Log.d("angela", "bajarFoto()/ imageData: " + imageData);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length); //Convertir en bitmap
                                fotoView.setImageBitmap(bitmap); //Mostrar en la pantalla
                            }else{
                                Toast.makeText(Perfil.this, "No se ha podido bajar la foto de perfil.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}