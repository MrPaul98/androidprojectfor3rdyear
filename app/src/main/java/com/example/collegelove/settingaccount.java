package com.example.collegelove;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class settingaccount extends AppCompatActivity {
    private DatabaseReference usrdb;
    private FirebaseUser curusr;
    private FirebaseAuth mAuth;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private NavigationView navigationView;

    public CircleImageView cvm;

    public TextView tv1;
    public TextView tv2;
    public TextView tv3;
    public TextView tv4;


    private Toolbar tlb;

    private static final int GALLERY_PICK = 1;

    private StorageReference everyonepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingaccount);
        mAuth = FirebaseAuth.getInstance();
        final CircleImageView cvm = (CircleImageView) findViewById(R.id.crvm);
        final Context content = this;
        tlb = (Toolbar) findViewById(R.id.setttlb);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Setting Account");
        tv1 = (TextView) findViewById(R.id.textView0);
        tv2 = (TextView) findViewById(R.id.textView1);
        tv3 = (TextView) findViewById(R.id.textView2);
        tv4 = (TextView) findViewById(R.id.textView3);

        dl =(DrawerLayout) findViewById(R.id.settingnav);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        navigationView = (NavigationView) findViewById(R.id.settingnan);
       // final Context content = this;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.setstatus :
                        Intent intent = new Intent(content, activitystatus.class);
                        startActivity(intent);
                        break;
                    case R.id.chgimg :
                        Intent pic = new Intent();
                        pic.setType("image/*");
                        pic.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(pic, "SELECT IMAGE"),GALLERY_PICK);
                        break;
                    case R.id.bak :
                        Intent intentb = new Intent(content, MainActivity.class);
                        startActivity(intentb);
                        break;
                    case R.id.Chgbio :
                        Intent intentbio =new Intent(content,chgbio.class);
                        startActivity(intentbio);
                         break;
                }
                return true;
            }
        });

        everyonepic = FirebaseStorage.getInstance().getReference();
        curusr = FirebaseAuth.getInstance().getCurrentUser();
        String cuid = curusr.getUid();
        usrdb = FirebaseDatabase.getInstance().getReference().child("users").child(cuid);
        usrdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String img = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String ide = dataSnapshot.child("regstration").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String bio = dataSnapshot.child("Bio").getValue().toString();
                tv2.setText(status);
                tv3.setText(ide);
                tv1.setText(name);
                tv4.setText(bio);
                Picasso.get().load(img).placeholder(R.drawable.addpic).into(cvm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final Context content = this;
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent intent= new Intent(content,cl2.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       // super.onOptionsItemSelected(item);
        abdt.onOptionsItemSelected(item);
     /*   final Context content = this;
        if (item.getItemId() == R.id.setstatus) {
            Intent intent = new Intent(content, activitystatus.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.chgimg) {
            Intent pic = new Intent();
            pic.setType("image/*");
            pic.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(pic, "SELECT IMAGE"),GALLERY_PICK);

        }
        if (item.getItemId() == R.id.bak) {
            Intent intent = new Intent(content, MainActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.Chgbio){
            Intent intent =new Intent(content,chgbio.class);
            startActivity(intent);
        } */
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri img = data.getData();

            CropImage.activity(img)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resulturi = result.getUri();
                final String cuid = curusr.getUid();
                final StorageReference filepath = everyonepic.child("profilepic").child(cuid+".jpg");
                filepath.putFile(resulturi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {

                              usrdb = FirebaseDatabase.getInstance().getReference().child("users").child(cuid);
                               String xx = String.valueOf(uri);
                               usrdb.child("image").setValue(xx).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                       Toast.makeText(settingaccount.this,"uploaded",Toast.LENGTH_LONG).show();
                                   }
                               });
                           }
                       });

                    }
                });
            } else {
                Exception error = result.getError();
            }
        }

    }

}
