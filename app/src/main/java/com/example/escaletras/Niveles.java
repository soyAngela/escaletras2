package com.example.escaletras;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Niveles extends AppCompatActivity {
    private Activity activityNiveles = this;
    ArrayAdapter<String> adapter;
    MyDatabaseHelper miDB;
    ArrayList<String> nivel, origen, destino;

    Button botonNuevo;
    Button botonReset;

    DialogFragment dialogoReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveles);

        botonNuevo = findViewById(R.id.botonNuevo);
        botonReset = findViewById(R.id.botonReset);

        miDB = new MyDatabaseHelper(Niveles.this);
        nivel = new ArrayList<>();
        origen = new ArrayList<>();
        destino = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nivel);
        ListView listView = (ListView) findViewById(R.id.viewNiveles);
        listView.setAdapter(adapter);

        dialogoReset = new ResetDialogo();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(Niveles.this, Juego.class);
                intent.putExtra("palabraOrigen", origen.get(position));
                intent.putExtra("palabraDestino", destino.get(position));

                Niveles.this.startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Niveles.this, EditarNivel.class);
                intent.putExtra("nivel", nivel.get(i));
                intent.putExtra("palabraOrigen", origen.get(i));
                intent.putExtra("palabraDestino", destino.get(i));

                activityNiveles.startActivityForResult(intent, 1);
                return true;
            }
        });

        botonNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Niveles.this, EditarNivel.class);
                intent.putExtra("nivel", "nuevo");

                activityNiveles.startActivityForResult(intent, 1);
            }
        });

        botonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoReset.show(getSupportFragmentManager(), "etiquetaDialogo");
                adapter.notifyDataSetChanged();
                recreate();

            }
        });

        guardarDatos();
        adapter.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1){
            recreate();
        }
    }

    public void guardarDatos(){
        Cursor cursor = miDB.leerNiveles();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No hay datos.", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                nivel.add(cursor.getString(0));
                origen.add(cursor.getString(1));
                destino.add(cursor.getString(2));
            }
        }
    }
}