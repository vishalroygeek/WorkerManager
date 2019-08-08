package com.vishalroy.workermanager.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vishalroy.workermanager.Helpers.CollectionRef;
import com.vishalroy.workermanager.Helpers.LoadingDialog;
import com.vishalroy.workermanager.Helpers.EmployeeRefCredentials;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vishalroy.workermanager.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AddEmployeeActivity extends AppCompatActivity {

    private TextView header_text_1, header_text_2;
    private ImageView header_image;
    private Toolbar toolbar;
    private CircleImageView profile_image;
    private EditText name, phone, wage;
    private Spinner employee_type;
    private Button submit;
    private Uri profile_image_uri = EmployeeRefCredentials.DEFAULT_URI;
    private FirebaseFirestore mDatabase;
    private LoadingDialog loadingDialog;
    private StorageReference mStorage;
    private boolean ACTION_EDIT;
    private String url, username, type, number, id, wage_amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        mDatabase = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        loadingDialog = new LoadingDialog(AddEmployeeActivity.this);


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
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        employee_type = findViewById(R.id.employee_type);
        submit = findViewById(R.id.submit);
        wage = findViewById(R.id.wage);

        //Checking action type
        Intent intent = getIntent();
        ACTION_EDIT = intent.getBooleanExtra("action", false);
        url = intent.getStringExtra("url");
        username = intent.getStringExtra("name");
        type = intent.getStringExtra("type");
        number = intent.getStringExtra("number");
        id = intent.getStringExtra("id");
        wage_amount = intent.getStringExtra("wage");


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


        if (ACTION_EDIT){
            header_text_1.setText("Edit Employee");
            submit.setText("Update");
            profile_image_uri = Uri.parse(url);
            loadImage(url);
            name.setText(username);
            phone.setText(number);
            wage.setText(wage_amount);
            setEmployeeType();
        }else {
            header_text_1.setText("Add Employee");
        }




        //Initialing profile image picker
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(AddEmployeeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddEmployeeActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else{
                        pickimage();
                    }
                }else {
                    pickimage();
                }
            }
        });


        //Initialing submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTION_EDIT){
                    if (allClear()){
                        updateEmployee(name.getText().toString(), phone.getText().toString(), employee_type.getSelectedItem()
                                .toString(), wage.getText().toString());
                    }
                }else {
                    if (allClear()){
                        if (String.valueOf(profile_image_uri).equals(String.valueOf(EmployeeRefCredentials.DEFAULT_URI))){
                            addEmployee(name.getText().toString(), phone.getText().toString(),
                                    employee_type.getSelectedItem().toString(),String.valueOf(profile_image_uri)
                                    , wage.getText().toString());
                        }else {
                            uploadImage(name.getText().toString(), phone.getText().toString(), employee_type.getSelectedItem()
                                    .toString(), wage.getText().toString());
                        }
                    }
                }

            }
        });


    }

    private void updateEmployee(String name, String phone, String type, String wage){
        if (String.valueOf(profile_image_uri).equals(url)){
            updateProfile(name, phone, type, String.valueOf(profile_image_uri), wage);
        }else {
            uploadImage(name, phone, type, wage);
        }
    }

    private void updateProfile(String name, String number, String type, String image, String wage){
        if (!loadingDialog.isShowing()){
            loadingDialog.show();
        }

        HashMap<String, Object> employee = new HashMap<>();
        employee.put(EmployeeRefCredentials.NAME, name);
        employee.put(EmployeeRefCredentials.PHONE, number);
        employee.put(EmployeeRefCredentials.EMPLOYEE_TYPE, type);
        employee.put(EmployeeRefCredentials.IMAGE, image);
        employee.put(EmployeeRefCredentials.WAGE, wage);

        mDatabase.collection(CollectionRef.EMPLOYEES).document(id)
                .update(employee)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismiss();
                        if (task.isSuccessful()){
                            toast("Employee updated successfully");
                        }else {
                            toast("Unable to update employee");
                        }
                    }
                });
    }

    private void uploadImage(final String name, final String phone, final String type, final String wage){
        loadingDialog.show();


        final StorageReference reference = mStorage.child(CollectionRef.EMPLOYEES).child(name+" ("+phone+")"+".png");
        UploadTask uploadTask = reference.putFile(profile_image_uri);

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    if (ACTION_EDIT){
                        updateProfile(name, phone, type, String.valueOf(task.getResult()), wage);
                    }else {
                        addEmployee(name, phone, type, String.valueOf(task.getResult()), wage);
                    }
                }else {
                    loadingDialog.dismiss();
                    toast("Unable to upload Image");
                }
            }
        });

    }

    private void addEmployee(String name, String phone, String type, String image, String wage){
        if (!loadingDialog.isShowing()){
            loadingDialog.show();
        }

        HashMap<String, Object> employee = new HashMap<>();
        employee.put(EmployeeRefCredentials.NAME, name);
        employee.put(EmployeeRefCredentials.PHONE, phone);
        employee.put(EmployeeRefCredentials.IMAGE, image);
        employee.put(EmployeeRefCredentials.EMPLOYEE_TYPE, type);
        employee.put(EmployeeRefCredentials.LAST_PAID, 0);
        employee.put(EmployeeRefCredentials.DATE_ADDED, System.currentTimeMillis());
        employee.put(EmployeeRefCredentials.ADVANCE_AMOUNT, 0);
        employee.put(EmployeeRefCredentials.WAGE, wage);

        mDatabase.collection(CollectionRef.EMPLOYEES).document().set(employee)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismiss();
                        if (task.isSuccessful()){
                            clearFields();
                            toast("Employee added successfully");
                        }else {
                            toast("Unable to add Employee");
                        }
                    }
                });
    }

    private void pickimage(){
        CropImage.activity()
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(AddEmployeeActivity.this);
    }

    private boolean allClear(){
        if (TextUtils.isEmpty(name.getText().toString())){
            toast("Please enter a name");
            return false;
        }else if (phone.getText().length() < 10){
            toast("Please enter a valid phone number");
            return false;
        }else if (employee_type.getSelectedItem().toString().equals(getResources().getString(R.string.employee_type_prompt))){
            toast("Please select employee type");
            return false;
        }else if (TextUtils.isEmpty(wage.getText().toString())){
            toast("Please enter a wage");
            return false;
        } else {
            return true;
        }
    }

    private void toast(String message){
        Toast.makeText(AddEmployeeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearFields(){
        profile_image_uri = EmployeeRefCredentials.DEFAULT_URI;
        profile_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_labor));
        name.setText("");
        phone.setText("");
        employee_type.setSelection(0);
    }

    private void loadImage(String url){
        Glide.with(AddEmployeeActivity.this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url)
                .into(profile_image);
    }

    private void setEmployeeType(){
        if (type.equals("Mistri")){
            employee_type.setSelection(1);
        }else {
            employee_type.setSelection(2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File original_image = new File(result.getUri().getPath());
                try {
                    File compressedImage = new Compressor(this)
                            .setMaxWidth(512)
                            .setMaxHeight(512)
                            .setQuality(50)
                            .compressToFile(original_image);
                    profile_image_uri = Uri.fromFile(compressedImage);
                    profile_image.setImageURI(profile_image_uri);
                } catch (IOException e) {
                    toast("Please select the image again");
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                toast("Unable to load Image");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                AddEmployeeActivity.this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
