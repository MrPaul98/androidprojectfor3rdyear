package com.example.collegelove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class cl3 extends AppCompatActivity {
     private Button b1;
     private EditText nm;
     private EditText em;
     private EditText pas;
     private EditText cd;
     private Toolbar tlb;

     private ProgressDialog pg1;
    private DatabaseReference dbf;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cl3);
        tlb = (Toolbar) findViewById(R.id.subbar);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("CrushChatApp New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Context content =this;
        mAuth = FirebaseAuth.getInstance();
        pg1= new ProgressDialog(content);
        nm=(EditText) findViewById(R.id.name);
        cd=(EditText) findViewById(R.id.un);
        em=(EditText) findViewById(R.id.em);
        pas=(EditText) findViewById(R.id.p);
        b1=(Button) findViewById(R.id.subgolog);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nm.getEditableText().toString();
                String collegeid = cd.getEditableText().toString();
                String email = em.getEditableText().toString();
                String password = pas.getEditableText().toString();

                check(name,collegeid,email,password);

            }
        });
    }
    private void reg(final String name, final String collegeid, String email, String password){
        final Context content = this;
        pg1.setTitle("Registration New User");
        pg1.setMessage("please wait for a moment");
        pg1.setCanceledOnTouchOutside(false);
        pg1.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String devicetoken = FirebaseInstanceId.getInstance().getToken();
                    FirebaseUser current_ur = FirebaseAuth.getInstance().getCurrentUser();
                    String uid= current_ur.getUid();
                    dbf= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    HashMap<String,String> uemap = new HashMap<>();
                    uemap.put("name",name);
                    uemap.put("regstration",collegeid);
                    uemap.put("status","Enter Status");
                    uemap.put("Bio","Enter Bio");
                    uemap.put("image","Enter image");
                    uemap.put("device_token",devicetoken);
                    dbf.setValue(uemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pg1.dismiss();
                                Intent intent= new Intent(content,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent= new Intent(content,cl3.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(content,"Some thing went wrong",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else
                {
                    Intent intent= new Intent(content,cl3.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(content,"Some thing went wrong",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private boolean check(String name,String collegeid,String email,String password)
    {   final Context content =this;
        if(name.isEmpty() || collegeid.isEmpty() || email.isEmpty() || password.isEmpty())
        {
            Intent intent= new Intent(content,cl3.class);
            startActivity(intent);
            finish();
            Toast.makeText(content,"Enter all details",Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            reg(name, collegeid, email, password);

        }
        return true;
    }

}

