package com.example.sparkdev;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupChatActivity {





    public class GroupChatActivity extends AppCompatActivity{
         private String currentGroupName; // add this variable
    }

    protected void onCreate (Bundle savedInstanceState){
       // super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_group_chat);  <-- currentGroupName goes below these two

        currentGroupName = getIntent().getExtras.get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();
    }

    private void InitializeFields(){
        getSupportActionBar().setTitle(currentGroupName); // this replaces original third line of InitializeFields() method
    }
}
