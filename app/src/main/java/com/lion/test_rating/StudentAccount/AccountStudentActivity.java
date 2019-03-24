package com.lion.test_rating.StudentAccount;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.lion.test_rating.StudentAccount.Fragments.FragmentInformationForStudentsAccount;
import com.lion.test_rating.StudentAccount.Fragments.FragmentRatings;
import com.lion.test_rating.StudentAccount.Fragments.FragmentTeachersForStudentsAccount;
import com.lion.test_rating.StudentAccount.Fragments.FragmentTestsForStudentsAccount;

import java.util.ArrayList;

public class AccountStudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    NavigationView navigationView;

    StudentInformation studentInformation;

    public static ArrayList<String> mListUserInformation;

    TextView headerName;
    TextView headerEmail;
    TextView headerOtherInformation;

    private FragmentTestsForStudentsAccount fTest;
    private FragmentTeachersForStudentsAccount fResult;
    private FragmentInformationForStudentsAccount fInformation;
    private FragmentRatings fRatings;
    FragmentTransaction fragmentTransaction;

    public static int checkFragment = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                    if (dataSnapshot.exists()) {
                        showData(dataSnapshot);
                    }
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
        fInformation = new FragmentInformationForStudentsAccount();
        fRatings = new FragmentRatings();
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
            studentInformation = new StudentInformation();
            studentInformation.setName((String) dataStudents.child(ConstantsNames.FULL_NAME).getValue());
            studentInformation.setEmail((String) dataStudents.child(ConstantsNames.EMAIL).getValue());
            studentInformation.setCourse((String) dataStudents.child(ConstantsNames.COURSE).getValue());
            studentInformation.setGroup((String) dataStudents.child(ConstantsNames.GROUP).getValue());
            studentInformation.setRating((String) dataStudents.child(ConstantsNames.RATING).getValue());

            mListUserInformation = new ArrayList<>();
            mListUserInformation.add(studentInformation.getName());
            mListUserInformation.add(studentInformation.getEmail());
            mListUserInformation.add(studentInformation.getCourse());
            mListUserInformation.add(studentInformation.getGroup());
            mListUserInformation.add(studentInformation.getRating());

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
        headerName.setText(mListUserInformation.get(0));
        headerEmail.setText(mListUserInformation.get(1));
        headerOtherInformation.setText(mListUserInformation.get(2) + " курс, "
                + mListUserInformation.get(3) + " группа");
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (checkFragment == 0) {
                super.onBackPressed();
            } else if (checkFragment == 1) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, fResult);
                fragmentTransaction.commit();
                checkFragment = 0;
            }
        }
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
            fragmentTransaction.replace(R.id.container, fRatings);
        } else if (id == R.id.nav_information) {
            fragmentTransaction.replace(R.id.container, fInformation);
        } else if (id == R.id.nav_exit) {
            logout();
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mAuth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.internet_connection_error, Toast.LENGTH_LONG).show();
        }
    }

    private void errorNull() {
        Toast.makeText(this, R.string.server_connection_error, Toast.LENGTH_LONG).show();
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
