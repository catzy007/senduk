package com.bagon.senduk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";
    public TextView Hasil;
    public Spinner Spinner1, Spinner2, Spinner3, Spinner4;
    public EditText Input1, Input2, Input3, Input4;
    Button SubmitBtn;
    String SelProvId, SelKotaId, SelKecId;

    ArrayList<String> ProvId = new ArrayList<String>();
    ArrayList<String> KotaId = new ArrayList<String>();
    ArrayList<String> KecId = new ArrayList<String>();

    ArrayList<String> SpinProvince = new ArrayList<String>();
    ArrayList<String> SpinKota = new ArrayList<String>();
    ArrayList<String> SpinKecamatan = new ArrayList<String>();
    ArrayList<String> SpinKelurahan = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);
        final View MyView = findViewById(android.R.id.content).getRootView(); //store view


        SubmitBtn = findViewById(R.id.BtnSubmit);
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TambahData(MyView);
            }
        });

        Spinner1 = findViewById(R.id.Province);
        Spinner2 = findViewById(R.id.Kota);
        Spinner3 = findViewById(R.id.Kecamatan);
        Spinner4 = findViewById(R.id.Kelurahan);
        ParseProvince(MyView); //load parser 1

        //Provinsi
        Spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int Selected1 = Spinner1.getSelectedItemPosition() + 1;
                String SelProv = String.valueOf(Selected1);
                SelProvId = ProvId.get(Selected1);
                Log.d(TAG, "Provinsi " + SelProvId + SelProv);
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
                Log.d(TAG, "Kota " + SelKota + SelKotaId);
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
                Log.d(TAG, "Kota " + SelKec + SelKecId);
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
                Log.d(TAG, "Kelurahan " + SelKel);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // lala nothing
            }
        });
    }

    public void ParseProvince(View v){
        Spinner1 = findViewById(R.id.Province);
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
        Spinner2 = findViewById(R.id.Kota);
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
        Spinner3 = findViewById(R.id.Kecamatan);
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
        Spinner4 = findViewById(R.id.Kelurahan);
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

    public void TambahData(View v){
        final String SubProv, SubKota, SubKec, SubKel, SubRw, SubRt, SubKk, SubPend;
        Spinner1 = findViewById(R.id.Province);
        Spinner2 = findViewById(R.id.Kota);
        Spinner3 = findViewById(R.id.Kecamatan);
        Spinner4 = findViewById(R.id.Kelurahan);
        Input1 = findViewById(R.id.IptRw);
        Input2 = findViewById(R.id.IptRt);
        Input3 = findViewById(R.id.IptKk);
        Input4 = findViewById(R.id.IptPnd);

        SubProv = Spinner1.getSelectedItem().toString();
        SubKota = Spinner2.getSelectedItem().toString();
        SubKec = Spinner3.getSelectedItem().toString();
        SubKel = Spinner4.getSelectedItem().toString();
        SubRw = Input1.getText().toString();
        SubRt = Input2.getText().toString();
        SubKk = Input3.getText().toString();
        SubPend = Input4.getText().toString();

        final Map<String, Object> sensus = new HashMap<>();
        sensus.put("rt",SubRt);
        sensus.put("kk",SubKk);
        sensus.put("penduduk",SubPend);

    //cek if data redundant
        db.collection("sensus")
                .document(SubProv)
                .collection("kota")
                .document(SubKota)
                .collection("kecamatan")
                .document(SubKec)
                .collection("kelurahan")
                .document(SubKel)
                .collection("rw")
                .document(SubRw)
                .collection("rt")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("rt"));
                                if(document.getData().get("rt").toString().contains(SubRt)){
                                    Toast.makeText(MainActivity.this, "Data Redundant", Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Data Redundant");
                                }else{
    //submit data if not redundant
                                    db.collection("sensus")
                                            .document(SubProv)
                                            .collection("kota")
                                            .document(SubKota)
                                            .collection("kecamatan")
                                            .document(SubKec)
                                            .collection("kelurahan")
                                            .document(SubKel)
                                            .collection("rw")
                                            .document(SubRw)
                                            .collection("rt")
                                            .add(sensus)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(MainActivity.this, "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "Error adding document", Toast.LENGTH_SHORT).show();
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void TampilData(View v){
        Hasil = (TextView)findViewById(R.id.mashok);

        db.collection("sensus")
        .document("jawatengah")
            .collection("kota")
            .document("semarang")
                .collection("kecamatan")
                .document("tembalang")
                    .collection("kelurahan")
                    .document("sendangmulyo")
                        .collection("rw")
                        .document("01")
                            .collection("rt")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ///Hasil.setText(document.getData().get("NIM").toString());
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                }else{
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                                }
                            });
    }
}
