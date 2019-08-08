package com.vishalroy.workermanager.Activities;

import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.vishalroy.workermanager.Adapters.ManageEmployeesAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class ManageEmployeesActivity extends AppCompatActivity {

    private RecyclerView recylerView;
    private ProgressBar progressBar;
    private TextView header_text_1, header_text_2;
    private ImageView header_image;
    private Toolbar toolbar;
    private List<Employees> employeesList;
    private ManageEmployeesAdapter manageEmployeesAdapter;
    private FirebaseFirestore mDatabse;
    private boolean loadMore = true;
    private DocumentSnapshot lastDocument;
    private SwipeRefreshLayout swipe_refresh;
    private RecyclerView.OnScrollListener onScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employees);
        mDatabse = FirebaseFirestore.getInstance();


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
        progressBar = findViewById(R.id.progressBar);
        recylerView = findViewById(R.id.recylerView);
        swipe_refresh = findViewById(R.id.swipe_refresh);


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
        header_text_1.setText("Manage Employees");


        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreEmployees();
                }
            }
        };


        employeesList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageEmployeesActivity.this);
        manageEmployeesAdapter = new ManageEmployeesAdapter(employeesList);
        recylerView.setLayoutManager(linearLayoutManager);
        recylerView.setAdapter(manageEmployeesAdapter);
        recylerView.addOnScrollListener(onScrollListener);
        loadFirstEmployeesList();

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                employeesList.clear();
                manageEmployeesAdapter.notifyDataSetChanged();
                loadFirstEmployeesList();
            }
        });
    }

    private void loadFirstEmployeesList(){
        showProgress(true);
        swipe_refresh.setRefreshing(false);

        mDatabse.collection(CollectionRef.EMPLOYEES).orderBy(EmployeeRefCredentials.DATE_ADDED, Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        showProgress(false);
                        loadMore = true;

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
                                    manageEmployeesAdapter.notifyDataSetChanged();

                                }
                            }
                        }else {
                            toast("Unable to load Employees");
                        }
                    }
                });


    }

    private void loadMoreEmployees(){

        if (loadMore){

            mDatabse.collection(CollectionRef.EMPLOYEES).orderBy(EmployeeRefCredentials.DATE_ADDED, Query.Direction.ASCENDING)
                    .startAfter(lastDocument)
                    .limit(10)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                int noOfEmployees = task.getResult().getDocuments().size();

                                if (noOfEmployees!=0){

                                    lastDocument = task.getResult().getDocuments().get(noOfEmployees-1);

                                    for (int i = 0; i < noOfEmployees; i++){
                                        Employees employee = task.getResult().getDocuments().get(i).toObject(Employees.class);
                                        employee.setId(task.getResult().getDocuments().get(i).getId());
                                        employeesList.add(employee);
                                        manageEmployeesAdapter.notifyDataSetChanged();

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
        Toast.makeText(ManageEmployeesActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                ManageEmployeesActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
