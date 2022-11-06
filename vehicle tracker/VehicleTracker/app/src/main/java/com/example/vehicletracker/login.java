package com.example.vehicletracker;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Locale;

public class login extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    //private static final int RC_SIGN_IN =123;
    ImageButton googleLog;
    FirebaseAuth mAuth;
    TextView textError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.login_layout);

        mAuth =FirebaseAuth.getInstance();



        //get login button
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);


        //PRESSED LOGIN
        loginbtn.setOnClickListener(view -> {
            loginUser();
        });


        //PRESSED LOGIN VIA GOOGLE
        requestGoogleSignIn();
        googleLog = findViewById(R.id.googleLog);

        //call google sign in method
        googleLog.setOnClickListener(view -> {
            resultLauncher.launch(new Intent((mGoogleSignInClient.getSignInIntent())));

         });




        //don't have an account yet
        TextView createAcc = findViewById(R.id.create_acc);

        //TO REGISTRATION
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_SignUp();
            }

        });


    }

    ////////////////// GOOGLE SIGN IN //////////////////////

    private  void requestGoogleSignIn(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("424814017101-ialt6ccuaq0bk7cmkpj660624rm8qv65.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }




    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK){
                Intent intent = result.getData();

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());

                    SharedPreferences.Editor editor = getApplicationContext()
                            .getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                    editor.putString("userEmail", account.getDisplayName());
                    editor.apply();

                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(login.this,"Authentication failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

        }
    });





    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this,"User logged in successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login.this, tracker.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(login.this,"Authentication failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }



    ///////////////// END OF GOOGLE SIGN IN /////////////////




    //LOGIN USER
    protected void loginUser(){
        TextView email = (TextView) findViewById(R.id.email_login);
        TextView password= (TextView) findViewById(R.id.password_login);;
        textError = findViewById(R.id.errorLogIn);


        if(email.getText().toString().isEmpty()){
            textError.setText("email is empty");

        }else if(password.getText().toString().isEmpty()){

            textError.setText("password is empty");

        }else{
            mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        SharedPreferences.Editor editor = getApplicationContext()
                                .getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                        editor.putString("userEmail", email.getText().toString());
                        editor.apply();

                        Toast.makeText(login.this,"User logged in successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this, tracker.class));
                    }else{
                        Toast.makeText(login.this,"Log in Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }








    //OPEN SIGN UP
    public void open_SignUp(){
        Intent intent = new Intent(login.this,signup.class);
        startActivity(intent);

    }

    //OPEN Tracker
    public void open_Tracker(){
        Intent intent = new Intent(this,tracker.class);
        startActivity(intent);

    }



    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity((new Intent(login.this, tracker.class)));
        }

    }

}
