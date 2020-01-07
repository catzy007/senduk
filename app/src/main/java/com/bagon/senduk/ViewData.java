package com.bagon.senduk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewData extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";

    ArrayList<String> ListDocument = new ArrayList<>();

    RecyclerviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        final ArrayList<String> HasilSensus = new ArrayList<>();
        final RecyclerView recyclerView = findViewById(R.id.MyRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        db.collection("sensus")
                .document("Aceh")
                .collection("kota")
                .document("Kabupaten Aceh Barat Daya")
                .collection("kecamatan")
                .document("Blang Pidie")
                .collection("kelurahan")
                .document("Baharu")
                .collection("rw")
                .document("01")
                .collection("rt")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                HasilSensus.add("RT : " + document.getData().get("rt").toString() +
                                        "\n" + "Jumlah Kepala keluarga : " + document.getData().get("kk").toString() +
                                        "\n" + "Jumlah Penduduk : " + document.getData().get("penduduk").toString() + "\n");
                            }
                            adapter = new RecyclerviewAdapter(ViewData.this, HasilSensus);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(ViewData.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
