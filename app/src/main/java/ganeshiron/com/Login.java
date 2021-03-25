package ganeshiron.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText logemail,logpasswd;
    private Button buttonl;
    private TextView Fpass,singup,jarvis;
   private  ProgressBar progres;
   private FirebaseAuth fauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logemail = findViewById(R.id.editemail);
        logpasswd = findViewById(R.id.editpass);
        buttonl = findViewById(R.id.blogin);
        Fpass = findViewById(R.id.ptxt);
        progres = findViewById(R.id.lprogress);
        fauth = FirebaseAuth.getInstance();
        singup =findViewById(R.id.signup);
        jarvis = findViewById(R.id.jarvis);

        progres.setVisibility(View.INVISIBLE);


        //for signup textview
        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,signup.class));
            }
        });

        //for jarvis textview
        jarvis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,MainActivity.class));

            }
        });


        //for reset password
        Fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail = new EditText(v.getContext());
                final AlertDialog.Builder resetp = new AlertDialog.Builder(v.getContext());
                resetp.setTitle("Reset Password");
                resetp.setMessage("Enter your email to reset password");
                resetp.setView(resetmail);

                //get email and send reset link.


                resetp.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        String email;
                        progres.setVisibility(View.VISIBLE);
                        email = resetmail.getText().toString().trim();
                            if(TextUtils.isEmpty(email)) {
                                Toast.makeText(Login.this, "Error ! Enter email", Toast.LENGTH_SHORT).show();
                                progres.setVisibility(View.INVISIBLE);
                            }
                            else {
                                fauth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Login.this, "Reset link is sent to your email", Toast.LENGTH_SHORT).show();
                                        progres.setVisibility(View.INVISIBLE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this, "Error ! Reset link isn't sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progres.setVisibility(View.INVISIBLE);
                                    }
                                });

                            }
                    }
                });


                resetp.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                resetp.create().show();
            }
        });

        // for login button
        buttonl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = logemail.getText().toString().trim();
                String password = logpasswd.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    logemail.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    logpasswd.setError("Password is required.");
                    return;
                }
                if (password.length() < 6) {
                    logpasswd.setError("Password must be greter than 6 charaters");
                    return;
                }
                progres.setVisibility(View.VISIBLE);

                //for Authentication of user
                fauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Event.class));

                            progres.setVisibility(View.INVISIBLE);
                        } else {
                            progres.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });
    }
    }

