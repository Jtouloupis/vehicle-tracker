package com.example.vehicletracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class signup extends AppCompatActivity {

    //functional register, sign up with google etc (+ acc already exists),

    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    TextView textError;
    TextInputEditText editEmail;
    TextInputEditText editPassword;
    Button signUpButton;
    ImageButton googleReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth = FirebaseAuth.getInstance();




        // GET IDs
        signUpButton = findViewById(R.id.signUpButton);

        textError = findViewById(R.id.error);
        googleReg = findViewById(R.id.googleReg);



        signUpButton.setOnClickListener((view->{
            createUser();
        }));



        //call google sign in method
        requestGoogleSignIn();
        googleReg.setOnClickListener(view -> {
            resultLauncher.launch(new Intent((mGoogleSignInClient.getSignInIntent())));

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
                    Toast.makeText(signup.this,"Authentication failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(signup.this,"User logged in successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this, tracker.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(signup.this,"Authentication failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }



    ///////////////// END OF GOOGLE SIGN IN /////////////////







    private void createUser(){
        TextView email = (TextView) findViewById(R.id.email);
        TextView password = (TextView) findViewById(R.id.password1);
        TextView password2 = (TextView) findViewById(R.id.password2);


        if(email.getText().toString().isEmpty()){
            textError.setText("email is empty");

        }else if(password.getText().toString().isEmpty()){

            textError.setText("password is empty");

        }else if(!(password.getText().toString().equals(password2.getText().toString()))){
            textError.setText("passwords don't match!");

        }else{
            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SharedPreferences.Editor editor = getApplicationContext()
                                .getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                        editor.putString("userEmail", email.getText().toString());
                        editor.apply();

                        Toast.makeText(signup.this,"User registerd successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(signup.this, login.class));
                    }else{
                        Toast.makeText(signup.this,"Registration Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

}
