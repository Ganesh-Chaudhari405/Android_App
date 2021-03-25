package ganeshiron.com;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Event extends AppCompatActivity {
    private EditText Editnotice,Editdesc;
    private Button uploadbtn,summitbtn;
    private TextView txtf;
    EditText uptxt;
    private  FirebaseDatabase databas; //used to store URL of upoaded file.
    private  DatabaseReference dataref,ref;
    private FirebaseStorage store;  //used to uploading file/actual.
    private StorageReference storageRef;
    private Uri urifile;
    Insertdata insert;
    private Button logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
     Editnotice = findViewById(R.id.eventedit);
     Editdesc = findViewById(R.id.descevent);
     uploadbtn = findViewById(R.id.Selectb);
     summitbtn = findViewById(R.id.summitb);
     txtf = findViewById(R.id.Txtf);
     logout = findViewById(R.id.btnlog);
     //for database connectivity
     databas =FirebaseDatabase.getInstance(); //return object of firebase database.
     dataref =databas.getReference().child("Inserdata");
     ref =databas.getReference().child("Uploads");
     store = FirebaseStorage.getInstance();
     storageRef=store.getReference();

     insert = new Insertdata();
     uptxt=findViewById(R.id.uploadpdf);
     //for logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

     //for summit button
        summitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = Editnotice.getText().toString().trim();
                String desc = Editdesc.getText().toString().trim();
                String file =uptxt.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    Editnotice.setError("Title is required.");
                    return;
                }
                if(desc.length()<=50)
                {
                    insert.setTitle(title);
                }

                if(desc.length()<220)
                {
                    insert.setDesc(desc);
                }

                if(urifile != null)
                {
                    if(TextUtils.isEmpty(file))
                    { uptxt.setError("Filename is required.");
                        return;
                    }
                    uploadFile(urifile);
                }
                else
                {
                    Toast.makeText(Event.this,"Data succussfully uploaded",Toast.LENGTH_SHORT).show();
                }

                dataref.child("Insertd").setValue(insert);
            }
        });

           //for selection file
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(ContextCompat.checkSelfPermission(Event.this, Manifest.permission.READ_EXTERNAL_STORAGE)==getPackageManager().PERMISSION_GRANTED)
              {
                 selectfile();
              }
              else
              {
                  ActivityCompat.requestPermissions(Event.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
              }
            }
        });
    }

    private void selectfile() {
        Intent intent = new Intent();
       intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select File "),4);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==4 && data !=null && data.getData() !=null)
        { urifile =data.getData();
         txtf.setText("A file is selected : "+ data.getData().getLastPathSegment());
        }
        else
        {         Toast.makeText(Event.this," please select file",Toast.LENGTH_SHORT).show();
        }
   }

    private void uploadFile(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
       progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("File is uploading");
        progressDialog.setProgress(0);
        progressDialog.show();
        StorageReference reference = storageRef.child("Upload/"+System.currentTimeMillis()+".pdf");
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri =taskSnapshot.getStorage().getDownloadUrl();

                while(!uri.isComplete());

                    Uri url =uri.getResult();

                Upload upload =new Upload( uptxt.getText().toString().trim() ,url.toString().trim());
                ref.child(ref.push().getKey()).setValue(upload);

                Toast.makeText(Event.this, "Data succussfully uploaded", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currpro = (int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
             progressDialog.setProgress(currpro);

            }
        });
    }


}
