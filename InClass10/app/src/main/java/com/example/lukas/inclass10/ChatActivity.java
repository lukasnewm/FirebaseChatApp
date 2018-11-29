package com.example.lukas.inclass10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.LineNumberReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;

    ListView listViewChat;
    TextView textViewName;
    EditText editTextMessage;
    ImageButton imageButtonLogOut;
    ImageButton imageButtonFile;
    ImageButton imageButtonSend;
    String Fname, Lname, userID, sender, time, dURL, messageID;
    String message;
    DatabaseReference messages;
    Bitmap image;
    boolean selectedImage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Chat Room");

        listViewChat = findViewById(R.id.ListViewChat);
        textViewName = findViewById(R.id.textViewName);
        editTextMessage = findViewById(R.id.editTextChatMessage);
        imageButtonFile = findViewById(R.id.imageButtonFile);
        imageButtonLogOut = findViewById(R.id.imageButtonChatLogOut);
        imageButtonSend = findViewById(R.id.imageButtonChatSend);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference name = mDatabase.getReference("Users/" + mUser.getUid());
        messages = mDatabase.getReference("Messages");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        image = null;

        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Fname = user.FName;
                Lname = user.LName;
                textViewName.setText(Fname + " " + Lname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                Intent goToLogin = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(goToLogin);
            }
        });

        /*
        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = editTextMessage.getText().toString();

                //Not mine
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] data = baos.toByteArray();
                //end not mine

                if (message.length() == 0) {
                    Toast.makeText(ChatActivity.this, "Enter a Message", Toast.LENGTH_SHORT).show();
                } else {
                    userID = mAuth.getUid();
                    sender = Fname + Lname;
                    time = String.valueOf(System.currentTimeMillis());
                    image = null;

                    if (image != null) {
                        Uri file = Uri.fromFile(new File(""));

                        StorageReference imageRef = mStorageRef.child("images/");

                        imageRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                dURL = downloadUrl.toString();
                            }
                        });
                    }
                    Message sendMess = new Message(message, sender, time, dURL, userID);

                    messages.push().setValue(sendMess);
                }
            }
        });
        */

        final int REQUEST_LOAD = 1;

        imageButtonFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImage = true;
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_LOAD);
            }
        });

        messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<Message> messList = new ArrayList<>();
                final ArrayList<String> messID = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    messList.add(snapshot.getValue(Message.class));
                }
                MessageAdapter messageAdapter = new MessageAdapter(ChatActivity.this, R.layout.chat, messList);
                listViewChat.setAdapter(messageAdapter);
                editTextMessage.setText("");
                message = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = editTextMessage.getText().toString();
                userID = mAuth.getUid();
                sender = Fname +  " " + Lname;
                Date dt = new Date();
                DateFormat df = new android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                time = df.format(dt).toString();

                if(selectedImage == true){
                    getImageFromButton();
                } else {
                    if (message.length() == 0) {
                        Toast.makeText(ChatActivity.this, "Enter a Message or Select an Image", Toast.LENGTH_SHORT).show();
                    } else {
                        dURL = "";
                        messageID = messages.push().getKey();
                        Message mess = new Message(message, sender, time, dURL, userID, messageID);
                        messages.child(messageID).setValue(mess);
                    }
                }
            }
        });
    }

    public void getImageFromButton(){
        imageButtonFile.setDrawingCacheEnabled(true);
        imageButtonFile.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageButtonFile.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final String key = messages.push().getKey();
        StorageReference imageRef = mStorageRef.child("images/" + key + "/jpeg");

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String Url = task.getResult().toString();
                        //dURL = (Url);//.getMetadata() contains file metadata such as size, content-type, etc.
                        Message mess = new Message(message, sender, time, Url, userID, key);
                        messages.child(key).setValue(mess);
                        image = BitmapFactory.decodeResource(getResources(), R.drawable.addimage);
                        imageButtonFile.setImageBitmap(image);
                    }
                });
            }
        });
        selectedImage = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    Log.d("Mess","Here result");
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    imageButtonFile.setImageBitmap(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}