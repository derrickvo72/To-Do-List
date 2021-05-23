package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.example.todolist.SupClass.Todo;

public class MainActivity2 extends AppCompatActivity {
    // declaration
    public EditText name, desc;
    public Button btn, cancle;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        name = findViewById(R.id.edname);
        desc = findViewById(R.id.eddes);
        btn = findViewById(R.id.button2);

        // add the code below to initialize Amplify
        try {
            // Add these lines to add the AWSApiPlugin plugins
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("MyAmplifyApp", "Initialized Amplify");
        }
        catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
        findViewById(R.id.backto).setOnClickListener(v->{
            super.onBackPressed();
        });
        // set listener on the store data button to store
        // data in dynamoDB
        btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v)
            {
                String name1 = name.getText().toString();
                String name2 = desc.getText().toString();
                // add the code below to create a toto item
                // with two properties a name and a
                // description
                if(name1.isEmpty() || name2.isEmpty()){
                    Toast.makeText(MainActivity2.this, "Please fill all fiels !", Toast.LENGTH_SHORT).show();
                }
                else{
                    Todo todo = Todo.builder()
                            .name(name1)
                            .description(name2)
                            .build();
                    // add the code below  to save item using mutate
                    Amplify.API.mutate(ModelMutation.create(todo), response -> Log.i(
                            "MyAmplifyApp", "Added Todo with id: " + response.getData().getId()),
                            error
                                    -> Log.e("MyAmplifyApp", "Create failed", error));
                    Toast.makeText(MainActivity2.this, "Saved !", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }
    // move to the next activity
    @Override public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(MainActivity2.this, MainActivity.class));
    }
}