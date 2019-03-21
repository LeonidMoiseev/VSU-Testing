package com.lion.test_rating.TeacherAccount.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.lion.test_rating.ConstantsNames;
import com.lion.test_rating.R;
import com.lion.test_rating.TeacherAccount.AccountTeacherActivity;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentStatisticsForTeachers extends Fragment {

    String nameSubject;
    String topicName;
    String numberTest;

    int assessment_2;
    int assessment_3;
    int assessment_4;
    int assessment_5;

    ArrayList<Integer> listAssessment = new ArrayList<>();

    DatabaseReference statisticsDatabase;

    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_for_teacher_statistics, container, false);

        getActivity().setTitle(getString(R.string.statistics_item));

        nameSubject = this.getArguments().getString("nameSubject");
        topicName = this.getArguments().getString("nameTopic");
        numberTest = this.getArguments().getString("numberTest");

        statisticsDatabase = FirebaseDatabase.getInstance().getReference().child(ConstantsNames.RESULTS).
                child(AccountTeacherActivity.mListUserInformation.get(0)).child(numberTest);
        statisticsDatabase.keepSynced(true);

        try {
            statisticsDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        assessment_2 = 0;
                        assessment_3 = 0;
                        assessment_4 = 0;
                        assessment_5 = 0;
                        countAssessment(dataSnapshot);
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

        return fragmentView;
    }

    private void countAssessment(DataSnapshot dataSnapshot) {
        for (DataSnapshot courses : dataSnapshot.getChildren()) {
            if (courses.hasChildren()) {
                for (DataSnapshot groups : courses.getChildren()) {
                    for (DataSnapshot students : groups.getChildren()) {
                        int points = Integer.parseInt((String) students.getValue());
                        if (points >= 0 && points <= 24) {
                            assessment_2++;
                        } else if (points > 24 && points <= 34) {
                            assessment_3++;
                        } else if (points > 34 && points <= 44) {
                            assessment_4++;
                        } else if (points > 44 && points <= 50) {
                            assessment_5++;
                        }
                    }
                }
            }
        }
        initGraph();
    }

    @SuppressLint("SetTextI18n")
    private void initGraph() {
        GraphView graph = fragmentView.findViewById(R.id.graph_statistic);
        TextView title = fragmentView.findViewById(R.id.graph_title);

        graph.removeAllSeries();

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "0-24", "25-34", "35-44", "45-50", ""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        /*graph.getGridLabelRenderer().setVerticalAxisTitle("Количество человек");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Баллы");
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);*/
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().reloadStyles();

        title.setText(nameSubject + "\n" + topicName);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(1, assessment_2),
                new DataPoint(2, assessment_3),
                new DataPoint(3, assessment_4),
                new DataPoint(4, assessment_5),
        });

        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(5, 0),
        });
        graph.addSeries(series);
        graph.getSecondScale().addSeries(series2);

        listAssessment.clear();
        listAssessment.add(assessment_2);
        listAssessment.add(assessment_3);
        listAssessment.add(assessment_4);
        listAssessment.add(assessment_5);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(Collections.max(listAssessment)+1);
        graph.getViewport().setYAxisBoundsManual(true);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if (data.getX() == 1) {
                    return Color.rgb(255, 100, 85);
                }
                else if (data.getX() == 2) {
                    return Color.rgb(255, 150, 85);
                }
                else if (data.getX() == 3) {
                    return Color.rgb(255, 200, 85);
                }
                else if (data.getX() == 4) {
                    return Color.rgb(255, 250, 85);
                }
                return Color.rgb(255, 205, 87);
            }
        });

        series.setSpacing(10);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopSize(50);
        series.setValuesOnTopColor(Color.WHITE);
    }

    private void errorNull() {
        Log.d("Errors", "NullPointerException");
        Toast.makeText(getActivity(), "Ошибка соединения с сервером..", Toast.LENGTH_LONG).show();
    }
}
