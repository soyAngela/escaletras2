package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Juego extends AppCompatActivity {

    ArrayList<String> palabrasPosibles = new ArrayList<String>();
    Integer contPalabras = 2;
    String palabraOrigen;
    String palabraDestino;
    boolean victoria = false;
    ArrayList<String> jugadas = new ArrayList<>();
    NotificationChannel channel;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Bundle extras = getIntent().getExtras(); //Se recuperan los datos del intent anterior

        cargarPalbras();

        palabraOrigen = extras.getString("palabraOrigen").toUpperCase(); //Convertir las dos palabras a mayuscula spara que sea consistente con la base de datos
        palabraDestino = extras.getString("palabraDestino").toUpperCase();

        jugadas.add(palabraOrigen);

        setPalabra(palabraDestino, 0); //Añadir las palabras que se van a jugar a la tabla
        setPalabra(palabraOrigen, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ //Si la version es oreo crea un canal para la notificacion
            channel = new NotificationChannel("victoria", "notificacionVictoria", NotificationManager.IMPORTANCE_HIGH);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){ //Todas las acciones que se tienen que llevar a cabo para restaurar la actividad despues de una interrupcion
        jugadas = savedInstanceState.getStringArrayList("jugadas");
        for(int i = 1; i<jugadas.size(); i++){ //Reconstruye la tabla con cada palabra que se habia jugado hasta el momento
            setPalabra(jugadas.get(i), i+1);
            int resID = getResources().getIdentifier("tableRow"+(i+2), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.VISIBLE);
        }
        contPalabras = jugadas.size()+1;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) { //Guardar toda la informacion necesaria antes de una interrupcion
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("jugadas", jugadas);

    }

    public void setPalabra(String palabra, Integer fila){ //Se encarga de mostrar la palabra en la vista

        for(int i = 0; i<4; i++){ //Escribe la fila caracter a caracter
            String s = "f"+fila+"c"+(i+1); //Le pone el nombre necesario a cada casilla
            int resID = getResources().getIdentifier(s, "id", getPackageName());
            TextView casilla = (TextView)findViewById(resID);
            casilla.setText(palabra.charAt(i)+""); //Añade el caracter en su sitio

            if(palabra.charAt(i) == palabraDestino.charAt(i) && fila != 0){
                casilla.setBackgroundColor(Color.GREEN); //Comprueba que letras coinciden con el resultado deseado y lo pinta de verde
            }
        }
    }

    public void eliminarPalabra(View v){ //Elimina la ultima palabra añadida
        if(contPalabras>2){ //Comprueba que hay palabras borrables
            for(int i = 0; i<4; i++){ //Va recorriendo casilla a casilla
                String s = "f"+(contPalabras-1)+"c"+(i+1);
                int resID = getResources().getIdentifier(s, "id", getPackageName());
                TextView casilla = findViewById(resID);
                casilla.setText(""); //Pone el caracter vacio para borrar la palabra
                casilla.setBackgroundResource(R.drawable.back); //Resetea el fondo para que no quede verde
            }
            int resID = getResources().getIdentifier("tableRow"+(contPalabras), "id", getPackageName());
            ((TableRow)findViewById(resID)).setVisibility(View.GONE); //Quita l avisibilidad de la finla que ya no se va a usar
            jugadas.remove(jugadas.size()-1); //Se borra la palabra de la lista de palabras usadas
            contPalabras--;
        }
        if(victoria){
            victoria = false;
        }
    }

    public void anadirPalabra(View v){ //Añade una palabra al juego
        String palabraMin = (((TextView) findViewById(R.id.nuevaPalabra)).getText()).toString();
        String palabra = palabraMin.toUpperCase(); //Recoge la palabra introducida por el usuario y se asegura de que esta en mayuscula

        if(contPalabras<=6 && !victoria){ //Compruba que  no se ha alcanzado el maximo de palabras y que no se ha ganado ya la partida

            if(palabrasPosibles.contains(palabra) && unaLetra(jugadas.get(jugadas.size()-1), palabra)){ //Compruba que la palabra que se esta jugando aparece en el fichero de palabras posibles
                                                                                                        // y ademas compruba que solo se ha cambiado una letra
                setPalabra(palabra, contPalabras);

                if(palabra.equals(this.palabraDestino)){ //Se compruba si la palabra jugada es la palabra final
                    builder = new NotificationCompat.Builder(this, "victoria"); //Muestra una notificacion con los intentos
                    if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, 11);
                    }
                    builder.setSmallIcon(android.R.drawable.btn_star_big_on)
                            .setContentTitle("Enhorabuena!")
                            .setContentText("Lo has conseguido en "+ (jugadas.size()) +" palabras.")
                            .setSubText("Victoria")
                            .setVibrate(new long[]{0, 1000, 500, 1000})
                            .setAutoCancel(true);
                    notificationManager.notify(1, builder.build());

                    victoria = true;
                }else if(contPalabras<6){ //Si aun es posible añade una fila vacia
                    int resID = getResources().getIdentifier("tableRow"+(contPalabras+1), "id", getPackageName());
                    ((TableRow)findViewById(resID)).setVisibility(View.VISIBLE);
                    ((EditText) findViewById(R.id.nuevaPalabra)).setText("");
                }else{ //Si se ha alcanzado maximo de palabras
                    Toast.makeText(this, "Has alcanzado el máximo de palabras posibles.", Toast.LENGTH_SHORT).show();
                }
                jugadas.add(palabra); //Se añade la palabra a las palabras jugadas
                contPalabras++;
            }else if(!palabrasPosibles.contains(palabra)){ //Si la palabra no esta en el fichero de palabras posibles
                Toast.makeText(this, "Introduce una palabra valida.", Toast.LENGTH_SHORT).show();
            }else{ //Si se ha cambiado mas de una letra o ninguna letra
                Toast.makeText(this, "Solo puedes cambiar una letra.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cargarPalbras(){ //carga laa palabras de palabras.txt a la lista de palabras posibles
        InputStream fich = getResources().openRawResource(R.raw.palabras);
        BufferedReader buff = new BufferedReader(new InputStreamReader(fich));
        String linea;
        try {
            while ((linea = buff.readLine()) != null){ //Recorre todo el fichero de palabras
                String[] palabras = linea.split(" "); //separa las palabras cuando hay un espacio
                for(String palabra : palabras) {
                    palabrasPosibles.add(palabra.toUpperCase());//Guarda la palabra en mayuscula
                }
            }
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean unaLetra(String palabra1, String palabra2){ //Metodo que comprueba que se ha cambiado una sola letra
        int c = 0;
        for(int i = 0; i<palabra1.length(); i++){
            if(palabra1.charAt(i) == palabra2.charAt(i)){ //Comparacin de  caracteres
                c++;
            }
        }
        return c==(palabra1.length()-1); //DEvuelve si se han cambiado todas las letars menos 1
    }
}