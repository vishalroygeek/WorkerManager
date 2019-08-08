package com.vishalroy.workermanager.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vishalroy.workermanager.Helpers.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vishalroy.workermanager.R;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button sign_in;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();


        //Hiding statusBar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Finding view by ID
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        sign_in = findViewById(R.id.sign_in);


        //Initializing Sign In Button
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allClear()){
                    signIn(email.getText().toString(), password.getText().toString());
                }
            }
        });


    }

    private void signIn(String email, String password){
        loadingDialog = new LoadingDialog(SignInActivity.this);
        loadingDialog.show();//Show loading Dialog

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingDialog.dismiss();
                        if (task.isSuccessful()){
                            SignInActivity.this.finish();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        }else {
                            toast("Unable to Sign In. Please retry !");
                        }
                    }
                });
    }

    private boolean allClear(){
        if (TextUtils.isEmpty(email.getText().toString())){
            toast("Please enter an Email");
            return false;
        }else if (TextUtils.isEmpty(password.getText().toString())){
            toast("Please enter you password");
            return false;
        }else if (!email.getText().toString().contains("@")||!email.getText().toString().contains(".")){
            toast("Please enter a valid Email");
            return false;
        }else {
            return true;
        }
    }

    private void toast(String message){
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
