package com.lion.test_rating.TeacherAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;

import java.util.ArrayList;

public class RVAInformationForTeacherAccount extends RecyclerView.Adapter<RVAInformationForTeacherAccount.ViewHolder>{

    private ArrayList<String> mDateName;
    private ArrayList<String> mInformation;
    private ArrayList<String> mCoursesAndGroups;
    private ArrayList<String> mNumberInformationBlock;
    private Context mContext;

    private AlertDialog dialog;

    public RVAInformationForTeacherAccount(Context mContext, ArrayList<String> mInfo
            , ArrayList<String> mCoursesAndGroups, ArrayList<String> mDate, ArrayList<String> mNumber) {
        this.mContext = mContext;
        this.mInformation = mInfo;
        this.mCoursesAndGroups = mCoursesAndGroups;
        this.mDateName = mDate;
        this.mNumberInformationBlock = mNumber;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_teachers_list_information, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.infoTV.setText(mInformation.get(position));
        holder.coursesAndGroupsTV.setText(mCoursesAndGroups.get(position));
        holder.dateTV.setText(mDateName.get(position));

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
        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_delete_information, null);

        Button btnNo = mView.findViewById(R.id.btn_no);
        Button btnYes = mView.findViewById(R.id.btn_yes);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteInfo(position);
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void deleteInfo(final int position) {
        DatabaseReference mDatabaseInfo = FirebaseDatabase.getInstance().getReference();
        mDatabaseInfo.keepSynced(true);
        mDatabaseInfo.child(ConstantsNames.INFORMATION).child(AccountTeacherActivity.mListUserInformation.get(0)).child(mNumberInformationBlock.get(position)).removeValue();
    }

    @Override
    public int getItemCount() {
        return mInformation.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView infoTV, coursesAndGroupsTV, dateTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            infoTV = itemView.findViewById(R.id.text_information);
            coursesAndGroupsTV = itemView.findViewById(R.id.text_course_and_group);
            dateTV = itemView.findViewById(R.id.text_date);
            parentLayout = itemView.findViewById(R.id.parent_layout_information_for_teachers_account);
        }
    }
}
