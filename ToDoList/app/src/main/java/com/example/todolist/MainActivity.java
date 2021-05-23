package com.example.todolist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.example.todolist.SupClass.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    // declaration
    public FloatingActionButton btn, re, off;
    public ListView lv;
    public String[] st;
    ProgressDialog progressDialog;
    int i = 0;
    Handler handler;
    // the array adapter converts an ArrayList of objects
    // into View items filled into the ListView container
    ArrayAdapter<String> arrayAdapter;
    // list to store data
    public static List<Todo> ls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.activity_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        new CountDownTimer(2000,500){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                progressDialog.cancel();
            }
        }.start();
        // provide id to the layout items
        btn = findViewById(R.id.fab);
        st = new String[100];

        lv = findViewById(R.id.lt);
//
        findViewById(R.id.off).setOnClickListener(v->{
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
        findViewById(R.id.restart).setOnClickListener(v->{
            Intent intent= new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Desctiption: " + ls.get(position).getDescription())
                        .setTitle("Id: " +ls.get(position).getId())
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                               // remove(position);
                                Todo todo = Todo.justId(ls.get(position).getId());
//                Todo todo = Todo.justId(ls.get(position).getId()).copyOfBuilder("a","a").build();
                                // add the code below  to save item using mutate
                                Amplify.API.mutate(ModelMutation.delete(todo), response -> Log.i(
                                        "MyAmplifyApp", "Added Todo with id: " + response.getData().getId()),
                                        error
                                                -> Log.e("MyAmplifyApp", "Create failed", error));
                                Toast.makeText(MainActivity.this, "Deleted !", Toast.LENGTH_SHORT).show();
                                MainActivity.super.recreate();
                            }
                        })

                       .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                //now get Editor
                                SharedPreferences.Editor editor = sharedPref.edit();
                                //put your value
                                editor.putString("id",ls.get(position).getId());
                                editor.putString("name",ls.get(position).getName());
                                editor.putString("des",ls.get(position).getDescription());
                                //commits your edits
                                editor.commit();
                                Intent intent= new Intent(MainActivity.this, MainActivity3.class);
                                startActivity(intent);
                            }
                        });
                // Create the AlertDialog object and return it
                 builder.create().show();
            }
        });
        
        // set listener to the floating button which takes
        // you to the next activity where you add and sore
        // your data
        btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
        ls = new ArrayList<Todo>();
        // add the code below to initialize Amplpify
        try {
            // Add these lines to add the AWSApiPlugin plugins
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("MyAmplifyApp", "Initialized Amplify");
        }
        catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
//
        // add the code below to fetch
        // data/run queries to
        // retrieve the stored data
        //Set du lieu
        Amplify.API.query(ModelQuery.list(Todo.class), response -> {
                    for (Todo todo : response.getData()) {
                        Todo lodo = new Todo(todo.getId(),todo.getName(),todo.getDescription());
                        ls.add(lodo);
                        Log.i("MyAmplifyApp", todo.getName());
                    }
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error));

        handler = new Handler();
        final Runnable r = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run()
            {
                handler.postDelayed(this, 2000);
                List<String> a = new ArrayList<>();
                for(int i=0;i<ls.size();i++){
                    a.add(ls.get(i).getName());
                }

                arrayAdapter = new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        a);
                lv.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }
        };
        handler.postDelayed(r, 1000);
    }
    private void remove(int i){
        ls.remove(ls.get(i));
        arrayAdapter.notifyDataSetChanged();
    }

    private Activity getActivity() {
        return MainActivity.this;
    }
}