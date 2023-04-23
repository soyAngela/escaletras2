package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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

        TextView textRegistrate = findViewById(R.id.textRegistrate);
        EditText editUsuario = findViewById(R.id.editUsuario);
        EditText editContrasena = findViewById(R.id.editContrasena);
        Button botonIniciar = findViewById(R.id.botonIniciar);

        textRegistrate.setPaintFlags(textRegistrate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //Subraya la palabra "Registrate"

        textRegistrate.setOnClickListener(new View.OnClickListener() { //Listener para acceder al menu de registro
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registro.class);
                Login.this.startActivity(intent);
            }
        });

        botonIniciar.setOnClickListener(new View.OnClickListener() { //Listener para intentr inicar sesion
            @Override
            public void onClick(View view) {
                if(comprobarUsuario(editUsuario.getText().toString(), editContrasena.getText().toString())){ //Si el usuario es correcto vuelve a la pagina principal
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    Login.this.startActivity(intent);
                }else{ //Si no es correcto muestra un agsio
                    Toast.makeText(Login.this, "El usuario o la contraseña son incorrectos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean comprobarUsuario(String usuario, String contrasena){ //Comprueba el usuario
        String parametros = "usuario="+usuario+"&contra="+contrasena;
        return true;
    }

}