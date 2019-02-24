package com.lion.test_rating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapterResultsStudents extends RecyclerView.Adapter<RecyclerViewAdapterResultsStudents.ViewHolder>{

    private ArrayList<String> mSubject;
    private ArrayList<String> mData;
    private ArrayList<String> mPoints;
    private Context mContext;

    RecyclerViewAdapterResultsStudents(Context mContext, ArrayList<String> mSubject
            , ArrayList<String> mData, ArrayList<String> mPoints) {
        this.mSubject = mSubject;
        this.mData = mData;
        this.mPoints = mPoints;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_results_students, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.subjectTV.setText(mSubject.get(position));
        holder.dataCreateTestTV.setText(mData.get(position));
        holder.pointsTV.setText(mPoints.get(position));

    }

    @Override
    public int getItemCount() {
        return mSubject.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTV, pointsTV, dataCreateTestTV;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            subjectTV = itemView.findViewById(R.id.text_name_subject);
            pointsTV = itemView.findViewById(R.id.points_result);
            dataCreateTestTV = itemView.findViewById(R.id.text_data);
            parentLayout = itemView.findViewById(R.id.parent_layout_results_students);
        }
    }
}