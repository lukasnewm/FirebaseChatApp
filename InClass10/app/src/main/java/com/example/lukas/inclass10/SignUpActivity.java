package com.example.lukas.inclass10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextPassword2;
    Button buttonSignUp;
    Button buttonCancel;
    String first, last, email, pass, pass2;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        setTitle("Sign Up");

        editTextFirstName = findViewById(R.id.editTextSignUpFirstName);
        editTextLastName = findViewById(R.id.editTextSignUpLastName);
        editTextEmail = findViewById(R.id.editTextSignUpEmail);
        editTextPassword = findViewById(R.id.editTextSignUpPassword);
        editTextPassword2 = findViewById(R.id.editTextSignUpPassword2);
        buttonSignUp = findViewById(R.id.buttonSignUpSignUp);
        buttonCancel = findViewById(R.id.buttonSignUpCancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first = editTextFirstName.getText().toString().trim();
                last = editTextLastName.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                pass = editTextPassword.getText().toString().trim();
                pass2 = editTextPassword2.getText().toString().trim();

                if (!(pass.equals(pass2)) || !(pass.length() > 0)) {
                    Toast.makeText(SignUpActivity.this, "Passwords Must Match", Toast.LENGTH_SHORT).show();
                } else if (!(first.length() > 0)) {
                    Toast.makeText(SignUpActivity.this, "Enter A First Name", Toast.LENGTH_SHORT).show();
                } else if (!(last.length() > 0)) {
                    Toast.makeText(SignUpActivity.this, "Enter A Last Name", Toast.LENGTH_SHORT).show();
                } else if (!(email.length() > 0)) {
                    Toast.makeText(SignUpActivity.this, "Enter An Email", Toast.LENGTH_SHORT).show();
                } else {
                    //Succesful
                    signUpUser();
                    //Toast.makeText(SignUp.this, "Created Account", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void signUpUser() {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    User userClass = new User(first, last, email);

                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "New Account Registered!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent signUpToChatAct = new Intent(SignUpActivity.this, ChatActivity.class);
                    signUpToChatAct.putExtra("fireUser", user);
                    startActivity(signUpToChatAct);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("FirebaseAppError", task.getException().getMessage().toString());
                }
            }
        });
    }
}
