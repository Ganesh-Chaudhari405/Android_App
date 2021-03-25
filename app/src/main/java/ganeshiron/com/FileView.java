package ganeshiron.com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileView extends AppCompatActivity {
   public ListView listView;
    DatabaseReference databaseReference;
    ArrayList<Upload> upload;
    TextView jarvis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);
         listView =findViewById(R.id.listview);
         upload =new ArrayList<>();
         jarvis = findViewById(R.id.jarvis);

        //for jarvis textview
        jarvis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FileView.this,MainActivity.class));

            }
        });
           //for retrive file
         viewFiles();
              //for listview
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @SuppressLint("IntentReset")
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Upload upload2 = upload.get(position);
                 Intent intent = new Intent();
                 intent.setAction(Intent.ACTION_DEFAULT);
                 intent.setData(Uri.parse(upload2.getUrl()));

                 startActivity(intent);
             }
         });
    }

    private void viewFiles() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Upload upload1 =postSnapshot.getValue(Upload.class);
                    upload.add(upload1);
                }
                String[] uploads =new String[upload.size()];
                for(int i=0;i<uploads.length;i++)
                {
                    uploads[i]=upload.get(i).getFilename();

                }
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,uploads);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FileView.this, " please check database connection", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
