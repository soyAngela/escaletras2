package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        if(!nivel.equals("nuevo")){ //Si el nivel es nuevo hace modificaciones en la interfaz
            textoAnadir.setText("Modifica el nivel");
            origenInput.setText(extras.getString("palabraOrigen"));
            destinoInput.setText(extras.getString("palabraDestino"));
        }

        botonGuardar.setOnClickListener(new View.OnClickListener() { //Boton de guardar que gaurad lo encesario en la base de datos
            @Override
            public void onClick(View view) {
                if(origenInput.getText().length()==4 && destinoInput.getText().length()==4){
                    if(nivel.equals("nuevo")){ //Si es un nivel nuevo
                        miDB.anadirNivel(origenInput.getText().toString(), destinoInput.getText().toString()); //Añade un nuevo libro a la base de datos
                        finish();
                    }else{ //Si no es nueva
                        miDB.actualizarNivel(nivel,origenInput.getText().toString(), destinoInput.getText().toString()); //Se modifica el nivel seleccionado
                        finish();
                    }
                }else{//Si se ha introducido mal una palabra
                    Toast.makeText(EditarNivel.this, "La palabra debe tener 4 letras.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        botonBorrar.setOnClickListener(new View.OnClickListener() { //Boton de borrar el nivel
            @Override
            public void onClick(View view) {
                miDB.borrarNivel(nivel); //Borra de la base datos el nivel seleccionado
                finish();
            }
        });
    }
}