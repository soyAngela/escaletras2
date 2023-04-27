package com.example.escaletras;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Activity activityMain = this;
    Button botonJugar;
    Button botonInstrucciones;
    Button botonIdentificate;
    Button botonPerfil;
    TextView textBienvenida;
    String usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonJugar = findViewById(R.id.botonJugar);
        botonInstrucciones = findViewById(R.id.botonInstrucciones);
        botonIdentificate = findViewById(R.id.botonIdentificate);
        botonPerfil = findViewById(R.id.botonPerfil);
        textBienvenida = findViewById(R.id.textBienvenido);
        botonJugar.setOnClickListener(new View.OnClickListener() { //Boton que lleva a la actividad de Niveles.java
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Niveles.class);
                MainActivity.this.startActivity(intent);
            }
        });

        botonInstrucciones.setOnClickListener(new View.OnClickListener(){ //Boton que muestra un dialogo con las instrucciones del juego
            @Override
            public void onClick(View view) {
                DialogFragment dialogoInstrucciones = new InstruccionesDialogo();
                dialogoInstrucciones.show(getSupportFragmentManager(), "etiquetaDialogo");
            }
        });

        botonIdentificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usuario == null){
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    activityMain.startActivityForResult(intent, 1);
                }else{
                    usuario = null;
                    botonIdentificate.setVisibility(View.VISIBLE);
                    textBienvenida.setVisibility(View.GONE);
                }
            }
        });
        botonPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Perfil.class);
                intent.putExtra("usuario", usuario);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        usuario = data.getStringExtra("usuario");

        botonIdentificate.setVisibility(View.GONE);

        textBienvenida.setText("Bienvenid@ de nuevo, "+usuario+"!");
        textBienvenida.setVisibility(View.VISIBLE);

        botonIdentificate.setText("Cerrar sesión");
        botonIdentificate.setBackgroundColor(Color.WHITE);

        botonPerfil.setVisibility(View.VISIBLE);
    }

}