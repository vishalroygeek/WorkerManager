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

import com.vishalroy.workermanager.Adapters.EmployeesAttendanceAdapter;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Helpers.EmployeeRefCredentials;
import com.vishalroy.workermanager.Models.Employees;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishalroy.workermanager.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MakeAttendanceActivity extends AppCompatActivity {

    private TextView header_text_1, header_text_2;
    private ImageView header_image;
    private Toolbar toolbar;
    private RecyclerView recylerView;
    private ProgressBar progressBar;
    private List<Employees> employeesList;
    private EmployeesAttendanceAdapter adapter;
    private FirebaseFirestore mDatabase;
    private DocumentSnapshot lastDocument;
    private boolean loadMore = true;
    private String date;

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

        //Finding view by ID
        header_text_1 = findViewById(R.id.header_text_1);
        header_text_2 = findViewById(R.id.header_text_2);
        header_image = findViewById(R.id.header_image);
        toolbar = findViewById(R.id.toolbar);
        recylerView = findViewById(R.id.recylerView);
        progressBar = findViewById(R.id.progressBar);


        //Setting date to show attendance
        if (getIntent().getStringExtra("date")!=null){
            date = getIntent().getStringExtra("date");
            header_text_1.setText("Attendance");
        }else {
            date = new SimpleDateFormat("dd MMM yyyy").format(System.currentTimeMillis());
            header_text_1.setText("Make Attendance");
        }

        toast(date);

        //Changing Typefaces
        Typeface molot = Typeface.createFromAsset(getAssets(), "fonts/molot.otf");
        header_text_1.setTypeface(molot);


        //Customizing ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        header_text_2.setVisibility(View.GONE);
        header_image.setVisibility(View.GONE);



        //Loading first ten employees
        loadTenFirst();

    }

    private void loadTenFirst(){
        employeesList = new ArrayList<>();
        adapter = new EmployeesAttendanceAdapter(employeesList, date);
        recylerView.setLayoutManager(new LinearLayoutManager(MakeAttendanceActivity.this));
        recylerView.setAdapter(adapter);

        recylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreEmployees();
                }
            }
        });


        mDatabase.collection(CollectionRef.EMPLOYEES).orderBy(EmployeeRefCredentials.DATE_ADDED, Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        showProgress(false);
                        if (task.isSuccessful()){
                            int numberOfEmployees = task.getResult().getDocuments().size();

                            if (numberOfEmployees==0){
                                toast("No employees found");
                            }else {
                                lastDocument = task.getResult().getDocuments().get(numberOfEmployees-1);

                                for (int i = 0; i < numberOfEmployees; i++){

                                    Employees employee = task.getResult().getDocuments().get(i).toObject(Employees.class);
                                    employee.setId(task.getResult().getDocuments().get(i).getId());
                                    employeesList.add(employee);
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        }else {
                            toast("Unable to load Attendance");
                        }
                    }
                });

    }

    private void loadMoreEmployees(){

        if (loadMore){

            mDatabase.collection(CollectionRef.EMPLOYEES).orderBy(EmployeeRefCredentials.DATE_ADDED, Query.Direction.ASCENDING)
                    .startAfter(lastDocument)
                    .limit(10)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                int noOfEmployees = task.getResult().getDocuments().size();

                                if (noOfEmployees!=0){

                                    for (int i = 0; i < noOfEmployees; i++){
                                        lastDocument = task.getResult().getDocuments().get(noOfEmployees-1);

                                        Employees employee = task.getResult().getDocuments().get(i).toObject(Employees.class);
                                        employee.setId(task.getResult().getDocuments().get(i).getId());
                                        employeesList.add(employee);
                                        adapter.notifyDataSetChanged();

                                    }

                                }else {
                                    loadMore = false;
                                }
                            }
                        }
                    });
        }
    }

    private void showProgress(boolean show){
        if (show){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void toast(String message){
        Toast.makeText(MakeAttendanceActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                MakeAttendanceActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
