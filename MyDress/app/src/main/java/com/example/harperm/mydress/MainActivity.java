package com.example.harperm.mydress;

import android.os.Bundle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import android.os.Environment;
import org.json.*;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.BitmapFactory;
import com.google.firebase.auth.FirebaseAuth; //For Firbase Login
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;


public class MainActivity extends AppCompatActivity {

    HashMap<String, List<String>> dictionary = new HashMap<String, List<String>>();
    List<String> topList = new ArrayList<String>();
    List<String> bottomList = new ArrayList<String>();
    static final int REQUEST_TAKE_PHOTO = 1;
    public List<File> photoFileList = new ArrayList<File>();
    File photoFile = null;
    String mCurrentPhotoPath;
    private TextView mTextMessage;

    private static final String TAG = "EmailPassword";

    // [START initialize_auth]
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // [END initialize_auth]


    //If the app is open, and user has perivously signed in, stay logged in
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                 Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                 Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        }
    };

    public FirebaseUser user;

    @Override
    //app opening: bring to main screen
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (user != null){
            Log.v(TAG, "UID: " + user.getUid());
             setContentView(R.layout.activity_main);
        }

    }

    @Override
    //sign out user end of applicaiton
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

         //mTextMessage = (TextView) findViewById(R.id.message);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //helper function, used throughout
    public String userEmail (){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        return email;
    }

    //Reads the clothing type and color from database in order to help generate an outfit.
    //When reading from database, the clothing file path (also saved in database will be
    //added to a global top or bottom list for later usage.
    public void readFromDatabase(View view, Integer count){
        String intEmail = userEmail();
        String email1 = intEmail.replaceAll("@", "");
        String email = email1.replaceAll("\\.", "");

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(email + "/" + String.valueOf(count));
        mDatabaseReference.addValueEventListener(
                new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object clothing = dataSnapshot.getValue(Object.class);
                        String clothingItem = String.valueOf(clothing);
                        String clothingItem1 = clothingItem.replace("{","");
                        String clothingItem2 = clothingItem1.replace("}","");
                        List<String> clothingList = new ArrayList<String>(Arrays.asList(clothingItem2.split(",")));
                        String clothingType = clothingList.get(1);
                        String clothingPath = clothingList.get(0);
                        String clothingPath1 = clothingPath.replace("PhotoPath=","");
                        if(clothingType.contains("Bottom")){
                            bottomList.add(clothingPath1);
                        }
                        else if(clothingType.contains("Top")){
                            topList.add(clothingPath1);
                        }
                    }
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //Toast.makeText(getApplicationContext(), String.valueOf(mDatabaseReference), Toast.LENGTH_LONG).show();
    }

    //Things saved to database for the given user:
        //photo path
        //object (Top/Bottom)
        //color of object
    public void saveToDatebase (Integer count, String child, String value){
        String intEmail = userEmail();
        String email1 = intEmail.replaceAll("@", "");
        String email = email1.replaceAll("\\.", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(email);
        myRef.child(String.valueOf(count)).child(child).setValue(value);
    }

    public void homepage (View view) {
        setContentView(R.layout.activity_main);
    }

    public void navigateLogin (View View){
        setContentView(R.layout.login_screen);
    }


    /*
        Login:
            with mauth (built into firebase, more info in documentation), verify
            that the user creating is successful, both the email and password and correct
            based on the informaiton contained in the database for the given user
     */
    public void login (final View view) {
        String email = ((EditText)findViewById(R.id.emailAddress)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
//                            updateUI(user);
                            Toast.makeText(MainActivity.this, "Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();
                            setContentView(R.layout.activity_main);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    /*Functions below are simple navigation helpers
        I.e. button clicked, moving forward through app
     */

    public void openCloset(View view) {
        setContentView(R.layout.closet_page1);
    }

    public void openLaundry(View view){
        setContentView(R.layout.laundry_page1);
    }

    public void openShopping(View view){
        setContentView(R.layout.shopping_page1);
    }

    public void navigateHome (View view) {
        setContentView(R.layout.activity_main);
    }

    /* Redirects:
        Each redirect contains the url to the stores shopping page
        When the user selects the stores logo in the shopping sections
        of the application, the user will be redirected to the shopping
        website.
     */
    public void aeRedirect (View view){
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.ae.com/"));
            startActivity(viewIntent);
    }

    public void gapRedirect (View view){
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.gap.com/"));
        startActivity(viewIntent);
    }

    public void lordRedirect (View view){
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.lordandtaylor.com/"));
        startActivity(viewIntent);
    }

    public void nordRedirect (View view){
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://shop.nordstrom.com/"));
        startActivity(viewIntent);
    }

    public void tjRedirect (View view){
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.tjmaxx.com/"));
        startActivity(viewIntent);
    }

    public void jcrewRedirect (View view){
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.jcrew.com/"));
        startActivity(viewIntent);
    }

    //Helper function user in openCamers which created the given
    //file path to store the photo.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /* Camera:
        When the camera is open, an image file will be created  up capture
        This will be where the image is stored on the device. Inside the
        android manifest, it is defined that the device in which is running
        the application must contain a camera.

        The camera will not capture and save the image if the file path is
        not created successfully.

     */
    public void openCamera (View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
                photoFileList.add(photoFile);
            } catch (IOException ex) {

                Toast.makeText(MainActivity.this, "Camera Error.",
                        Toast.LENGTH_SHORT).show();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }

    }

    /*colorTag:
        Helper function used in viewItems in order to make the ColorTag API Call.
        Base on the response of the call, a list of colors will be returned. The first color
        contains the main makeup of the article of clothing and will be added to the database
        under the color tag for the given photopath.
     */

    public String colorTag (View view, Integer count, String pictureFile) throws UnirestException {
        String colorDefined = null;
        //POST https://apicloud-colortag.p.mashape.com/tag-file.json
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        if(!photoFileList.isEmpty()) {
            try {
                HttpResponse<JsonNode> response = Unirest.post("https://apicloud-colortag.p.mashape.com/tag-file.json")
                        .header("X-Mashape-Key", "Z2wYzX8a6imshpT1USvHw9MsrumRp1sjz78jsn7Gw78pE6MMCE")
                        .field("image", new File(pictureFile))
                        .field("palette", "simple")
                        .field("sort", "relevance")
                        .asJson();
                JSONObject myObj = response.getBody().getObject();
                List<String> myList = new ArrayList<String>(Arrays.asList(String.valueOf(myObj).split("label")));
                String color = myList.get(1);

                if(color.contains("Red")){
                    saveToDatebase(count,"ColorTag","Red");
                    colorDefined = "Red";
                }
                else if(color.contains("Orange")){
                    saveToDatebase(count,"ColorTag","Orange");
                    colorDefined = "Red";
                }
                else if(color.contains("Yellow")){
                    saveToDatebase(count, "ColorTag","Yellow");
                    colorDefined = "Red";
                }
                else if(color.contains("Green")){
                    saveToDatebase(count, "ColorTag","Green");
                    colorDefined = "Red";
                }
                else if(color.contains("Cyan")){
                    saveToDatebase(count, "ColorTag","Cyan");
                    colorDefined = "Red";
                }
                else if(color.contains("Blue")){
                    saveToDatebase(count, "ColorTag","Blue");
                    colorDefined = "Red";
                }
                else if(color.contains("Purple")){
                    saveToDatebase(count, "ColorTag","Purple");
                    colorDefined = "Red";
                }
                else if(color.contains("Pink")){
                    saveToDatebase(count, "ColorTag","Pink");
                    colorDefined = "Red";
                }
                else if(color.contains("Beige")){
                    saveToDatebase(count, "ColorTag","Beige");
                    colorDefined = "Red";
                }
                else if(color.contains("Brown")){
                    saveToDatebase(count, "ColorTag","Brown");
                    colorDefined = "Red";
                }
                else if(color.contains("White")){
                    saveToDatebase(count, "ColorTag","White");
                    colorDefined = "Red";
                }
                else if(color.contains("Gray")){
                    saveToDatebase(count, "ColorTag","Gray");
                    colorDefined = "Red";
                }
                else if(color.contains("Black")){
                    saveToDatebase(count, "ColorTag","Black");
                    colorDefined = "Red";
                }
                else{
                    colorDefined = "Red";
                }

            }
            catch(UnirestException e){
                Toast.makeText(getApplicationContext(), "Api Call Error: " + String.valueOf(e), Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "There is no photos, cannot find the color", Toast.LENGTH_LONG).show();
        }

        return colorDefined;
    }

    /* recognition:
        Helper function used in viewItems in order to make the Image Recognition API Call
        Based on the response, it looks to see if it contains any of the key words for tops/bottoms.
        If so, the article of clothing will be updated in the database to contain the correct tag for
        object.
     */
    public String recognition (View view, Integer count, String pictureFile) throws  UnirestException{
            String objDefined=null;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            if(!photoFileList.isEmpty()) {
                try {
                    HttpResponse<JsonNode> response = Unirest.post("https://zyanyatech1-image-recognition-v1.p.mashape.com/")
                            .header("X-Mashape-Key", "Z2wYzX8a6imshpT1USvHw9MsrumRp1sjz78jsn7Gw78pE6MMCE")
                            .field("imgUploader", new File(pictureFile))
                            .asJson();
                    JSONObject myObj = response.getBody().getObject();
                    objDefined = String.valueOf(myObj);
                    if(objDefined.contains("Long Sleeve")|objDefined.contains("Shirt")| objDefined.contains("T-Shirt")|
                            objDefined.contains("Blouse")|objDefined.contains("Sweater")| objDefined.contains("Sweatshirt")|
                            objDefined.contains("Jacket")){
                        saveToDatebase(count, "Object", "Top");
                    }
                    else if(objDefined.contains("Shorts") | objDefined.contains("Pants") | objDefined.contains("Jean")
                            | objDefined.contains("Capris") | objDefined.contains("Skirt") | objDefined.contains("Skort")){
                        saveToDatebase(count, "Object", "Bottom");
                    }

                }
                catch(UnirestException e){
                    Toast.makeText(getApplicationContext(), "Api Call Error: " + String.valueOf(e), Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "There is no photos, cannot find the type", Toast.LENGTH_LONG).show();
            }

        return objDefined;
    }

    /* view items:
        When the items in ones closet is viewed, all information is shared to the database
        Both API calls are made here (colorTag and Recognition)
        The photopath is saved with the two tags: object and color
        Based on the responsed of the api calls, the information is updated in the database.

        Api Calls are made via Unirest and mashape, view documentation for further
        details.
     */
    public void viewItems(View view) throws UnirestException{
        setContentView(R.layout.closet_page2);
        LinearLayout layout1 = (LinearLayout)findViewById(R.id.LinearLayout1); //0-2
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.LinearLayout2); //3-5
        LinearLayout layout3 = (LinearLayout)findViewById(R.id.LinearLayout3); //6-8
        //LinearLayout layout4 = (LinearLayout)findViewById(R.id.LinearLayout4); //9-11
        //LinearLayout layout5 = (LinearLayout)findViewById(R.id.LinearLayout5); //12-14
        //LinearLayout layout6 = (LinearLayout)findViewById(R.id.LinearLayout6); //15-17

        if(photoFile != null) {
            int numberOfPhotos = photoFileList.size();
            for(int i=0; i < numberOfPhotos; i++) {

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int width = metrics.widthPixels;
                int height = metrics.heightPixels;




                ImageView image = new ImageView(this);
                image.setRotation(90);
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(width/3,height/3));
                image.setMaxHeight(10);
                image.setMaxWidth(10);

                ImageView image2 = new ImageView(this);
                image2.setRotation(90);
                image2.setLayoutParams(new android.view.ViewGroup.LayoutParams(width/3,height/3));
                image2.setMaxHeight(10);
                image2.setMaxWidth(10);

                ImageView image3 = new ImageView(this);
                image3.setRotation(90);
                image3.setLayoutParams(new android.view.ViewGroup.LayoutParams(width/3,height/3));
                image3.setMaxHeight(10);
                image3.setMaxWidth(10);

                if(i >= 0 && i <= 2) {
                    layout1.addView(image);
                    File pathToPicture = photoFileList.get(i);
                    String pictureFile = String.valueOf(pathToPicture);
                    image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                    saveToDatebase(i, "PhotoPath", pictureFile);
                    colorTag(view, i, pictureFile);
                    recognition(view, i, pictureFile);
                    readFromDatabase(view, i);
                }
                else if(i >= 3 && i <= 5) {
                    layout2.addView(image2);
                    File pathToPicture = photoFileList.get(i);
                    String pictureFile = String.valueOf(pathToPicture);
                    image2.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                    saveToDatebase(i, "PhotoPath", pictureFile);
                    colorTag(view, i, pictureFile);
                    recognition(view, i, pictureFile);
                    readFromDatabase(view, i);
                }

                else if(i >= 6 && i <= 8) {
                    layout3.addView(image3);
                    File pathToPicture = photoFileList.get(i);
                    String pictureFile = String.valueOf(pathToPicture);
                    image3.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                    saveToDatebase(i, "PhotoPath", pictureFile);
                    colorTag(view, i, pictureFile);
                    recognition(view, i, pictureFile);
                    readFromDatabase(view, i);
                }

            }
        }
    }

    /* outfit generation:
        the user must have at least one top or bottom saved to their account.
        If not, an outfit cannot be generated.

        Creates image view for both the top and bottom in order to display the pictures
        of the selected clothing

        Look at the global lists for top in bottom in order to pick the one being displayed.
        The photopaths are added to these list in order to be added to the given photoview.
     */
    public void generateOutfit(View view){

            if(bottomList.isEmpty()){
                Toast.makeText(getApplicationContext(), "Must Add a Bottom First", Toast.LENGTH_LONG).show();
            }
            else if(topList.isEmpty()){
                Toast.makeText(getApplicationContext(), "Must Add a Top First", Toast.LENGTH_LONG).show();
            }
            else{
                setContentView(R.layout.generated_outfit);
                LinearLayout layoutTop = (LinearLayout)findViewById(R.id.LinearLayout2);
                LinearLayout layoutBottom = (LinearLayout) findViewById(R.id.LinearLayout3);

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int width = metrics.widthPixels;
                int height = metrics.heightPixels;


                ImageView imageTop = new ImageView(this);
                imageTop.setRotation(90);
                imageTop.setLayoutParams(new android.view.ViewGroup.LayoutParams(width/3,height/3));
               // imageTop.setMaxHeight(10);
               // imageTop.setMaxWidth(10);

                ImageView imageBottom = new ImageView(this);
                imageBottom.setRotation(90);
                imageBottom.setLayoutParams(new android.view.ViewGroup.LayoutParams(width/3,height/3));
                //imageTop.setMaxHeight(10);
               // imageTop.setMaxWidth(10);

                layoutBottom.addView(imageBottom);
                layoutTop.addView(imageTop);

                int bottomSize = bottomList.size();
                int topSize = topList.size();

                Random random = new Random();
                int randomNumTop = random.nextInt((topSize));
                int randomNumBottom = random.nextInt((bottomSize));

                String bottomPicture = bottomList.get(randomNumBottom);
                String topPicture = topList.get(randomNumTop);
                File bottomFile = new File(bottomPicture);
                File topFile = new File(topPicture);
                imageTop.setImageBitmap(BitmapFactory.decodeFile(topFile.getAbsolutePath()));
                imageBottom.setImageBitmap(BitmapFactory.decodeFile(bottomFile.getAbsolutePath()));
            }

        }

    //navigate to createAccount screen
    public void createAccount (View view){
        setContentView(R.layout.create_account);
    }


    //Checked used in register user
    public static  boolean numberCheckString(String str){
        char ch;
        for(int i=0; i < str.length(); i++){
            ch = str.charAt(i);
            if(Character.isDigit(ch)){
                return true;
            }
        }

        return false;
    }

    public static boolean capitalCheckString(String str){
        char ch;
        for(int i=0; i < str.length(); i++){
            ch = str.charAt(i);
            if(Character.isUpperCase(ch)){
                return true;
            }
        }
        return false;
    }

    public static boolean lowerCheckString(String str){
        char ch;
        for(int i=0; i < str.length(); i++){
            ch = str.charAt(i);
            if(Character.isLowerCase(ch)){
                return true;
            }
        }
        return false;
    }

    //user creation:
        /* email: string
           password:
                - 8 characters
                - one captial letter
                - one lowercase letter
                - one number
           password confirm:
                - must match password
           anything invalid will clear text field, give invalid popup
         */
    public void registerUser(View view) { //(String email, String password) {
        String email = ((EditText)findViewById(R.id.emailAddress)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        String passwordConfirm = ((EditText)findViewById(R.id.passwordConfirm)).getText().toString();


        int passwordLength = password.length();
        boolean numberCheck =  numberCheckString(password);
        boolean capitalCheck = capitalCheckString(password);
        boolean lowerCheck = lowerCheckString(password);

        if(passwordLength < 8){
            Toast.makeText(MainActivity.this, "Password must be at least 8 characters long.",
                    Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.password)).setText("");
            ((EditText)findViewById(R.id.passwordConfirm)).setText("");
        }
        else if(numberCheck == false){
                Toast.makeText(MainActivity.this, "Password must contain at least 1 digit.",
                        Toast.LENGTH_SHORT).show();
                ((EditText)findViewById(R.id.password)).setText("");
                ((EditText)findViewById(R.id.passwordConfirm)).setText("");

        }
        else if(capitalCheck == false){
            Toast.makeText(MainActivity.this, "Password must contain at least 1 capital letter.",
                    Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.password)).setText("");
            ((EditText)findViewById(R.id.passwordConfirm)).setText("");
        }
        else if (lowerCheck == false){
            Toast.makeText(MainActivity.this, "Password must contain at least 1 lower case letter.",
                    Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.password)).setText("");
        }else if(!password.equals(passwordConfirm)){
            Toast.makeText(MainActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.password)).setText("");
            ((EditText)findViewById(R.id.passwordConfirm)).setText("");
        }

        else{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                                Toast.makeText(MainActivity.this, "Authentication Success.",
                                        Toast.LENGTH_SHORT).show();
                                setContentView(R.layout.activity_main);
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }

    //change screen to forgot password when clicked
    public void forgotPassword (View view){
        setContentView(R.layout.forgot_password);
    }

    //reset password through the firebase backend
    //reset email generated and sent
    public void resetPassword(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = ((EditText)findViewById(R.id.emailAddress)).getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Password reset email sent.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Email sent.");
                            setContentView(R.layout.login_screen);
                        }else{
                            Toast.makeText(MainActivity.this, "Please check the email and try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //sign user out via mauth
    public void signOut (View view){
        mAuth.signOut();
        setContentView(R.layout.login_screen);
    }

}


