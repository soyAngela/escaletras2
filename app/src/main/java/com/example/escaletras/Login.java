package com.example.escaletras;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Activity actividadLogin = this;

        TextView textRegistrate = findViewById(R.id.textRegistrate);
        EditText editUsuario = findViewById(R.id.editUsuario);
        EditText editContrasena = findViewById(R.id.editContrasena);
        Button botonIniciar = findViewById(R.id.botonIniciar);

        textRegistrate.setPaintFlags(textRegistrate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //Subraya la palabra "Registrate"

        textRegistrate.setOnClickListener(new View.OnClickListener() { //Listener para acceder al menu de registro
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registro.class);
                actividadLogin.startActivityForResult(intent, 1);
            }
        });

        botonIniciar.setOnClickListener(new View.OnClickListener() { //Listener para el boton de inicar sesion
            @Override
            public void onClick(View view) {
                comprobarUsuario(editUsuario.getText().toString(), String.valueOf(editContrasena.getText().toString().hashCode()));
            }
        });
    }

    public void comprobarUsuario(String usuario, String contrasena){ //Comprueba el usuario

        Data data = new Data.Builder() //Recoger los datos que se van a pasar a la peticion
                .putString("usuario",usuario)
                .putString("contrasena",contrasena)
                .putString("url", "validar_usuario.php")
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPhp.class).setInputData(data).build(); //Se construye la tarea del WorkBuilder
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) { //Cuando recibe la respuesta de la peticion
                        if(workInfo != null && workInfo.getState().isFinished()){
                            Log.d("angela", "Login/ resultado="+workInfo.getOutputData().getInt("resultado", 2));
                            if(workInfo.getOutputData().getInt("resultado", 2) == 1){ //Si se recibe el resultado deseado
                                volverMain(usuario);
                            }else{
                                Toast.makeText(Login.this, "El usuario o la contraseña son incorrectos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //Al volver de la actividad de registro
        super.onActivityResult(requestCode, resultCode, data);
        String usuario = data.getStringExtra("usuario");
        volverMain(usuario);
    }

    public void volverMain(String usuario){ //Se encarga de volver a la actividad main
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("usuario", usuario);
        setResult(1, intent);
        finish();
    }
}