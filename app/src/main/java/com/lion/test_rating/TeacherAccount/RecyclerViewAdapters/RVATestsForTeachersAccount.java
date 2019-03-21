package com.lion.test_rating.TeacherAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;
import com.lion.test_rating.TeacherAccount.Fragments.FragmentResultsForTeachers;
import com.lion.test_rating.TeacherAccount.Fragments.FragmentStatisticsForTeachers;

import java.util.ArrayList;

public class RVATestsForTeachersAccount extends RecyclerView.Adapter<RVATestsForTeachersAccount.ViewHolder>{

    private ArrayList<String> mSubject;
    private ArrayList<String> mData;
    private ArrayList<String> mNumberTest;
    private ArrayList<String> mTopicName;
    private Context mContext;

    private AlertDialog dialog;

    private FragmentResultsForTeachers fragmentResultsForTeachers;
    private FragmentStatisticsForTeachers fragmentStatisticsForTeachers;

    public RVATestsForTeachersAccount(Context mContext, ArrayList<String> mSubject, ArrayList<String> mData
            , ArrayList<String> mNumberTest, ArrayList<String> mTopic) {
        this.mSubject = mSubject;
        this.mData = mData;
        this.mContext = mContext;
        this.mNumberTest = mNumberTest;
        this.mTopicName = mTopic;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_teachers_list_tests, parent, false);

        fragmentResultsForTeachers = new FragmentResultsForTeachers();
        fragmentStatisticsForTeachers = new FragmentStatisticsForTeachers();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.subjectTV.setText(mSubject.get(position));
        holder.dataCreateTestTV.setText(mData.get(position));
        holder.topicTV.setText(mTopicName.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
    }

    private void showDialog(final int position) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams")
        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_open_results_or_statistics, null);

        Button btnResults = mView.findViewById(R.id.btn_results);
        Button btnStatistics = mView.findViewById(R.id.btn_statistics);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("numberTest", mNumberTest.get(position));
                replaceFragment(fragmentResultsForTeachers, bundle);
                dialog.dismiss();
            }
        });

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("nameSubject", mSubject.get(position));
                bundle.putString("nameTopic", mTopicName.get(position));
                bundle.putString("numberTest", mNumberTest.get(position));
                replaceFragment(fragmentStatisticsForTeachers, bundle);
                dialog.dismiss();
            }
        });
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        Activity activity = (Activity) mContext;
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
        AccountTeacherActivity.checkFragment = 2;
        dialog.dismiss();
    }

    @Override
    public int getItemCount() {
        return mSubject.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTV, dataCreateTestTV, topicTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            subjectTV = itemView.findViewById(R.id.text_name_subject);
            topicTV = itemView.findViewById(R.id.text_topic_name);
            dataCreateTestTV = itemView.findViewById(R.id.text_data);
            parentLayout = itemView.findViewById(R.id.parent_layout_tests_for_teachers_account);
        }
    }
}
