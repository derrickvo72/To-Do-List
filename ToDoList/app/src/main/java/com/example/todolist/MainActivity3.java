package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.example.todolist.SupClass.Todo;

public class MainActivity3 extends AppCompatActivity {
    TextView id;
    EditText name,des;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        findViewById(R.id.goback).setOnClickListener(v->{
            super.onBackPressed();
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String ID = sharedPref.getString("id", "Not Available");
        id = findViewById(R.id.tvId);
        id.setText(ID);

        String NAME = sharedPref.getString("name", "Not Available");
        name = findViewById(R.id.edtName);
        name.setText(NAME);

        String DES = sharedPref.getString("des", "Not Available");
        des = findViewById(R.id.edtDes);
        des.setText(DES);

        findViewById(R.id.btnSave).setOnClickListener(v -> {

                Todo todo = Todo.justId(ID.trim()).copyOfBuilder(name.getText().toString(),des.getText().toString()).build();
            // add the code below  to save item using mutate
            Amplify.API.mutate(ModelMutation.update(todo), response -> Log.i(
                    "MyAmplifyApp", "Added Todo with id: " + response.getData().getId()),
                    error
                            -> Log.e("MyAmplifyApp", "Create failed", error));
            Toast.makeText(this, "Update !", Toast.LENGTH_SHORT).show();
//            super.onBackPressed();
            Intent intent= new Intent(MainActivity3.this, MainActivity.class);
            startActivity(intent);
        });



    }
}