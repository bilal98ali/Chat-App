package com.example.sparkdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
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

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            VerifyUserExistance();
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
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_settings_option) {
            SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.main_find_friends_option) {
        }

        return true;
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    public boolean onOptionItemSelected(MenuItem item){
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.main_logout_option){

             mAuth.signOut();
             SendUserToLoginActivity();
         }
         if(item.getItemId() == R.id.main_settings_option){

        }
        if(item.getItemId() == R.id.main_find_friends_option){

        }

        return true;
    }
}