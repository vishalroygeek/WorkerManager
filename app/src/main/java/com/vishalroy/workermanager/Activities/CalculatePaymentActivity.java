package com.vishalroy.workermanager.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Helpers.EmployeeRefCredentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vishalroy.workermanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalculatePaymentActivity extends AppCompatActivity {

    private ImageView header_image;
    private Toolbar toolbar;
    private CircleImageView profile_image;
    private TextView header_text_1, header_text_2, name, wage, advance_amt, last_paid, attendance, daily_payments, final_payment;
    private FirebaseFirestore mDatabse;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private int advance, daysPresent, dailyPaymentsAmount, final_amount, employee_wage;
    private long lastPaid;
    private String id, LAST_PAYMENT, type, url, user_name;
    private Button make_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_payment);
        mDatabse = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("wages", MODE_PRIVATE);
        progressDialog = new ProgressDialog(CalculatePaymentActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);


        //Changing the status bar theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        }


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        url = intent.getStringExtra("url");
        user_name = intent.getStringExtra("name");
        type = intent.getStringExtra("type");
        lastPaid = intent.getLongExtra("last_paid", 0);
        advance = intent.getIntExtra("advance", 0);
        employee_wage = Integer.parseInt(intent.getStringExtra("wage"));


        //Finding view by ID
        header_text_1 = findViewById(R.id.header_text_1);
        header_text_2 = findViewById(R.id.header_text_2);
        header_image = findViewById(R.id.header_image);
        toolbar = findViewById(R.id.toolbar);
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        wage = findViewById(R.id.wage);
        advance_amt = findViewById(R.id.advance_amt);
        last_paid = findViewById(R.id.last_paid);
        attendance = findViewById(R.id.attendance);
        daily_payments = findViewById(R.id.daily_payments);
        final_payment = findViewById(R.id.final_payment);
        make_payment = findViewById(R.id.make_payment);


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
        header_text_1.setText("Monthly Payment");


        //Setting up everything left here
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        LAST_PAYMENT = sdf.format(lastPaid);

        loadImage(url);
        name.setText(user_name);
        wage.setText("Wage : ₹ " + String.valueOf(employee_wage));
        advance_amt.setText("Advance : ₹ " + String.valueOf(advance));
        if (lastPaid==0){
            last_paid.setText("Last monthly payment : Nil");
        }else {
            last_paid.setText("Last monthly payment : " + LAST_PAYMENT);
        }

        setAttendance();

        make_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(final_amount <= 0)){
                    showConfirmationDailog();
                }else {
                    toast("Total amount is not enough to be paid !");
                }
            }
        });

    }

    private void showConfirmationDailog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CalculatePaymentActivity.this);
        dialog.setMessage("Are you sure you wan to send monthly payment to " + user_name + " ?");
        dialog.setIcon(getResources().getDrawable(R.drawable.ic_info));
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
                String date = simpleDateFormat.format(System.currentTimeMillis());
                Date date1 = null;
                try {
                    date1 = simpleDateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                updateLastMonthlyPayment(date1.getTime());
            }
        });
        dialog.setNegativeButton("No", null);
        dialog.create();
        dialog.show();
    }

    private void updateLastMonthlyPayment(final long time){
        progressDialog.setMessage("Paying employee...");
        progressDialog.show();
        Map<String, Object> lastPayment = new HashMap<>();
        lastPayment.put(EmployeeRefCredentials.LAST_PAID, time);

        mDatabse.collection(CollectionRef.EMPLOYEES)
                .document(id)
                .update(lastPayment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            addMonthlyPaymentRecord(time);
                        }else {
                            progressDialog.dismiss();
                            toast("Unable to pay " + user_name + ". Please try again !");
                        }
                    }
                });
    }

    private void addMonthlyPaymentRecord(final long time){
        progressDialog.setMessage("Adding payment to record...");
        Map<String , Object> monthlyPayment = new HashMap<>();
        monthlyPayment.put("id", id);
        monthlyPayment.put("date", time);
        monthlyPayment.put("amount", String.valueOf(final_amount));

        mDatabse.collection(CollectionRef.EMPLOYEE_MONTHLY_PAYMENTS)
                .document()
                .set(monthlyPayment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            CalculatePaymentActivity.this.finish();
                            toast(user_name + " got the monthly payment :)");
                        }else {
                            toast("Unable to pay " + user_name + ". Please try again !");
                        }
                    }
                });

    }

    private void setAttendance(){
        progressDialog.setMessage("Getting total attendance...");
        progressDialog.show();

        mDatabse.collection(CollectionRef.ATTENDANCE)
                .whereGreaterThan("date", lastPaid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++){
                                if (task.getResult().getDocuments().get(i).contains(id) &&
                                        task.getResult().getDocuments().get(i).get(id).equals(true)){
                                   daysPresent++;
                                }
                            }
                            attendance.setText("Days present, since last payment : " + String.valueOf(daysPresent));
                            setDailyPayments();
                        }else {
                            progressDialog.dismiss();
                            toast("Unable to get attendance. Please try again !");
                        }
                    }
                });
    }

    private void setDailyPayments(){
        progressDialog.setMessage("Getting total amount of daily payments...");

        mDatabse.collection(CollectionRef.EMPLOYEE_DAILY_PAYMENTS)
                .whereGreaterThan("date", lastPaid)
                .whereEqualTo("user_id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++){
                                int amount = Integer.parseInt(task.getResult().getDocuments().get(i).getString("amount"));
                                dailyPaymentsAmount = dailyPaymentsAmount+amount;
                            }
                            daily_payments.setText("Total amount of daily payments, since last payment : ₹ "+
                            String.valueOf(dailyPaymentsAmount));
                            setFinalPayment();
                        }else {
                            Log.d("error", task.getException().toString());
                            toast("Unable to load total amount of daily payments. Please try again !");
                        }
                    }
                });

        //TODO: have to do a little changes here :)
        /*mDatabse.collection(CollectionRef.PAYMENTS)
                .whereGreaterThan("date", lastPaid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            for (int i = 0; i<task.getResult().getDocuments().size(); i++){
                                if (task.getResult().getDocuments().get(i).contains(id) &&
                                        task.getResult().getDocuments().get(i).get(id).equals(true)){
                                    noOfDailyPayments++;
                                }
                            }
                            daily_payments.setText("No. of daily payments : " + String.valueOf(noOfDailyPayments));
                            setFinalPayment();
                        }else {
                            toast("Unable to get daily payments. Please try again !");
                        }
                    }
                });*/
    }

    private void setFinalPayment(){
        final_amount = (daysPresent*employee_wage) - ((dailyPaymentsAmount) + advance);
        if (final_amount<0){
            final_payment.setText("Final amount to be paid : ₹ 0");
        }else {
            final_payment.setText("Final amount to be paid : ₹ " + String.valueOf(final_amount));
        }

    }

    private void loadImage(String url) {
        Glide.with(CalculatePaymentActivity.this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url)
                .into(profile_image);
    }

    private void toast(String message){
        Toast.makeText(CalculatePaymentActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                CalculatePaymentActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
