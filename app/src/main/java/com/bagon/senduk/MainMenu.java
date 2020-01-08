package com.bagon.senduk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainMenu extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";
    TextView Welcome;
    Button Btn1, Btn2, Btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        db = FirebaseFirestore.getInstance();

        Welcome = findViewById(R.id.TxtWelcome);
        Btn1 = findViewById(R.id.MBtn1);
        Btn2 = findViewById(R.id.MBtn2);
        Btn3 = findViewById(R.id.MBtn3);

        db.collection("petugas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("token").toString().contains(readFromFile(MainMenu.this))){
                                    Welcome.setText("Welcome " + document.getData().get("username").toString());
                                }
                            }
                        } else {
                            Toast.makeText(MainMenu.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        Btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        Btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SelectView.class);
                startActivity(i);
            }
        });

        Btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile("NULL",MainMenu.this);
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private static void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("ulala.cfg", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("ulala.cfg");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return "NULL";
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
