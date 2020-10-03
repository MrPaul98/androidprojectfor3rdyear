package com.example.collegelove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;


import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private Toolbar tlbr;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;


    private ViewPager vpg;
    private SectionPagerAdapter secpage;

    private TabLayout tlv;
    //nav
   private CircleImageView navproimg;
   private TextView navproname;
   private TextView navprrostatus;
   private DatabaseReference userref;
   String cutid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tlbr = (Toolbar) findViewById(R.id.maintool_bar);
        setSupportActionBar(tlbr);
        getSupportActionBar().setTitle("CrushChatApp");


        vpg=(ViewPager) findViewById(R.id.pagermain);
        secpage = new SectionPagerAdapter(getSupportFragmentManager());
        vpg.setAdapter(secpage);

        tlv = (TabLayout) findViewById(R.id.maintab);
        tlv.setupWithViewPager(vpg);
        tlv.getTabAt(0).setIcon(R.drawable.chaticon);
        tlv.getTabAt(1).setIcon(R.drawable.crush);
        tlv.getTabAt(2).setIcon(R.drawable.requestfriend);
        mAuth = FirebaseAuth.getInstance();
         cutid = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("users");

        //drawnav
        dl =(DrawerLayout) findViewById(R.id.nav);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        navigationView = (NavigationView) findViewById(R.id.nan);
        View view = navigationView.inflateHeaderView(R.layout.navprofileheader);
        navproimg = (CircleImageView) view.findViewById(R.id.proimage);
        navproname = (TextView) view.findViewById(R.id.proname);
        navprrostatus = (TextView) view.findViewById(R.id.prostatus);
        userref.child(cutid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                navproname.setText(name);
                navprrostatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.addpic).into(navproimg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 usermenuselecter(item);

                return true;
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
         FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser == null)
        {

            Intent intent = new Intent(MainActivity.this,cl2.class);
            startActivity(intent);


        }



    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        abdt.onOptionsItemSelected(item);
      //  super.onOptionsItemSelected(item);
         return true;
    }
   public void usermenuselecter(MenuItem item)
   {
       final  Context content =this;
       switch (item.getItemId())
       {
           case R.id.set:
               Intent intent=new Intent(content,settingaccount.class);
               startActivity(intent);
               Toast.makeText(MainActivity.this,"setting",Toast.LENGTH_LONG).show();
             break;
             case R.id.usr:
               Intent intents=new Intent(content,alluser.class);
               startActivity(intents);
               break;
       }
   }


}
