package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class EditarNivel extends AppCompatActivity {

    EditText origenInput, destinoInput;
    Button botonGuardar;
    Button botonBorrar;
    TextView textoAnadir;
    MyDatabaseHelper miDB;
    String nivel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_nivel);

        miDB = new MyDatabaseHelper(EditarNivel.this);

        Bundle extras = getIntent().getExtras();
        nivel = extras.getString("nivel");
        Log.d("angela",""+nivel);

        origenInput = findViewById(R.id.origenInput);
        destinoInput = findViewById(R.id.destinoInput);
        botonGuardar = findViewById(R.id.botonGuardar);
        botonBorrar = findViewById(R.id.botonBorrar);
        textoAnadir = findViewById(R.id.textoAnadir);

        MyDatabaseHelper miDB = new MyDatabaseHelper(EditarNivel.this);

        if(!nivel.equals("nuevo")){
            textoAnadir.setText("Modifica el nivel");
            origenInput.setText(extras.getString("palabraOrigen"));
            destinoInput.setText(extras.getString("palabraDestino"));
        }

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(origenInput.getText().length()==4 && destinoInput.getText().length()==4){
                    if(nivel.equals("nuevo")){
                        miDB.anadirLibro(origenInput.getText().toString(), destinoInput.getText().toString());
                        finish();
                    }else{
                        miDB.actualizarLibro(nivel,origenInput.getText().toString(), destinoInput.getText().toString());
                        finish();
                    }
                }else{
                    Toast.makeText(EditarNivel.this, "La palabra debe tener 4 letras.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miDB.borrarNivel(nivel);
                finish();
            }
        });
    }
}