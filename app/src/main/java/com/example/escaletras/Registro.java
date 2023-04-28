package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        EditText editRegisUsuario = findViewById(R.id.editRegisUsuario);
        EditText editRegisContrasena = findViewById(R.id.editRegisContrasena);
        Button botonRegistrate = findViewById(R.id.botonRegistrate);
        TextView textYaInicia = findViewById(R.id.textYaInicia);

        textYaInicia.setPaintFlags(textYaInicia.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //Subraya la palabra "Inicia Sesion"

        textYaInicia.setOnClickListener(new View.OnClickListener() { //Listener de boton para acceder al menu de inicio de sesion
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registro.this, Login.class);
                Registro.this.startActivity(intent);
            }
        });

        botonRegistrate.setOnClickListener(new View.OnClickListener() { //Listener para el boton de registrarse
            @Override
            public void onClick(View view) {
                registrarUsuario(editRegisUsuario.getText().toString(), String.valueOf(editRegisContrasena.getText().toString().hashCode()));
            }
        });
    }

    public void registrarUsuario(String usuario, String contrasena){ //Metodo que hace la peticion de registrarse
        Data data = new Data.Builder()
                .putString("usuario",usuario)
                .putString("contrasena",contrasena)
                .putString("url", "registrar_usuario.php")
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionPhp.class).setInputData(data).build(); //Se construye la tarea del WorkBuilder
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) { //Cuando recibe la respuesta de la peticion
                        if(workInfo != null && workInfo.getState().isFinished()){
                            if(workInfo.getOutputData().getInt("resultado", 2) == 1){ //Si recibe el resultado deseado
                                Intent intent = new Intent(Registro.this, MainActivity.class);
                                intent.putExtra("usuario", usuario);
                                setResult(1, intent);
                                finish();
                            }else{
                                Toast.makeText(Registro.this, "El usuario o la contraseña no son validos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}