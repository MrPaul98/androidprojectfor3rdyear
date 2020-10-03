package com.example.collegelove;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat extends AppCompatActivity {
    private String messagerecid,messagercname,messegecimage,messagesenderid;
    private EditText ed;
    private TextView username,userlastseen;
    private CircleImageView userimage;
    private Toolbar tlv;
    private Button messagesendbut;
    private ImageButton sendimagebutton;
    private FirebaseAuth mAth;
    private DatabaseReference rootref;
    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private messageadapter messageadapterx;
    private RecyclerView usermessagelist;
    private String checker="",myurl="";
    private StorageTask uploadtask;
    private Uri fileUri;

    private String saveCurrentTime,saveCurrentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAth = FirebaseAuth.getInstance();
        messagesenderid = mAth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();
        messagerecid = getIntent().getExtras().get("userid").toString();
        messagercname = getIntent().getExtras().get("username").toString();
        messegecimage = getIntent().getExtras().get("userimg").toString();


        Intializecontroler();
        username.setText(messagercname);
        Picasso.get().load(messegecimage).placeholder(R.drawable.addpic).into(userimage);
        messagesendbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
     //   DisplayLastseen();
        sendimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options [] = new CharSequence[]
                        {
                           "Image"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(chat.this);
                builder.setTitle("Select");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (i==0)
                        {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"select Image"),438);
                        }

                    }
                });
                builder.show();
            }
        });

    }

    private void Intializecontroler()
    {
        tlv= (Toolbar) findViewById(R.id.chatappbar);
        setSupportActionBar(tlv);
        getSupportActionBar().setTitle("");
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.chatbar,null);
        actionbar.setCustomView(actionBarView);
        username = findViewById(R.id.tochatname);
        userimage=findViewById(R.id.tochatpic);
        messagesendbut = (Button) findViewById(R.id.chatbut);
        sendimagebutton = (ImageButton) findViewById(R.id.senderfilebutton);
        userlastseen = (TextView) findViewById(R.id.tochatlastseen);
        ed = (EditText) findViewById(R.id.chathere);
        ed.setScroller(new Scroller(chat.this));
        ed.setMaxLines(1);
        ed.setVerticalScrollBarEnabled(true);
        ed.setMovementMethod(new ScrollingMovementMethod());
        messageadapterx = new messageadapter(messageList);
        usermessagelist = (RecyclerView) findViewById(R.id.privatemessage);
        linearLayoutManager = new LinearLayoutManager(this);
        usermessagelist.setLayoutManager(linearLayoutManager);
        usermessagelist.setAdapter(messageadapterx);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        saveCurrentDate = currentDate.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fileUri = data.getData();
             if (checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image_file");
                final String messagesenderref = "messages/" + messagesenderid + "/" + messagerecid;
                final String messagereciverref = "messages/" +messagerecid + "/" +  messagesenderid ;
                DatabaseReference usermessagekeyref = rootref.child("messages")
                        .child(messagesenderid).child(messagerecid).push();
                final String messagepushid = usermessagekeyref.getKey();
                final StorageReference filepath = storageReference.child(messagepushid + "." + "jpg");
                uploadtask = filepath.putFile(fileUri);
                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw  task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            Uri downloadUrl = task.getResult();
                            myurl = downloadUrl.toString();
                            Map messagetextbody = new HashMap();
                            messagetextbody.put("message", myurl);
                            messagetextbody.put("name", fileUri.getLastPathSegment());
                            messagetextbody.put("type", checker);
                            messagetextbody.put("from", messagesenderid);
                            messagetextbody.put("to",messagerecid);
                            messagetextbody.put("messageID",messagepushid);
                            messagetextbody.put("time",saveCurrentTime);
                            messagetextbody.put("date",saveCurrentDate);
                            Map messagebodydetail = new HashMap();
                            messagebodydetail.put(messagesenderref + "/" + messagepushid,messagetextbody);
                            messagebodydetail.put(messagereciverref + "/" + messagepushid,messagetextbody);
                            Toast.makeText(chat.this,"sending....",Toast.LENGTH_SHORT).show();
                            rootref.updateChildren(messagebodydetail).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(chat.this,"send",Toast.LENGTH_LONG).show();

                                    }
                                    else
                                    {
                                        Toast.makeText(chat.this,"Message Error",Toast.LENGTH_LONG).show();
                                    }
                                    ed.setText("");

                                }
                            });
                        }
                    }
                });
            }
            else
            {
                //nothing error
            }
        }
    }

    /*  private void DisplayLastseen()
    {
        rootref.child("users").child(messagerecid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("userstate").hasChild("state"))
                {
                    String state = dataSnapshot.child("userstate").child("state").getValue().toString();
                    String date = dataSnapshot.child("userstate").child("date").getValue().toString();
                    String time = dataSnapshot.child("userstate").child("time").getValue().toString();

                    if (state.equals("online"))
                    {
                        userlastseen.setText("online");
                    }
                    else if (state.equals("offline"))
                    {
                        userlastseen.setText(date + " " + time);
                    }
                }
                else
                {
                    userlastseen.setText("offline");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    @Override
    protected void onStart() {
        super.onStart();



        rootref.child("messages").child(messagesenderid).child(messagerecid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages mess= dataSnapshot.getValue(Messages.class);
                messageList.add(mess);
                messageadapterx.notifyDataSetChanged();
                usermessagelist.smoothScrollToPosition(usermessagelist.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void sendmessage()
    {
        String messagetext = ed.getText().toString();
        if (TextUtils.isEmpty(messagetext))
        {
            Toast.makeText(chat.this,"please enter message",Toast.LENGTH_LONG).show();
        }
        else
        {
            String messagesenderref = "messages/" + messagesenderid + "/" + messagerecid;
            String messagereciverref = "messages/" +messagerecid + "/" +  messagesenderid ;
            DatabaseReference usermessagekeyref = rootref.child("messages").child(messagesenderid).child(messagerecid).push();
            String messagepushid = usermessagekeyref.getKey();
            Map messagetextbody = new HashMap();
            messagetextbody.put("message", messagetext);
            messagetextbody.put("type", "text");
            messagetextbody.put("from", messagesenderid);
            messagetextbody.put("to",messagerecid);
            messagetextbody.put("messageID",messagepushid);
            messagetextbody.put("time",saveCurrentTime);
            messagetextbody.put("date",saveCurrentDate);
            Map messagebodydetail = new HashMap();
            messagebodydetail.put(messagesenderref + "/" + messagepushid,messagetextbody);
            messagebodydetail.put(messagereciverref + "/" + messagepushid,messagetextbody);
            Toast.makeText(chat.this,"sending....",Toast.LENGTH_SHORT).show();
            rootref.updateChildren(messagebodydetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(chat.this,"send",Toast.LENGTH_LONG).show();
                        ed.setText("");
                    }
                    else
                    {
                        Toast.makeText(chat.this,"Message Error",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

}
