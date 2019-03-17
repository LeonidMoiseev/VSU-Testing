package com.lion.test_rating.TeacherAccount.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lion.test_rating.R;

import java.util.ArrayList;

public class RVARatingOnGroupForTeacherAccount extends RecyclerView.Adapter<RVARatingOnGroupForTeacherAccount.ViewHolder>{

    private ArrayList<String> mListPlaceNumber;
    private ArrayList<String> mListNameStudent;
    private ArrayList<String> mListStudentRating;
    private Context mContext;

    public RVARatingOnGroupForTeacherAccount(Context mContext, ArrayList<String> mNumber, ArrayList<String> mName
            , ArrayList<String> mStudentRating) {
        this.mListPlaceNumber = mNumber;
        this.mListNameStudent = mName;
        this.mListStudentRating = mStudentRating;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_teachers_list_rating_on_group, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.numberTV.setText(mListPlaceNumber.get(position));
        holder.nameStudentTV.setText(mListNameStudent.get(position));
        holder.ratingTV.setText("Рейтинг: " + mListStudentRating.get(position));

    }

    @Override
    public int getItemCount() {
        return mListPlaceNumber.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView numberTV, nameStudentTV, ratingTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            numberTV = itemView.findViewById(R.id.place_number);
            nameStudentTV = itemView.findViewById(R.id.text_name_student);
            ratingTV = itemView.findViewById(R.id.text_rating);
            parentLayout = itemView.findViewById(R.id.parent_layout_rating_on_group);
        }
    }
}
