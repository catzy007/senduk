package com.bagon.senduk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";
    public TextView Hasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);
    }

    public void ParseCsv(View v){
        try {
            AssetManager mng = getApplicationContext().getAssets();
            InputStream is = mng.open("tbl_provinsi.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                System.out.println(nextLine[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void TambahData(View v){
        Map<String, Object> sensus = new HashMap<>();
        sensus.put("rt","02");
        sensus.put("kk","61");

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
                                .add(sensus)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
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
