package com.example.escaletras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Niveles extends AppCompatActivity {

    public ArrayList<String> listaNiveles = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveles);

        listaNiveles.add("Nivel 1");
        listaNiveles.add("Nivel 2");
        listaNiveles.add("Nivel 3");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaNiveles);
        ListView listView = (ListView) findViewById(R.id.viewNiveles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(Niveles.this, Juego.class);

                switch (selectedItem){
                    case "Nivel 1":
                        intent.putExtra("palabraOrigen", "CASA");
                        intent.putExtra("palabraDestino", "CAMA");
                        break;
                    case "Nivel 2":
                        intent.putExtra("palabraOrigen", "COLA");
                        intent.putExtra("palabraDestino", "CANA");
                        break;
                    case "Nivel 3":
                        intent.putExtra("palabraOrigen", "RENO");
                        intent.putExtra("palabraDestino", "TEMA");
                        break;
                    default:
                        intent.putExtra("palabraOrigen", "CASA");
                        intent.putExtra("palabraDestino", "CAMA");
                }
                Niveles.this.startActivity(intent);
            }
        });

    }

    public void volverMenu(View v){
        finish();
    }
}