package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Juego extends AppCompatActivity {

    ArrayList<String> palabrasPosibles = new ArrayList<String>();
    Integer contPalabras = 2;
    String palabraOrigen;
    String palabraDestino;
    boolean victoria = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Bundle extras = getIntent().getExtras();

        String palabraOrigen = extras.getString("palabraOrigen").toUpperCase();
        String palabraDestino = extras.getString("palabraDestino").toUpperCase();

        setPalabra(palabraOrigen, 1);
        setPalabra(palabraDestino, 0);

        palabrasPosibles.add("CASA");
        palabrasPosibles.add("TETA");
        palabrasPosibles.add("POPO");
        palabrasPosibles.add("PIÑA");
        palabrasPosibles.add("PALO");
        palabrasPosibles.add("MESA");
        palabrasPosibles.add("LEMA");
        palabrasPosibles.add("CANA");
    }

    public void setPalabra(String palabra, Integer fila){
        for(int i = 0; i<4; i++){
            String s = "f"+fila+"c"+(i+1);
            int resID = getResources().getIdentifier(s, "id", getPackageName());
            ((TextView)findViewById(resID)).setText(palabra.charAt(i)+"");
        }
    }

    public void eliminarPalabra(View v){
        if(contPalabras>=2){
            for(int i = 0; i<4; i++){
                String s = "f"+contPalabras+"c"+(i+1);
                int resID = getResources().getIdentifier(s, "id", getPackageName());
                ((TextView)findViewById(resID)).setText("");
            }
            int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.GONE);
            contPalabras--;
            Log.d("contPalabras: ", contPalabras+"");
        }
        if(victoria){
            victoria = false;
        }
    }

    public void anadirPalabra(View v){
        String palabraMin = (((TextView) findViewById(R.id.nuevaPalabra)).getText()).toString();
        String palabra = palabraMin.toUpperCase();
        if(contPalabras<=6 && !victoria){
            if(palabrasPosibles.contains(palabra)){
                setPalabra(palabra, contPalabras);
                int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
                ((TableRow)findViewById(resID)).setVisibility(View.VISIBLE);
                contPalabras++;
            }
            else{
                Toast.makeText(this, "Introduce una palabra valida.", Toast.LENGTH_SHORT).show();
            }
            if(palabra.equals(this.palabraDestino)){ //Victoria
                //TODO: Notificacion de lo has conseguido en X intentos
                victoria = true;
            }
        }
        else if(!victoria){
            /*int resID = getResources().getIdentifier("tableRow"+(contPalabras), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.GONE);
            contPalabras--;*/
            Toast.makeText(this, "Has alcanzado el máximo de palabras posibles.", Toast.LENGTH_SHORT).show();
        }
    }


}