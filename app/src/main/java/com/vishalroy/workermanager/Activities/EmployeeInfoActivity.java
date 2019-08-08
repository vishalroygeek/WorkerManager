package com.vishalroy.workermanager.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vishalroy.workermanager.R;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployeeInfoActivity extends AppCompatActivity {

    private TextView header_text_1, header_text_2, name, number, employee_type, last_paid, date_joined, advance_amt;
    private ImageView header_image;
    private Toolbar toolbar;
    private CircleImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_info);


        //Changing the status bar theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        }

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String user_name = intent.getStringExtra("name");
        final String user_number = intent.getStringExtra("number");
        String type = intent.getStringExtra("type");
        long lastPaid = intent.getLongExtra("last_paid", 0);
        long dateAdded = intent.getLongExtra("date_added", 0);
        int advance = intent.getIntExtra("advance", 0);


        //Finding view by ID
        header_text_1 = findViewById(R.id.header_text_1);
        header_text_2 = findViewById(R.id.header_text_2);
        header_image = findViewById(R.id.header_image);
        toolbar = findViewById(R.id.toolbar);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        employee_type = findViewById(R.id.employee_type);
        last_paid = findViewById(R.id.last_paid);
        date_joined = findViewById(R.id.date_joined);
        profile_image = findViewById(R.id.profile_image);
        advance_amt = findViewById(R.id.advance_amt);


        //Setting up all the info
        loadImage(url);
        name.setText(user_name);
        number.setText(user_number);
        employee_type.setText(type);
        advance_amt.setText("Advance : ₹ " + String.valueOf(advance));
        if (lastPaid==0){
            last_paid.setText("Last monthly payment : Nil");
        }else {
            last_paid.setText("Last monthly payment : " + new SimpleDateFormat("dd MMM yyyy").format(lastPaid));
        }
        date_joined.setText("Added on : " + new SimpleDateFormat("dd MMM yyyy").format(dateAdded));


        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user_number));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(EmployeeInfoActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) EmployeeInfoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    }else{
                        startActivity(call);
                    }
                }else {
                    startActivity(call);
                }
            }
        });


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
        header_text_1.setText("Employee Info");

    }

    private void loadImage(String url) {
        Glide.with(EmployeeInfoActivity.this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url)
                .into(profile_image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                EmployeeInfoActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
