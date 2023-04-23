package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button botonJugar = findViewById(R.id.botonJugar);
        Button botonInstrucciones = findViewById(R.id.botonInstrucciones);
        Button botonIdentificate = findViewById(R.id.botonIdentificate);
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
                Intent intent = new Intent(MainActivity.this, Login.class);
                MainActivity.this.startActivity(intent);
            }
        });


    }


}