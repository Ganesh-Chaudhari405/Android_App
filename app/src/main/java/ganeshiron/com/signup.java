package ganeshiron.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {
 private EditText fname,lname,email,passwd;
 private Button bsign;
 private ProgressBar progess;
 private TextView jarvis,login;
 private FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fname = findViewById(R.id.efistname);
        lname = findViewById(R.id.elastname);
        email = findViewById(R.id.editemail);
        passwd = findViewById(R.id.editpass);
        bsign = findViewById(R.id.bsign);
        progess = findViewById(R.id.lprogress);
        fauth = FirebaseAuth.getInstance();
        jarvis =findViewById(R.id.jarvis);
        login = findViewById(R.id.login);

        //for jarvis textview
        jarvis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,MainActivity.class));

            }
        });
        //for login textview
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,Login.class));

            }
        });


        //for signup
        bsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Fname= fname.getText().toString().trim();
                String Lname =lname.getText().toString().trim();
                String eemail = email.getText().toString().trim();
                String epasswd= passwd.getText().toString().trim();


                if(TextUtils.isEmpty((Fname))) {
                    fname.setError("First name is required");
                }
                if(TextUtils.isEmpty(Lname)) {
                    lname.setError("Las tname is required");
                }

                if(TextUtils.isEmpty(eemail)) {
                    email.setError("Email is required.");
                    return;
                }

                if(TextUtils.isEmpty(epasswd))
                {
                    passwd.setError("Password is required.");
                    return;
                }
                if(epasswd.length()<6)
                {
                    passwd.setError("Password must be greter than 6 charaters");
                    return;
                }
                progess.setVisibility(View.VISIBLE);
                //for regitration of user
                fauth.createUserWithEmailAndPassword(eemail,epasswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(signup.this,"Signup is successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Event.class));
                            progess.setVisibility(View.INVISIBLE);
                        }
                        else{
                            progess.setVisibility(View.INVISIBLE);
                            Toast.makeText(signup.this,"Error !" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }
}
