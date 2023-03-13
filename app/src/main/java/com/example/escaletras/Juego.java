package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Juego extends AppCompatActivity {

    ArrayList<String> palabrasPosibles = new ArrayList<String>();
    Integer contPalabras = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        String palabraOrigen = "CASA";
        String palabraDestino = "CACA";

        palabrasPosibles.add("CASA");
        palabrasPosibles.add("TETA");
        palabrasPosibles.add("POPO");
        palabrasPosibles.add("PIÑA");
        palabrasPosibles.add("PALO");
        palabrasPosibles.add("MESA");
        palabrasPosibles.add("LEMA");

        setPalabra(palabraOrigen, 1);
        setPalabra(palabraDestino, 0);
    }

    public void setPalabra(String palabra, Integer fila){
        for(int i = 0; i<4; i++){
            String s = "f"+fila+"c"+(i+1);
            int resID = getResources().getIdentifier(s, "id", getPackageName());
            ((TextView)findViewById(resID)).setText(palabra.charAt(i)+"");
        }
    }

    public void eliminarFila(View v){
        if(contPalabras>=2){
            for(int i = 0; i<4; i++){
                String s = "f"+contPalabras+"c"+(i+1);
                int resID = getResources().getIdentifier(s, "id", getPackageName());
                ((TextView)findViewById(resID)).setText("");
            }
            int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.GONE);
            contPalabras--;
        }
    }

    public void anadirPalabra(View v){
        String palabra = (((TextView) findViewById(R.id.nuevaPalabra)).getText()).toString();
        if(contPalabras<=6){
            if(palabrasPosibles.contains(palabra)){
                setPalabra(palabra, contPalabras);
                int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
                ((TableRow)findViewById(resID)).setVisibility(View.VISIBLE);
                contPalabras++;
            }
            else{
                Toast.makeText(this, "Introduce una palabra valida.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            //alerta de has alcanzado el maximo de palabras posibles
            Toast.makeText(this, "Has alcanzado el máximo de palabras posibles.", Toast.LENGTH_SHORT).show();
        }
    }
}