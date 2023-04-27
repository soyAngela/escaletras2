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
import java.nio.file.Files;
import java.nio.file.Paths;
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

        textNombre.setText(usuario);
        bajarFotoPerfil(usuario);

        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisosCamara();
            }
        });

        botonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                actividadPerfil.startActivityForResult(galeria, 2);
            }
        });

        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void permisosCamara(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Se necesitan los permisos para utilizar la camara.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                File f = new File(currentPhotoPath);
                fotoView.setImageURI(Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                try {
                    byte[] img = Files.readAllBytes(Paths.get(currentPhotoPath));
                    Bitmap originalBitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    Log.d("angela", "bitesW: " + originalBitmap.getWidth());
                    Log.d("angela", "bitesH: " + originalBitmap.getHeight());
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth() / 100, originalBitmap.getHeight() / 100, false);
                    fotoView.setImageBitmap(resizedBitmap);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    byte[] imagenRedimensionada = outputStream.toByteArray();

                    String fotoen64 = Base64.encodeToString(imagenRedimensionada,Base64.DEFAULT);
                    subirFotoBD(usuario, fotoen64);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                fotoView.setImageURI(contentUri);

            }
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
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

    public void subirFotoBD(String usuario, String foto){

        Data data = new Data.Builder()
                .putString("usuario",usuario)
                .putString("foto",foto)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(SubirFotoPhp.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            Log.d("angelaa", "resultado="+workInfo.getOutputData().getInt("resultado", 2));

                            if(workInfo.getOutputData().getInt("resultado", 2) == 1){
                                actividadPerfil.finish();
                            }else{
                                Toast.makeText(Perfil.this, "No se ha podido subir la foto de perfil.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);

    }

    public void bajarFotoPerfil(String usuario){
        Data data = new Data.Builder()
                .putString("usuario",usuario)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(BajarFotoPhp.class).setInputData(data).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            Log.d("angelaa", "bajarFoto/ resultado="+workInfo.getOutputData().getString("resultado"));

                            if(workInfo.getOutputData().getString("resultado") != null){
                                String base64String = workInfo.getOutputData().getString("resultado");
                                Log.d("angela", "bajarFoto/ base64String: " + base64String);
                                byte[] imageData = Base64.decode(base64String, Base64.DEFAULT);
                                Log.d("angela", "bajarFoto/ imageData: " + imageData);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                fotoView.setImageBitmap(bitmap);
                            }else{
                                Toast.makeText(Perfil.this, "No se ha podido bajar la foto de perfil.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}