package com.lion.test_rating.StudentAccount;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.MainActivity;
import com.lion.test_rating.R;
import com.lion.test_rating.StudentAccount.Fragments.FragmentTeachersForStudentsAccount;
import com.lion.test_rating.StudentAccount.Fragments.FragmentTestsForStudentsAccount;

public class AccountStudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    NavigationView navigationView;

    String name;
    String email;
    String course;
    String group;

    TextView headerName;
    TextView headerEmail;
    TextView headerOtherInformation;

    FragmentTransaction fragmentTransaction;
    private FragmentTestsForStudentsAccount fTest;
    private FragmentTeachersForStudentsAccount fResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.activity_main_drawer_students);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child(ConstantsNames.USERS);
        myRef.keepSynced(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent loginIntent = new Intent(AccountStudentActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };

        try {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    errorNull();
                }
            });
        } catch (NullPointerException ex) {
            errorNull();
        }

        fTest = new FragmentTestsForStudentsAccount();
        fResult = new FragmentTeachersForStudentsAccount();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fTest);
        fragmentTransaction.commit();
    }

    private void showData(DataSnapshot snapshot) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String userID = user.getUid();
            DataSnapshot dataStudents = snapshot.child(ConstantsNames.STUDENTS).child(userID);
            name = (String) dataStudents.child(ConstantsNames.FULL_NAME).getValue();
            email = (String) dataStudents.child(ConstantsNames.EMAIL).getValue();
            course = (String) dataStudents.child(ConstantsNames.COURSE).getValue();
            group = (String) dataStudents.child(ConstantsNames.GROUP).getValue();

            updateUI();

        } catch (NullPointerException ex) {
            errorNull();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        View header = navigationView.getHeaderView(0);
        headerName = header.findViewById(R.id.headerName);
        headerEmail = header.findViewById(R.id.headerEmail);
        headerOtherInformation = header.findViewById(R.id.otherInformation);
        headerName.setText(name);
        headerEmail.setText(email);
        headerOtherInformation.setText(course + " курс, " + group + " группа");
    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        fragmentTransaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_tests) {
            fragmentTransaction.replace(R.id.container, fTest);
        } else if (id == R.id.nav_result) {
            fragmentTransaction.replace(R.id.container, fResult);
        } else if (id == R.id.nav_rating) {

        } else if (id == R.id.nav_information) {

        }
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), "Нет соединения с интернетом!", Toast.LENGTH_LONG).show();
        }
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(this, "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
        logout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
