package com.bagon.senduk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final String TAG = "MainActivity";

    private String Usrnme, Passwd;
    EditText Ipt1, Ipt2;
    Button BtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Ipt1 = findViewById(R.id.IptUsrnme);
        Ipt2 = findViewById(R.id.IptPasswd);
        BtnLogin = findViewById(R.id.BtnLogin);

        db = FirebaseFirestore.getInstance();

        db.collection("petugas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getData().get("token").toString() + " " + readFromFile(LoginActivity.this));
                                Log.d(TAG, document.getData().get("token").toString().contains(readFromFile(LoginActivity.this))?"true":"false");
                                if(document.getData().get("token").toString().contains(readFromFile(LoginActivity.this))){
                                    Intent myIntent = new Intent(LoginActivity.this, MainMenu.class);
                                    LoginActivity.this.startActivity(myIntent);
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usrnme = Ipt1.getText().toString();
                Passwd = MD5_Hash(Ipt2.getText().toString());

                Log.d(TAG, Usrnme + " " + Passwd);

                db.collection("petugas")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getData().get("username").toString().contains(Usrnme) &&
                                        document.getData().get("password").toString().contains(Passwd)){
                                            writeToFile(document.getData().get("token").toString(),LoginActivity.this);
                                            Intent myIntent = new Intent(LoginActivity.this, MainMenu.class);
                                            LoginActivity.this.startActivity(myIntent);
                                        }else{
                                            Toast.makeText(LoginActivity.this, "Username or Password Missmatch.", Toast.LENGTH_SHORT).show();
                                            Log.w(TAG, "Username or Password Missmatch.", task.getException());
                                        }
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error getting documents.", Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });
    }

    private static void writeToFile(String data,Context context) {
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

    public static String MD5_Hash(String s) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }


}
