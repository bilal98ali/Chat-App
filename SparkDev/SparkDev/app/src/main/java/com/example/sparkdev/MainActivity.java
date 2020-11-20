package com.example.sparkdev;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.koddev.instagramtest.InstaMainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

   // private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SparkDev"); // Title.

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


    }

    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            updateUserStatus("online");

            VerifyUserExistance();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }
    
    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText( MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                   SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //MenuInflater inflater = getMenuInflater();
    //inflater.inflate(R.menu.options_menu,menu);
    //return true;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    // @Override
    // public boolean onOptionsItemSelected(MenuItem item)
    //{
    //  switch (item.getItemId()) {
    //    case R.id.main_logout_option:
    //      mAuth.signOut();
    //    SendUserToLoginActivity();
    //  return true;
    //default:
    //  return super.onOptionsItemSelected(item);
    //}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option)
        {
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_settings_option) {
            SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.main_create_group_option) {
            RequestNewGroup();
        }

        if (item.getItemId() == R.id.main_find_friends_option) {

                SendUserToFindFriendsActivity();
        }
        /*if (item.getItemId() == R.id.main_Insta_option) {

            SendUserToInstaActivity();
        }*/

        return true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name:");

        final EditText groupNameField = new EditText( MainActivity.this);
        groupNameField.setHint("e.g. Coding Cafe");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write group name...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this,  groupName + "group is created Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
//        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
//        finish();
        //task43 removes settingsIntent and finish() line so back goes back button goes back to
        //main activity.
    }

    private void SendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
      //  findFriendsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findFriendsIntent);
      //  finish();
    }
    /*private void     SendUserToInstaActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, InstaMainActivity.class);
        //  findFriendsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findFriendsIntent);
        //  finish();
    }*/


    private void updateUserStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }

}