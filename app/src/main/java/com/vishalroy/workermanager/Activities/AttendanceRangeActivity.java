package com.vishalroy.workermanager.Activities;

import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vishalroy.workermanager.Adapters.AttendanceRangeAdapter;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Models.AttendanceRange;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishalroy.workermanager.R;

import java.util.ArrayList;
import java.util.List;

public class AttendanceRangeActivity extends AppCompatActivity {

    private TextView header_text_1, header_text_2;
    private ImageView header_image;
    private Toolbar toolbar;
    private RecyclerView recylerView;
    private ProgressBar progressBar;
    private String id;
    private long startDate, endDate;
    private FirebaseFirestore mDatabase;
    private List<AttendanceRange> attendanceRangeList;
    private AttendanceRangeAdapter attendanceRangeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_attendance);
        mDatabase = FirebaseFirestore.getInstance();

        //Changing the status bar theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        }


        id = getIntent().getStringExtra("id");
        startDate = getIntent().getLongExtra("startDate", 0);
        endDate = getIntent().getLongExtra("endDate", 0);


        //Finding view by ID
        header_text_1 = findViewById(R.id.header_text_1);
        header_text_2 = findViewById(R.id.header_text_2);
        header_image = findViewById(R.id.header_image);
        toolbar = findViewById(R.id.toolbar);
        recylerView = findViewById(R.id.recylerView);
        progressBar = findViewById(R.id.progressBar);

        //Changing Typefaces
        Typeface molot = Typeface.createFromAsset(getAssets(), "fonts/molot.otf");
        header_text_1.setTypeface(molot);
        header_text_1.setText("Attendance");


        //Customizing ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        header_text_2.setVisibility(View.GONE);
        header_image.setVisibility(View.GONE);

        //Loading attendance here
        showAttendance();
    }

    private void showAttendance(){
        attendanceRangeList = new ArrayList<>();
        attendanceRangeAdapter = new AttendanceRangeAdapter(attendanceRangeList);
        recylerView.setLayoutManager(new LinearLayoutManager(AttendanceRangeActivity.this));
        recylerView.setAdapter(attendanceRangeAdapter);

        mDatabase.collection(CollectionRef.ATTENDANCE)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        showProgress(false);

                        if (task.isSuccessful()){

                            int size = task.getResult().getDocuments().size();

                            for (int i = 0; i < size; i++){
                                if (task.getResult().getDocuments().get(i).contains(id) &&
                                        task.getResult().getDocuments().get(i).get(id).equals(true)){

                                    AttendanceRange attendanceRange = task.getResult().getDocuments().get(i).toObject(AttendanceRange.class);
                                    attendanceRangeList.add(attendanceRange);
                                    attendanceRangeAdapter.notifyDataSetChanged();
                                }
                            }

                        }else {
                            toast("Unable to load attendance. Please try again !");
                        }

                    }
                });

    }

    private void showProgress(boolean show){
        if (show){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void toast(String message){
        Toast.makeText(AttendanceRangeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                AttendanceRangeActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
