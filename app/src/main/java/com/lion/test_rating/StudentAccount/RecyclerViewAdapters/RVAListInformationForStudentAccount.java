package com.lion.test_rating.StudentAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lion.test_rating.R;

import java.util.ArrayList;

public class RVAListInformationForStudentAccount extends RecyclerView.Adapter<RVAListInformationForStudentAccount.ViewHolder>{

    private ArrayList<String> mTeacherName;
    private ArrayList<String> mInformation;
    private ArrayList<String> mDate;
    private Context mContext;

    public RVAListInformationForStudentAccount(Context mContext, ArrayList<String> mTeacher
            , ArrayList<String> mInfo, ArrayList<String> mDate) {
        this.mTeacherName = mTeacher;
        this.mInformation = mInfo;
        this.mDate = mDate;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_students_list_information, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.nameTV.setText(mTeacherName.get(position));
        holder.infoTV.setText(mInformation.get(position));
        holder.dateTV.setText(mDate.get(position));

    }

    @Override
    public int getItemCount() {
        return mInformation.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV, infoTV, dateTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.text_name_teacher);
            infoTV = itemView.findViewById(R.id.text_information);
            dateTV = itemView.findViewById(R.id.text_date);
            parentLayout = itemView.findViewById(R.id.parent_layout_information_for_students_account);
        }
    }
}
