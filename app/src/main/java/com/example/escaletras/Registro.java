package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        TextView textInicia = findViewById(R.id.textInicia);

        textInicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registro.this, Login.class);
                Registro.this.startActivity(intent);
            }
        });

        botonRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registratUsuario(editRegisUsuario.getText().toString(), editRegisContrasena.getText().toString())){
                    Intent intent = new Intent(Registro.this, MainActivity.class);
                    Registro.this.startActivity(intent);
                }else{
                    Toast.makeText(Registro.this, "No ha sido posible registrar este usuario.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean registratUsuario(String usuario, String contrasena){
        return true;
    }
}