package com.example.collegelove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class passwordreset extends AppCompatActivity {
     private EditText mail;
     private Button but;
     private Toolbar tlb;
     private FirebaseAuth mAth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordreset);
        tlb = (Toolbar) findViewById(R.id.logbar);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Password Reset");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAth = FirebaseAuth.getInstance();
        mail = (EditText) findViewById(R.id.emailenter);
        but = (Button) findViewById(R.id.passwordresetbutton);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setemail = mail.getText().toString();
                if (setemail.isEmpty())
                {
                    Toast.makeText(passwordreset.this,"Enter an Email",Toast.LENGTH_LONG).show();
                }
                else
                {
                    mAth.sendPasswordResetEmail(setemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful())
                         {
                             Intent intent = new Intent(passwordreset.this,cl2.class);
                             startActivity(intent);
                             Toast.makeText(passwordreset.this,"Password reset link send to your email",Toast.LENGTH_LONG).show();
                             finish();
                         }
                         else
                         {
                             Toast.makeText(passwordreset.this,"Enter a Valid Email",Toast.LENGTH_LONG).show();
                         }
                        }
                    });
                }
            }
        });
    }
}
