package com.vishalroy.workermanager.Activities;

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vishalroy.workermanager.Adapters.CashbookAdapter;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Helpers.LoadingDialog;
import com.vishalroy.workermanager.Models.Cashbook;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishalroy.workermanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashbookActivity extends AppCompatActivity {

    private RecyclerView recylerView;
    private ProgressBar progressBar;
    private TextView header_text_1, header_text_2;
    private ImageView header_image;
    private Toolbar toolbar;
    private FloatingActionButton add;
    private FirebaseFirestore mDatabase;
    private LoadingDialog loadingDialog;
    private CashbookAdapter cashbookAdapter;
    private List<Cashbook> cashbookList;
    private DocumentSnapshot lastDocument;
    private boolean loadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashbook);
        mDatabase = FirebaseFirestore.getInstance();
        loadingDialog = new LoadingDialog(CashbookActivity.this);

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
        add = findViewById(R.id.add);


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
        header_text_1.setText("Cashbook");


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddrecordDialog();
            }
        });


        //Loading first ten cash records
        cashbookList = new ArrayList<>();
        cashbookAdapter = new CashbookAdapter(cashbookList);
        recylerView.setLayoutManager(new LinearLayoutManager(CashbookActivity.this));
        recylerView.setAdapter(cashbookAdapter);

        recylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    loadMoreEmployees();
                }
            }
        });

        loadTenFirst();
    }

    private void loadTenFirst(){

        mDatabase.collection(CollectionRef.CASHBOOK)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        showProgress(false);
                        if (task.isSuccessful()){
                            int numberOfRecords = task.getResult().getDocuments().size();

                            if (numberOfRecords==0){
                                toast("No cash records found");
                            }else {
                                lastDocument = task.getResult().getDocuments().get(numberOfRecords-1);

                                for (int i = 0; i < numberOfRecords; i++){

                                    Cashbook cashbook = task.getResult().getDocuments().get(i).toObject(Cashbook.class);
                                    cashbookList.add(cashbook);
                                    cashbookAdapter.notifyDataSetChanged();

                                }
                            }
                        }else {
                            toast("Unable to load cash records");
                        }
                    }
                });

    }

    private void loadMoreEmployees(){

        if (loadMore){

            mDatabase.collection(CollectionRef.CASHBOOK)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .startAfter(lastDocument)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                int noOfRecords = task.getResult().getDocuments().size();

                                if (noOfRecords!=0){

                                    for (int i = 0; i < noOfRecords; i++){
                                        lastDocument = task.getResult().getDocuments().get(noOfRecords-1);

                                        Cashbook cashbook = task.getResult().getDocuments().get(i).toObject(Cashbook.class);
                                        cashbookList.add(cashbook);
                                        cashbookAdapter.notifyDataSetChanged();

                                    }

                                }else {
                                    loadMore = false;
                                }
                            }
                        }
                    });
        }
    }

    private void showAddrecordDialog(){
        final EditText name, amount, description;
        Button submit;
        ImageView close;

        final Dialog dialog = new Dialog(CashbookActivity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.cashbook_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        name = dialog.findViewById(R.id.name);
        amount = dialog.findViewById(R.id.amount);
        description = dialog.findViewById(R.id.description);
        submit = dialog.findViewById(R.id.submit);
        close = dialog.findViewById(R.id.close);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText().toString())){
                    toast("Please fill the name");
                }else if (TextUtils.isEmpty(amount.getText().toString())){
                    toast("Please enter an amount");
                }else if (TextUtils.isEmpty(description.getText().toString())){
                    toast("Please enter a description");
                }else {
                    addRecord(name.getText().toString(), amount.getText().toString(), description.getText().toString());
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void addRecord(String name, String amount, String desc){
        loadingDialog.show();

        Map<String, Object> record = new HashMap<>();
        record.put("name", name);
        record.put("amount", amount);
        record.put("description", desc);
        record.put("date", System.currentTimeMillis());

        mDatabase.collection(CollectionRef.CASHBOOK)
                .document()
                .set(record)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismiss();
                        if (task.isSuccessful()){
                            cashbookList.clear();
                            cashbookAdapter.notifyDataSetChanged();
                            loadTenFirst();
                            toast("Record added successfully");
                        }else {
                            toast("Unable to add record. Please try again");
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
        Toast.makeText(CashbookActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                CashbookActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
