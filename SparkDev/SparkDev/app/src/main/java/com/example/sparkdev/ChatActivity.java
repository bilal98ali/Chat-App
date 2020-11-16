package com.example.sparkdev;

//import android.support.v7.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private Toolbar ChatToolBar;
    private FirebaseAuth nAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        nAuth = FirebaseAuth.getInstance();
        messageSenderID = nAuth.getCurrentUser().getUid();
        //kev task43
        RootRef = FirebaseDatabase.getInstance().getReference();


        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();


        InitializeControllers();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
                //on user click, SendMessage() -kev task48
            }
        });

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Images",
                        "PDF Files",
                        "MS Word Files"

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the File");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if(i == 0){
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                        }
                        if(i == 1){
                            checker = "pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select PDF File"),438);
                        }
                        if(i == 2){
                            checker = "docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword*");
                            startActivityForResult(intent.createChooser(intent,"Select MS Word File"),438);
                        }
                    }
                });

                builder.show();

            }
        });
    }

    private void InitializeControllers() {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn) ;
        //must be same "btn" from activity_chat.xml or app crashes -kev
        MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==438 && resultCode == RESULT_OK && data !=null && data.getData() !=null){

            loadingBar.setTitle("Sending File...");
            loadingBar.setMessage("Please wait, your profile image is updating...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();

            if(!checker.equals("image")){

            }
            else if (checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;


                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID)
                        .child(messageReceiverID).push();
                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();

                        }

                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map messagePictureBody = new HashMap();
                            messagePictureBody.put("message", myUrl);
                            messagePictureBody.put("name", fileUri.getLastPathSegment());
                            messagePictureBody.put("type", checker);
                            messagePictureBody.put("from", messageSenderID);
                            messagePictureBody.put("to", messageReceiverID);
                            messagePictureBody.put("messageID", messagePushID);
                            messagePictureBody.put("time", saveCurrentTime);
                            messagePictureBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messagePictureBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messagePictureBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                    MessageInputText.setText("");
                                }
                            });


                        }
                    }
                });

            }
            else{
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Messages messages = snapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }



                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendMessage() {
        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "There is nothing to send...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;


            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID)
                    .child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }

    }
}
