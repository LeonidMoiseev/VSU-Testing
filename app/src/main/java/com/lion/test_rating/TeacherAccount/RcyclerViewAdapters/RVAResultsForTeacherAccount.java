package com.lion.test_rating.TeacherAccount.RcyclerViewAdapters;

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

public class RVAResultsForTeacherAccount extends RecyclerView.Adapter<RVAResultsForTeacherAccount.ViewHolder>{

    private ArrayList<String> mStudents;
    private ArrayList<String> mPoints;
    private Context mContext;

    public RVAResultsForTeacherAccount(Context mContext, ArrayList<String> mStudents, ArrayList<String> mPoints) {
        this.mStudents = mStudents;
        this.mPoints = mPoints;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_teachers_list_results, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.studentTV.setText(mStudents.get(position));
        holder.pointsTV.setText(mPoints.get(position));

    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView studentTV, pointsTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            studentTV = itemView.findViewById(R.id.text_name_student);
            pointsTV = itemView.findViewById(R.id.points_result);
            parentLayout = itemView.findViewById(R.id.parent_layout_results_teachers);
        }
    }
}
