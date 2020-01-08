package com.bagon.senduk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SelectView extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;
    public Spinner Spinner1, Spinner2, Spinner3, Spinner4, Spinner5;
    Button SubmitBtn;

    String SelProvId, SelKotaId, SelKecId;

    ArrayList<String> ProvId = new ArrayList<String>();
    ArrayList<String> KotaId = new ArrayList<String>();
    ArrayList<String> KecId = new ArrayList<String>();

    ArrayList<String> SpinProvince = new ArrayList<String>();
    ArrayList<String> SpinKota = new ArrayList<String>();
    ArrayList<String> SpinKecamatan = new ArrayList<String>();
    ArrayList<String> SpinKelurahan = new ArrayList<String>();

    ArrayList<String> ListDocument = new ArrayList<>();

    RecyclerviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_view);
        db = FirebaseFirestore.getInstance();
        final View MyView = findViewById(android.R.id.content).getRootView(); //store view

        SubmitBtn = findViewById(R.id.SubmitView);

        Spinner1 = findViewById(R.id.SpinView1);
        Spinner2 = findViewById(R.id.SpinView2);
        Spinner3 = findViewById(R.id.SpinView3);
        Spinner4 = findViewById(R.id.SpinView4);

        ParseProvince(MyView); //load parser 1

        SubmitBtn = findViewById(R.id.SubmitView);
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> HasilSensus = new ArrayList<>();
                final RecyclerView recyclerView = findViewById(R.id.MyRecyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(SelectView.this));

                db = FirebaseFirestore.getInstance();
                db.collection("sensus")
                        .document(Spinner1.getSelectedItem().toString())
                        .collection("kota")
                        .document(Spinner2.getSelectedItem().toString())
                        .collection("kecamatan")
                        .document(Spinner3.getSelectedItem().toString())
                        .collection("kelurahan")
                        .document(Spinner4.getSelectedItem().toString())
                        .collection("rw")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        HasilSensus.add("RW : " + document.getData().get("rw").toString() +
                                                "\n" + "RT : " + document.getData().get("rt").toString() +
                                                "\n" + "Jumlah Kepala keluarga : " + document.getData().get("kk").toString() +
                                                "\n" + "Jumlah Penduduk : " + document.getData().get("penduduk").toString() + "\n");
                                    }
                                    adapter = new RecyclerviewAdapter(SelectView.this, HasilSensus);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    Toast.makeText(SelectView.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        //Provinsi
        Spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int Selected1 = Spinner1.getSelectedItemPosition() + 1;
                String SelProv = String.valueOf(Selected1);
                SelProvId = ProvId.get(Selected1);
                Log.d(TAG, "Provinsi " + SelProvId + " " + SelProv);
                ParseKota(MyView, SelProvId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // lala nothing
            }
        });

        //Kota
        Spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int Selected2 = Spinner2.getSelectedItemPosition() + 1;
                String SelKota = String.valueOf(Selected2);
                SelKotaId = KotaId.get(Selected2);
                Log.d(TAG, "Kota " + SelKota + " " + SelKotaId);
                ParseKecamatan(MyView, SelKotaId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // lala nothing
            }
        });

        //Kecamatan
        Spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int Selected3 = Spinner3.getSelectedItemPosition() + 1;
                String SelKec = String.valueOf(Selected3);
                SelKecId = KecId.get(Selected3);
                Log.d(TAG, "Kota " + SelKec + " " + SelKecId);
                ParseKelurahan(MyView, SelKecId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // lala nothing
            }
        });

        //Kelurahan
        Spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String SelKel = String.valueOf(Spinner4.getSelectedItemPosition() + 1);
                Log.d(TAG, "Kelurahan " + " " + SelKel);
                Log.d(TAG, Spinner1.getSelectedItem().toString() + Spinner2.getSelectedItem().toString() +
                        Spinner3.getSelectedItem().toString() + Spinner4.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // lala nothing
            }
        });
    }

    public void ParseProvince(View v){
        Spinner1 = findViewById(R.id.SpinView1);
        try {
            AssetManager mng = getApplicationContext().getAssets();
            InputStream is = mng.open("tbl_provinsi.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                //System.out.println(nextLine[1]);
                SpinProvince.add(nextLine[1]); //store data to spinprovince
                ProvId.add(nextLine[0]);
            }
            SpinProvince.remove(0);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SpinProvince);
            Spinner1.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void ParseKota(View v, String ProvId){
        SpinKota.removeAll(SpinKota); //clear array before parsing other value
        KotaId.removeAll(KotaId);
        Spinner2 = findViewById(R.id.SpinView2);
        try {
            AssetManager mng = getApplicationContext().getAssets();
            InputStream is = mng.open("tbl_kabkot.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[1].equals(ProvId)) {
                    //System.out.println(nextLine[2]);
                    SpinKota.add(nextLine[2]); //store data to spinprovince
                    KotaId.add(nextLine[0]);
                }
            }
            SpinKota.remove(0);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SpinKota);
            Spinner2.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void ParseKecamatan(View v, String Kotaid){
        Spinner3 = findViewById(R.id.SpinView3);
        SpinKecamatan.removeAll(SpinKecamatan);
        KecId.removeAll(KecId);
        try {
            AssetManager mng = getApplicationContext().getAssets();
            InputStream is = mng.open("tbl_kecamatan.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[1].equals(Kotaid)) {
                    //System.out.println(nextLine[2]);
                    SpinKecamatan.add(nextLine[2]); //store data
                    KecId.add(nextLine[0]);
                }
            }
            SpinKecamatan.remove(0);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SpinKecamatan);
            Spinner3.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void ParseKelurahan(View v, String KecId){
        Spinner4 = findViewById(R.id.SpinView4);
        SpinKelurahan.removeAll(SpinKelurahan);
        try {
            AssetManager mng = getApplicationContext().getAssets();
            InputStream is = mng.open("tbl_kelurahan.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[1].equals(KecId)) {
                    //System.out.println(nextLine[2]);
                    SpinKelurahan.add(nextLine[2]); //store data
                }
            }
            SpinKelurahan.remove(0);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SpinKelurahan);
            Spinner4.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }
}
