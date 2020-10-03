package com.example.collegelove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class cl2 extends AppCompatActivity {
     private Button b1;
     private Button b2;
     private EditText et1;
     private EditText et2;
     private CheckBox cc;

     private TextView forgotpassword;
     private Toolbar tlb;
     private ProgressDialog logpro;

    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cl2);
        mAuth = FirebaseAuth.getInstance();
        final Context context=this;
        tlb = (Toolbar) findViewById(R.id.logbar);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("CrushChatApp Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        userref = FirebaseDatabase.getInstance().getReference().child("users");
        logpro= new ProgressDialog (context);
        addonclickListener();
        et1=(EditText) findViewById(R.id.loge);
        et2=(EditText) findViewById(R.id.logp);
        b2=(Button) findViewById(R.id.logbut);
        cc=(CheckBox) findViewById(R.id.ck);

        forgotpassword = (TextView) findViewById(R.id.linkfor);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaillog = et1.getText().toString();
                String passwordlog = et2.getText().toString();
                logcheck(emaillog,passwordlog);

            }
        });


        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(cl2.this,passwordreset.class);
                startActivity(intent);

            }
        });
        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkboxx = preferences.getString("remember","");
        if (checkboxx.equals("true"))
        {
            Intent intent= new Intent (cl2.this,MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(cl2.this,"Login",Toast.LENGTH_LONG).show();

        }
        else if (checkboxx.equals("false"))
        {
            Toast.makeText(cl2.this,"Please Login",Toast.LENGTH_LONG).show();
        }


        cc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String emaillog = et1.getText().toString();
                String passwordlog = et2.getText().toString();
                if (buttonView.isChecked())
                {


                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(cl2.this, "Checked", Toast.LENGTH_LONG).show();



                }
                else if (!buttonView.isChecked())
                {
                    if(emaillog.isEmpty() || passwordlog.isEmpty()){
                        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember","false");
                        editor.apply();
                        Toast.makeText(cl2.this,"Unchecked",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }



    public void addonclickListener()
    {        final Context content = this;
        b1 = (Button) findViewById(R.id.createaccbut);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(content,cl3.class);
                startActivity(intent);
            }
        });
    }
    private void loginuser(String emaillog,String passwordlog)
    {    final Context content = this;
        logpro.setTitle("Login");
        logpro.setMessage("Wait until we verify you");
        logpro.setCanceledOnTouchOutside(false);
        logpro.show();

        mAuth.signInWithEmailAndPassword(emaillog,passwordlog).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful())
              {
                  String curuid = mAuth.getCurrentUser().getUid();
                  String devicetoken = FirebaseInstanceId.getInstance().getToken();
                  userref.child(curuid).child("device_token").setValue(devicetoken).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           Intent intent= new Intent (content,MainActivity.class);
                           startActivity(intent);
                           finish();
                           Toast.makeText(content,"Login Successful",Toast.LENGTH_LONG).show();

                       }
                      }
                  });

              }
              else
              {
                  Intent intent = new Intent(content,cl2.class);
                  startActivity(intent);
                  finish();
                  Toast.makeText(content,"Login failed please check the data enterd",Toast.LENGTH_LONG).show();

              }
            }
        });
    }
    private boolean logcheck(String emaillog,String passwordlog)
    {   final Context content = this;
        if(emaillog.isEmpty() || passwordlog.isEmpty())
        {
            Intent intent = new Intent(content,cl2.class);
            startActivity(intent);
            finish();
            Toast.makeText(content,"Login failed please check the data enterd",Toast.LENGTH_LONG).show();
            return false;
        }
        else {

            loginuser(emaillog,passwordlog);
        }
        return true;
    }

}
