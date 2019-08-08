package com.vishalroy.workermanager.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vishalroy.workermanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView header_text_1, header_text_2, add_employee_text, manage_employee_text;
    private CardView add_employee, manage_employee, make_attendance, show_attendance, show_daily_payments, cashbook;
    private static final int DATE_DIALOG_ID = 0;
    private static final int DATE_DIALOG_ID_TWO = 1;
    private int day, month, year;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Changing the status bar theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        }


        //Finding view by ID
        header_text_1 = findViewById(R.id.header_text_1);
        header_text_2 = findViewById(R.id.header_text_2);
        add_employee_text = findViewById(R.id.add_employee_text);
        manage_employee_text = findViewById(R.id.manage_employee_text);
        add_employee = findViewById(R.id.add_employee);
        manage_employee = findViewById(R.id.manage_employee);
        make_attendance = findViewById(R.id.make_attendance);
        show_attendance = findViewById(R.id.show_attendance);
        show_daily_payments = findViewById(R.id.show_daily_payments);
        cashbook = findViewById(R.id.cashbook);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //Changing Typefaces
        Typeface molot = Typeface.createFromAsset(getAssets(), "fonts/molot.otf");
        header_text_1.setTypeface(molot);
        header_text_2.setTypeface(molot);

        //Get current date
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        //Setting click listeners for grid buttons
        add_employee.setOnClickListener(this);
        manage_employee.setOnClickListener(this);
        make_attendance.setOnClickListener(this);
        show_attendance.setOnClickListener(this);
        show_daily_payments.setOnClickListener(this);
        cashbook.setOnClickListener(this);

    }


    boolean goBack = false;
    @Override
    public void onBackPressed() {
        if (goBack){
            super.onBackPressed();
        }else {
            goBack = true;
            toast("Tap again to Exit");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goBack = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_employee:
                startActivity(new Intent(MainActivity.this, AddEmployeeActivity.class));
                break;
            case R.id.manage_employee:
                startActivity(new Intent(MainActivity.this, ManageEmployeesActivity.class));
                break;
            case R.id.make_attendance:
                startActivity(new Intent(MainActivity.this, MakeAttendanceActivity.class));
                break;
            case R.id.show_attendance:
                showDialog(DATE_DIALOG_ID);
                break;
            case R.id.show_daily_payments:
                showDialog(DATE_DIALOG_ID_TWO);
                break;
            case R.id.cashbook:
                startActivity(new Intent(MainActivity.this, CashbookActivity.class));
                break;
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, year, month, day);
            case DATE_DIALOG_ID_TWO:
                return new DatePickerDialog(this, dateSetListener1, year, month, day);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mneu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                MainActivity.this.finish();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));

        }

        return true;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date_month = getMonth(month);
            String date_day = String.valueOf(dayOfMonth);
            String date_year = String.valueOf(year);
            String date = date_day+" "+date_month+" "+date_year;

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date date1 = null;
            try {
                date1 = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Intent attendance = new Intent(MainActivity.this, DailyPaymentsActivity.class);
            attendance.putExtra("date", date1.getTime());
            startActivity(attendance);
        }
    };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date_month = getMonth(month);
            String date_day = String.valueOf(dayOfMonth);
            String date_year = String.valueOf(year);
            String date = date_day+" "+date_month+" "+date_year;

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date date1 = null;
            try {
                date1 = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Intent attendance = new Intent(MainActivity.this, MakeAttendanceActivity.class);
            attendance.putExtra("date", sdf.format(date1.getTime()));
            startActivity(attendance);
        }
    };

    private void toast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private String getMonth(int month){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }
}
