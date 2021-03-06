package com.lion.test_rating.TeacherAccount;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import com.lion.test_rating.TeacherAccount.Fragments.FragmentInformationForTeacherAccount;
import com.lion.test_rating.TeacherAccount.Fragments.FragmentMyTestsStatusForTeachersAccount;
import com.lion.test_rating.TeacherAccount.Fragments.FragmentRatingForTeacherAccount;
import com.lion.test_rating.TeacherAccount.Fragments.FragmentTestsForTeachersAccount;

import java.util.ArrayList;

public class TeacherAccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    NavigationView navigationView;

    TextView headerName;
    TextView headerEmail;
    TextView headerOtherInformation;

    TeacherInformation teacherInformation;

    public static ArrayList<String> mListUserInformation;

    private FragmentTestsForTeachersAccount fResult;
    private FragmentInformationForTeacherAccount fInformation;
    private FragmentRatingForTeacherAccount fRating;
    private FragmentMyTestsStatusForTeachersAccount fTests;
    FragmentTransaction fragmentTransaction;

    public static int checkFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.activity_main_drawer_teachers);

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
                    Intent loginIntent = new Intent(TeacherAccountActivity.this, MainActivity.class);
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

        fResult = new FragmentTestsForTeachersAccount();
        fInformation = new FragmentInformationForTeacherAccount();
        fRating = new FragmentRatingForTeacherAccount();
        fTests = new FragmentMyTestsStatusForTeachersAccount();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fTests);
        fragmentTransaction.commit();
    }

    private void showData(DataSnapshot snapshot) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            String userID = user.getUid();

            teacherInformation = new TeacherInformation();
            DataSnapshot dataTeachers = snapshot.child(ConstantsNames.TEACHERS).child(userID);
            teacherInformation.setName((String) dataTeachers.child(ConstantsNames.FULL_NAME).getValue());
            teacherInformation.setEmail((String) dataTeachers.child(ConstantsNames.EMAIL).getValue());
            teacherInformation.setDepartment((String) dataTeachers.child(ConstantsNames.DEPARTMENT).getValue());

            mListUserInformation = new ArrayList<>();
            mListUserInformation.add(teacherInformation.getName());
            mListUserInformation.add(teacherInformation.getEmail());
            mListUserInformation.add(teacherInformation.getDepartment());

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
        headerOtherInformation.setText(mListUserInformation.get(2));
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
                fragmentTransaction.replace(R.id.container, fRating);
                fragmentTransaction.commit();
                checkFragment = 0;
            } else if (checkFragment == 2) {
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
        checkFragment = 0;

        fragmentTransaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_test_t) {
            fragmentTransaction.replace(R.id.container, fTests);
        } else if (id == R.id.nav_result_t) {
            fragmentTransaction.replace(R.id.container, fResult);
        } else if (id == R.id.nav_information_t) {
            fragmentTransaction.replace(R.id.container, fInformation);
        } else if (id == R.id.nav_rating_t) {
            fragmentTransaction.replace(R.id.container, fRating);
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

    private void errorNull() {
        Toast.makeText(this, R.string.server_connection_error, Toast.LENGTH_LONG).show();
        logout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.internet_connection_error, Toast.LENGTH_LONG).show();
        }
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
