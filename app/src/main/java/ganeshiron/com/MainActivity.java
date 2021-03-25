package ganeshiron.com;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.okhttp.internal.Internal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.MemoryFile;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecog;
    private TextView txt;
    private TextView txt1;
    private TextView toolbar;
    private TextView txtlogin;
    private Button bsignup;
    private Button blogin;
    private TextView title, desc;
    public TextView filetxt;
    private FirebaseDatabase firebase;
    private DatabaseReference dataref;
    private FirebaseStorage firestorage;
    ConnDetector connDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        txt = findViewById(R.id.txt);
        txt1 = findViewById(R.id.txt1);
        blogin = findViewById(R.id.blogin);
        bsignup = findViewById(R.id.bsign);
        title = findViewById(R.id.Titletxt);
        desc = findViewById(R.id.desctxt);
        filetxt = findViewById(R.id.Ftxt);
        firebase = FirebaseDatabase.getInstance();
        firestorage = FirebaseStorage.getInstance();
        dataref = firebase.getReference().child("Inserdata");
        ArrayList<String> urls = new ArrayList<>();
        connDetector = new ConnDetector(this);


        //for signup page
        bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentsign = new Intent(MainActivity.this, signup.class);
                startActivity(intentsign);
                Toast.makeText(MainActivity.this, "You will signup here..", Toast.LENGTH_SHORT).show();
            }
        });


        // for login page
        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentlog = new Intent(MainActivity.this, Login.class);
                startActivity(intentlog);
                Toast.makeText(MainActivity.this, "You will login here..", Toast.LENGTH_SHORT).show();
            }
        });

        //for mic
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //for permission of mic.
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        // this thread waiting for the user's response!
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                } else {
                    // Permission has already been granted
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, 1);
                    speechRecog.startListening(intent);
                    txt.setHint("Listening");                 //startActivityForResult(intent,1);
                    txt1.setHint("");

                }
            }
        });

        // for retrive file from  firebase





        filetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connDetector.conn()) {
                    startActivity(new Intent(MainActivity.this, FileView.class));
                } else {
                    Toast.makeText(MainActivity.this, "Turn on internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });



        retrive();
        initializeTextToSpeech();
        initializeSpeechRecognizer();

    }


    // for recognization of voice
    private void initializeSpeechRecognizer() {
        if(SpeechRecognizer.isRecognitionAvailable(this))
        {
            speechRecog = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecog.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> result_arrs = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if(result_arrs != null)
                    {
                        Result(result_arrs.get(0));
                        txt.setHint(result_arrs.get(0));
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });

        }

    }



      //for user output

    private void Result(String final_message)
    {   //only lowercase

        final_message =final_message.toLowerCase();
                               //for daily life
        if(final_message.indexOf("what is your name") != -1 ||final_message.indexOf("name") !=-1){

            speak("My Name is Friday");
            txt1.setHint("My Name is Friday");

        }
        else if (final_message.indexOf("time") != -1){
            String time_now = DateUtils.formatDateTime(this, new Date().getTime(),DateUtils.FORMAT_SHOW_TIME);
            speak("The time is now: " + time_now);
            txt1.setHint("The time is now:"+time_now);

        }
        else if (final_message.indexOf("date") != -1){
            String date_now = DateUtils.formatDateTime(this, new Date().getTime(),DateUtils.FORMAT_SHOW_DATE);
            speak("The date is: " + date_now);
            txt1.setHint("The date is: " + date_now);
        }
        else if(final_message.indexOf("year")!=-1)
        {
            String curryear =DateUtils.formatDateTime(this,new Date().getTime(),DateUtils.FORMAT_SHOW_YEAR);
            speak("The current year is :"+ curryear);
            txt1.setHint("The current year is :"+ curryear);
        }
        else if(final_message.indexOf("day")!=-1)
        {
            String currday =DateUtils.formatDateTime(this,new Date().getTime(),DateUtils.FORMAT_SHOW_WEEKDAY);
            speak("The today is :"+ currday);
            txt1.setHint("The today is :"+ currday);
        }
        else if(final_message.indexOf("youtube") != -1||final_message.indexOf("open youtube") != -1||final_message.indexOf("start youtube") != -1)
        {

            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"));
            startActivity(intent);
        }
        else if(final_message.indexOf("google") != -1||final_message.indexOf("open google") != -1||final_message.indexOf("start google") != -1)
        {

            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
            startActivity(intent);
        }
        else if(final_message.indexOf("instagram") != -1||final_message.indexOf("open insta") != -1||final_message.indexOf("start insta") != -1)
        {

            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"));
            startActivity(intent);
        }
        else if(final_message.indexOf("facebook") != -1||final_message.indexOf("open facebook") != -1||final_message.indexOf("start facebook") != -1)
        {

            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
            startActivity(intent);
        }
        else if(final_message.indexOf("gcek gateway") != -1||final_message.indexOf("open wifi") != -1||final_message.indexOf("start gcek gateway") != -1)
        {

            speak("Opening a Gcek gateway");
            txt1.setHint("Opening a GCEK gateway");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://1.1.1.1:8090/httpclient.html"));
            startActivity(intent);
        }
        else if(final_message.indexOf("whatsapp") != -1||final_message.indexOf("open whatsapp") != -1||final_message.indexOf("start whatsapp") != -1)
        {

            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.whatsapp.com/"));
            startActivity(intent);
        }
        else if(final_message.indexOf("play store") != -1||final_message.indexOf("open play store") != -1||final_message.indexOf("start play store") != -1)
        {

            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps?hl=en"));
            startActivity(intent);
        }

        else if(final_message.indexOf("song") != -1||final_message.indexOf("play song") != -1||final_message.indexOf("play video") != -1||final_message.indexOf("video song") != -1)
        {
            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Yfydy5Z3TAQ&list=RDYfydy5Z3TAQ&start_radio=1"));
            startActivity(intent);

        }
        else if(final_message.indexOf("camera") != -1||final_message.indexOf("open camera") != -1||final_message.indexOf("start camera") != -1)
        {
                try {
                    speak("Ok");
                    txt1.setHint("Ok");
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
                    startActivity(intent);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

        }
        else if(final_message.indexOf("map") != -1||final_message.indexOf("google map") != -1||final_message.indexOf("open map") != -1||final_message.indexOf("open google map") != -1||final_message.indexOf("open google map") != -1)
        {
            speak("Opening a map");
            txt1.setHint("Opening a map");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/maps"));
            startActivity(intent);

        }

        else if(final_message.indexOf("contacts") != -1||final_message.indexOf("open cantacts") != -1||final_message.indexOf("open contact") != -1||final_message.indexOf("contact") != -1)
        {
            try {
                speak("Ok");
                txt1.setHint("Ok");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL_BUTTON);
                startActivity(intent);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        else if(final_message.indexOf("sign up") != -1||final_message.indexOf("open sign up") != -1||final_message.indexOf("open sign up page") != -1||final_message.indexOf("start sign up") != -1)
        {
            speak("Ok");
            txt1.setHint("Ok");
           startActivity(new Intent(MainActivity.this,signup.class));

        }
        else if(final_message.indexOf("login") != -1||final_message.indexOf("open login") != -1||final_message.indexOf("open login page") != -1||final_message.indexOf("start login") != -1)
        {
            speak("Ok");
            txt1.setHint("Ok");
            startActivity(new Intent(MainActivity.this,Login.class));

        }

        else if (final_message.indexOf("who are you") != -1){
            speak("Virtual assistant of IT department");
            txt1.setHint("Virtual assistant of IT department");
        }

        else if(final_message.indexOf("how are you") != -1){
            speak("Great! I'm doing really well, thank you");
            txt1.setHint("Great! I'm doing really well, thank you");
        }



                  //for department
        else if(final_message.indexOf("permanent faculty") != -1||final_message.indexOf("faculty permanent") != -1) {

            speak("There are 8 Faculties");
            txt1.setHint("There are 8 Faculties");
        }

        else if(final_message.indexOf("how many visiting faculties") != -1|| final_message.indexOf("visiting faculties") != -1 ||final_message.indexOf("faculties visiting") != -1) {

            speak("There are 8 Faculties");
            txt1.setHint("There are 8 Faculties");
        }
        else if(final_message.indexOf("name visiting") != -1|| final_message.indexOf("visiting faculties name") != -1 ||final_message.indexOf("visiting name") != -1) {
             speak("Niranjan Deokule,Kunjali Pawar,Abhilasha Sathe,Jyoti Admane,Chaitanya Garware,Pranali Sheth,Minakshee Patil,Balshetwar");
             txt1.setHint("Niranjan Deokule,Kunjali Pawar,Abhilasha Sathe,Jyoti Admane,Chaitanya Garware,Pranali Sheth,Minakshee Patil,Balshetwar");
        }
        else if(final_message.indexOf("name permanent") != -1|| final_message.indexOf("permanent faculties name") != -1 ||final_message.indexOf("permanent name") != -1) {

            speak("Dr. wagh,Raj Kulkarni,Nilakshi Mule,Y.D. Chavhan,Atul Chaudhari,Nikita Shety,Chetan Andhare,Rajesh Mavale");
            txt1.setHint("Dr. wagh,Raj Kulkarni,Nilakshi Mule,Y.D. Chavhan,Atul Chaudhari,Nikita Shety,Chetan Andhare,Rajesh Mavale");
        }
        else if(final_message.indexOf("number faculties") != -1|| final_message.indexOf("how many faculties") !=-1) {

            speak("There are 16 Faculties");
            txt1.setHint("There are 16 Faculties");
        }
        else if(final_message.indexOf("hod name") !=-1||final_message.indexOf("hod") !=-1)
        {   speak("Dr. Sanjeev J. Wagh  ,Professor & HOD, Information Technology");
            txt1.setHint("Dr. Sanjeev J. Wagh  ,Professor & HOD, Information Technology");
        }
        else if(final_message.indexOf("establishment of college") !=-1||final_message.indexOf("formation of college")!=-1)
        {
           speak("Government College Of Engineering,Karad was established in 1960");
           txt1.setHint("Government College Of Engineering,Karad was established in 1960");
        }
        else if(final_message.indexOf("principal") !=-1)
        {
            speak("A.T. Pise is the principle of Gcek");
            txt1.setHint("A.T. Pise is the principle of Gcek");
        }

        else if(final_message.indexOf("establishment of information technology") !=-1||final_message.indexOf("formation of it")!=-1)
        {
            speak("IT department was established in 2001");
            txt1.setHint("IT department was established in 2001");

        }
        else if(final_message.indexOf("branch") !=-1||final_message.indexOf("under graduate") !=-1)
        {
            speak("Electrical Engineering,Civil Engineering,Mechanical Engineering,Information Technology,Electronics and Telecommunication Engineering");
            txt1.setHint("Electrical Engineering,Civil Engineering,Mechanical Engineering,Information Technology,Electronics and Telecommunication Engineering");

        }
        else if(final_message.indexOf("autonomy status") !=-1||final_message.indexOf("autonomous")!=-1)
        {
            speak(" Gcek was affiliated to the Shivaji University till 2015 after which it became an autonomous institute.The autonomy was granted by the UGC");
            txt1.setHint(" Gcek was affiliated to the Shivaji University till 2015 after which it became an autonomous institute.The autonomy was granted by the UGC");
        }
        else if(final_message.indexOf("autonomous year") !=-1)
        {
            speak("2015");
            txt1.setHint("2015");
        }
        else if(final_message.indexOf("vision") !=-1)
        { speak("To provide value based high quality IT education by empowering every student to be innovative and employable IT professional");
        txt1.setHint("To provide value based high quality IT education by empowering every student to be innovative and employable IT professional");
        }
        else if(final_message.indexOf("mission") !=-1)
        {
            speak("To offer graduate program in Information Technology for making students excellent IT professionals and encouraging them for higher studies and research");
            txt1.setHint("To offer graduate program in Information Technology for making students excellent IT professionals and encouraging them for higher studies and research");
        }
        else if(final_message.indexOf("chairman of industry advisory board") !=-1||final_message.indexOf("chairman of board of studies")!=-1||final_message.indexOf("chairman bos")!=-1||final_message.indexOf("bos chairman")!=-1)
        {
            speak("Dr. S.J. Wagh ");
            txt1.setHint("Dr S.J. Wagh");
        }
        else if(final_message.indexOf("members bos")!=-1||final_message.indexOf("members board of studies")!=-1||final_message.indexOf("members bos")!=-1||final_message.indexOf("bos members")!=-1)
        {
            speak("18");
            txt1.setHint("18");
        }
        else if(final_message.indexOf("members iab")!=-1||final_message.indexOf("members industry advisory board")!=-1)
        {
            speak("12");
            txt1.setHint("12");
        }
        else if(final_message.indexOf(" how many labs") !=-1||final_message.indexOf("numbers of labs") !=-1)
        {
            speak("7");
            txt1.setHint("7");

        }
        else if(final_message.indexOf("name labs") !=-1||final_message.indexOf("labs name") !=-1)
        {
            speak("Design,Software,Network,Project,Graphics,Programming,Microprocessor and hardware laboratory");
            txt1.setHint("Design,Software,Network,Project,Graphics,Programming,Microprocessor and hardware laboratory");

        }
        else if(final_message.indexOf("student association") !=-1||final_message.indexOf("itsa") !=-1)
        {
            speak("Itsa");
            txt1.setHint("Itsa");

        }
        else if(final_message.indexOf("establishment student association") !=-1||final_message.indexOf("itsa establishment") !=-1||final_message.indexOf("establishment itsa") !=-1)
        {
            speak("2011");
            txt1.setHint("2011");
        }

        //for student
        else if(final_message.indexOf("first year topper") !=-1||final_message.indexOf("topper of first year") !=-1)
        {
            speak("");
            txt1.setHint("");
        }
        else if(final_message.indexOf("second year topper") !=-1||final_message.indexOf("topper of second year") !=-1)
        {
            speak("");
            txt1.setHint("");
        }
        else if(final_message.indexOf("third year topper") !=-1||final_message.indexOf("topper of third year") !=-1)
        {
            speak("");
            txt1.setHint("");
        }
        else if(final_message.indexOf("fourth year topper") !=-1||final_message.indexOf("topper of fourth year") !=-1)
        {
            speak("");
            txt1.setHint("");
        }
        else if(final_message.indexOf("first year student") !=-1||final_message.indexOf("how many students first year") !=-1)
        {
            speak("63");
            txt1.setHint("63");
        }
        else if(final_message.indexOf("second year student") !=-1||final_message.indexOf("how many students second year") !=-1)
        {
            speak("61");
            txt1.setHint("61");
        }
        else if(final_message.indexOf("third year student") !=-1||final_message.indexOf("how many students third year") !=-1)
        {
            speak("60");
            txt1.setHint("60");
        }
        else if(final_message.indexOf("fourth year student") !=-1||final_message.indexOf("how many students fourth year") !=-1)
        {  speak("58");
            txt1.setHint("58");

        }
        else if(final_message.indexOf("subjects of first semester") !=-1||final_message.indexOf("first semester subjects") !=-1||final_message.indexOf("subject of first semester") !=-1||final_message.indexOf("first semester subject") !=-1)
        {
            speak("Mathematics – I,Physics");
            txt1.setHint("Mathematics – I ,physics");
        }
        else if(final_message.indexOf("subjects of second semester") !=-1||final_message.indexOf("second semester subjects") !=-1||final_message.indexOf("subject of second semester") !=-1||final_message.indexOf("second semester subject") !=-1)
        {
            speak("Mathematics – I,Chemistry");
            txt1.setHint("Mathematics – I ,Chemistry");
        }
        else if(final_message.indexOf("subjects of 3rd semester") !=-1||final_message.indexOf("3rd semester subjects") !=-1||final_message.indexOf("subject of 3rd semester") !=-1||final_message.indexOf("3rd semester subject") !=-1)
        {
            speak("Applied Mathematics-III,Digital System,Data Structures and Application,Discrete Mathematics,Object Oriented Programming with C++,Environmental Studies");
            txt1.setHint("Applied Mathematics-III,Digital System,Data Structures and Application,Discrete Mathematics,Object Oriented Programming with C++,Environmental Studies");
        }
        else if(final_message.indexOf("subjects of 4th semester") !=-1||final_message.indexOf("4th semester subjects") !=-1)
        {
            speak("Systems Software,Data Communication,Database Management Systems,Java,Theory of Computer Science,Computer Organization and Architecture,General Proficiency-II");
            txt1.setHint("Systems Software,Data Communication,Database Management Systems,Java,Theory of Computer Science,Computer Organization and Architecture,General Proficiency-II");

        }
        else if(final_message.indexOf("subjects of 5th semester") !=-1||final_message.indexOf("5th semester subjects") !=-1||final_message.indexOf("subject of 5th semester") !=-1||final_message.indexOf("5th semester subject") !=-1)
        {
            speak("Advanced Database Management Systems,Software Engineering,Computer Nerworks,Operating System,Microprocessor and Microcontroller,General proficiency III");
            txt1.setHint("Advanced Database Management Systems,Software Engineering,Computer Nerworks,Operating System,Microprocessor and Microcontroller,General proficiency III");

        }
        else if(final_message.indexOf("subjects of 6th semester") !=-1||final_message.indexOf("6th semester subjects") !=-1||final_message.indexOf("subject of 6th semester") !=-1||final_message.indexOf("6th semester subject") !=-1)
        {
            speak("Open Elective- Web Technology,Data Warehousing and Mining,Computer Algorithms,Object Oriented Software and Web Engineering,Information Security");
            txt1.setHint("Open Elective- Web Technology,Data Warehousing and Mining,Computer Algorithms,Object Oriented Software and Web Engineering,Information Security");

        }
        else if(final_message.indexOf("subjects of 7th semester") !=-1||final_message.indexOf("7th semester subjects") !=-1||final_message.indexOf("subject of 7th semester") !=-1||final_message.indexOf("7th semester subject") !=-1)
        {
            speak("Software Testing and Quality Assurance,Internet of Things,Information Retrieval,Human Computer Interface,Mobile Computing,Artificial Intelligence");
            txt1.setHint("Software Testing and Quality Assurance,Internet of Things,Information Retrieval,Human Computer Interface,Mobile Computing,Artificial Intelligence");

        }
        else if(final_message.indexOf("subjects of 8th semester") !=-1||final_message.indexOf("8th semester subjects") !=-1||final_message.indexOf("subject of 8th semester") !=-1||final_message.indexOf("8th semester subject") !=-1)
        {
            speak("Principles of Information Technology Management,Cloud Computing,Soft Computing,Bioinformatics,Enterprise Resource Planning,Ethical Hacking and Digital Forensics");
            txt1.setHint("Principles of Information Technology Management,Cloud Computing,Soft Computing,Bioinformatics,Enterprise Resource Planning,Ethical Hacking and Digital Forensics");
        }


        else if(final_message.indexOf("placement") !=-1||final_message.indexOf("it placement") !=-1)
        {
            speak("45");
            txt1.setHint("45");
        }

        else if(final_message.indexOf("open gcek website") !=-1||final_message.indexOf("open gcek portal") !=-1||final_message.indexOf("gcek website") !=-1||final_message.indexOf("gcek portal") !=-1||final_message.indexOf("open college website") !=-1||final_message.indexOf("open college portal") !=-1)
        {
            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gcekarad.ac.in/"));
            startActivity(intent);
        }

        else if(final_message.indexOf("tell me about it") !=-1||final_message.indexOf("tell me about information technology") !=-1||final_message.indexOf("tell me about it department") !=-1)
        {
            speak("Ok");
            txt1.setHint("Ok");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gcekarad.ac.in/Deptindex.aspx?page=a&ItemID=sa&nDeptID=i"));
            startActivity(intent);

        }
        else if(final_message.indexOf("highest package") !=-1||final_message.indexOf("maximum package") !=-1||final_message.indexOf("package") !=-1)
        {
            speak("12 lac");
            txt1.setHint("12 lac");
        }

       else

        {
            speak("Sorry,i amn't recognized your voice");
            txt1.setHint("Sorry,i amn't recognized your voice");
        }



    }

    //for initialize the text to speech
    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            private Object Intent;

            @SuppressLint("NewApi")
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0 ){
                    Toast.makeText(MainActivity.this, "There is no tts engine on your device" ,Toast.LENGTH_LONG).show();
                    finish();
                }
                else {



                    tts.setLanguage(Locale.ENGLISH);
                    speak("Hi,how can i help you");
                    txt.setHint("Hi,how can i help you");
                }
            }
        });
    }


    //for initialize the speak method
    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21){
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    //for retrieve data from firebase
    private void retrive()
    {
        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rtitle = dataSnapshot.child("Insertd").child("title").getValue(String.class);
                String rdesc = dataSnapshot.child("Insertd").child("desc").getValue(String.class);
                //String filename=dataSnapshot.getKey();
               // String url = dataSnapshot.child("Insertd").child("Filename1").getValue(String.class);
                title.setText(rtitle);
                desc.setText(rdesc);
                title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                title.setSelected(true);
               // txt.setLinksClickable(Boolean.parseBoolean(url));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        txt1.setHint("");
        retrive();
        initializeSpeechRecognizer();
        initializeTextToSpeech();

    }
}