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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;


public class activitystatus extends AppCompatActivity {
    private EditText edt;
    private Button sbtn;
    private Toolbar tlb;


    private ProgressDialog pgb;

    private DatabaseReference statdb;
    private FirebaseUser statfbu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitystatus);
        final Context content =this;
        tlb = (Toolbar) findViewById(R.id.statstlb);
        edt = (TextInputEditText) findViewById(R.id.sated);
        sbtn = (Button) findViewById(R.id.satbut);

        pgb = new ProgressDialog(content);
        statfbu = FirebaseAuth.getInstance().getCurrentUser();
        String cur = statfbu.getUid();
        statdb = FirebaseDatabase.getInstance().getReference().child("users").child(cur);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pgb.setTitle("Saving Change");
                pgb.setMessage("Please wait");
                pgb.show();
                String statu = edt.getText().toString();
                if (statu.length()<=40){
                statdb.child("status").setValue(statu).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            pgb.dismiss();
                            Toast.makeText(content,"Status updated",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(content,settingaccount.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            pgb.dismiss();
                            Intent intent = new Intent(content,activitystatus.class);
                            startActivity(intent);
                            Toast.makeText(content,"some thing is wrong in saving status",Toast.LENGTH_LONG).show();
                        }
                    }
                });}
                else
                {  pgb.dismiss();
                    Intent intent = new Intent(content,activitystatus.class);
                    startActivity(intent);
                    Toast.makeText(content,"Status should not be greater then 10 word",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
