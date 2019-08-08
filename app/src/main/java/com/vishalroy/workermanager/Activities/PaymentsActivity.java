package com.vishalroy.workermanager.Activities;

import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vishalroy.workermanager.Adapters.PaymentsAdapter;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Models.Payments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishalroy.workermanager.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentsActivity extends AppCompatActivity {

    private RecyclerView recylerView;
    private ProgressBar progressBar;
    private TextView header_text_1, header_text_2;
    private ImageView header_image;
    private Toolbar toolbar;
    private List<Payments> paymentsList;
    private PaymentsAdapter paymentsAdapter;
    private FirebaseFirestore mDatabase;
    private DocumentSnapshot lastDocument;
    private boolean loadMore = true;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        mDatabase = FirebaseFirestore.getInstance();


        //Changing the status bar theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        }

        id = getIntent().getStringExtra("id");

        //Finding view by ID
        header_text_1 = findViewById(R.id.header_text_1);
        header_text_2 = findViewById(R.id.header_text_2);
        header_image = findViewById(R.id.header_image);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        recylerView = findViewById(R.id.recylerView);


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
        header_text_1.setText("Monthly Payments");


        //Loading first ten payments
        loadTenFirst();

    }

    private void loadTenFirst(){
        paymentsList = new ArrayList<>();
        paymentsAdapter = new PaymentsAdapter(paymentsList);
        recylerView.setLayoutManager(new LinearLayoutManager(PaymentsActivity.this));
        recylerView.setAdapter(paymentsAdapter);

        recylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreEmployees();
                }
            }
        });


        mDatabase.collection(CollectionRef.EMPLOYEE_MONTHLY_PAYMENTS)
                .whereEqualTo("id", id)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        showProgress(false);
                        if (task.isSuccessful()){
                            int numberOfPayments = task.getResult().getDocuments().size();

                            if (numberOfPayments==0){
                                toast("No payments found");
                            }else {
                                lastDocument = task.getResult().getDocuments().get(numberOfPayments-1);

                                for (int i = 0; i < numberOfPayments; i++){

                                    Payments payments = task.getResult().getDocuments().get(i).toObject(Payments.class);
                                    paymentsList.add(payments);
                                    paymentsAdapter.notifyDataSetChanged();

                                }
                            }
                        }else {
                            toast("Unable to load payments");
                            Log.d("error", task.getException().toString());
                        }
                    }
                });

    }

    private void loadMoreEmployees(){

        if (loadMore){

            mDatabase.collection(CollectionRef.EMPLOYEE_MONTHLY_PAYMENTS)
                    .whereEqualTo("id", id)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .startAfter(lastDocument)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                int numberOfPayments = task.getResult().getDocuments().size();

                                if (numberOfPayments!=0){
                                    lastDocument = task.getResult().getDocuments().get(numberOfPayments-1);
                                    for (int i = 0; i < numberOfPayments; i++){


                                        Payments payments = task.getResult().getDocuments().get(i).toObject(Payments.class);
                                        paymentsList.add(payments);
                                        paymentsAdapter.notifyDataSetChanged();

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
        Toast.makeText(PaymentsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                PaymentsActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
