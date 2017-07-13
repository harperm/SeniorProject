package com.example.harperm.mydress;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.List;
import android.os.Environment;
import org.json.*;


import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.AnswersEvent;
import com.crashlytics.android.answers.ContentViewEvent;
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
import java.util.Map;
import java.util.jar.*;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

import static android.R.attr.bitmap;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.mobileconnectors.apigateway.*;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    public List<File> photoFileList = new ArrayList<File>();
    File photoFile = null;
    String mCurrentPhotoPath;

    private TextView mTextMessage;

    private static final String TAG = "EmailPassword";

    // [START initialize_auth]
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // [END initialize_auth]



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

    public String userEmail (){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        return email;
    }

    public void readFromDatabase(){
        String intEmail = userEmail();
        String email1 = intEmail.replaceAll("@", "");
        String email = email1.replaceAll("\\.", "");
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(email);
        Toast.makeText(getApplicationContext(), String.valueOf(mDatabaseReference), Toast.LENGTH_LONG).show();

        // NEED TO FINISH THIS
    }

    public void saveToDatebase (String string){
        String intEmail = userEmail();
        String email1 = intEmail.replaceAll("@", "");
        String email = email1.replaceAll("\\.", "");
        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(email);

        myRef.child("PhotoPath").setValue(string);
    }

    public void homepage (View view) {
        setContentView(R.layout.activity_main);
    }

    public void navigateLogin (View View){
        setContentView(R.layout.login_screen);
    }

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

    public void colorTag () throws UnirestException {

        Toast.makeText(MainActivity.this, String.valueOf(photoFileList.get(0)),
                Toast.LENGTH_SHORT).show();


        // Old API Key
        //  Z2wYzX8a6imshpT1USvHw9MsrumRp1sjz78jsn7Gw78pE6MMCE

        String pathToPicture = String.valueOf(photoFileList.get(0));
        HttpResponse<JsonNode> response = Unirest.post("https://apicloud-colortag.p.mashape.com/tag-file.json")
                .header("X-Mashape-Key", "LhufMNcMDGmsh6pcmPjk8iVM0fdkp1lSlX1jsnVDc20yDqdhen")
                .field("image", new File(pathToPicture))
                .field("palette", "simple")
                .field("sort", "relevance")
                .asJson();

        JSONObject myObj = response.getBody().getObject();

        try {
            String test = myObj.getString("tags");
            Toast.makeText(MainActivity.this, test, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    public void viewItems(View view) {
        setContentView(R.layout.closet_page2);
        LinearLayout layout1 = (LinearLayout)findViewById(R.id.LinearLayout1); //0-2
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.LinearLayout2); //3-5
        LinearLayout layout3 = (LinearLayout)findViewById(R.id.LinearLayout3); //6-8
        LinearLayout layout4 = (LinearLayout)findViewById(R.id.LinearLayout4); //9-11
        LinearLayout layout5 = (LinearLayout)findViewById(R.id.LinearLayout5); //12-14
        LinearLayout layout6 = (LinearLayout)findViewById(R.id.LinearLayout6); //15-17

        if(photoFile != null) {
            int numberOfPhotos = photoFileList.size();
            for(int i=0; i < numberOfPhotos; i++) {

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int width = metrics.widthPixels;
                int height = metrics.heightPixels;


                ImageView image = new ImageView(this);
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(width/3,height/3));
                //image.setMaxHeight(75);
                //image.setMaxWidth(75);

                if(i >= 0 && i <= 2) {
                    layout1.addView(image);
                    File pathToPicture = photoFileList.get(i);
                    image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));

                }
                saveToDatebase(String.valueOf(photoFileList));

                readFromDatabase();
                //File imgFile = photoFileList.get(0);
                //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //int a = getDominantColor1(myBitmap);
                //Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();


                /*
                if(i >= 3 && i <= 5) {
                    layout2.addView(image);
                    File pathToPicture = photoFileList.get(i);
                    image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                }
                if(i >= 6 && i <= 8) {
                    layout3.addView(image);
                    File pathToPicture = photoFileList.get(i);
                    image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                }
                if(i >= 9 && i <= 11) {
                    layout4.addView(image);
                    File pathToPicture = photoFileList.get(i);
                    image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                }
                if(i >= 12 && i <= 14) {
                    layout5.addView(image);
                    File pathToPicture = photoFileList.get(i);
                    image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                }
                if(i >= 15 && i <= 17) {
                    layout6.addView(image);
                    File pathToPicture = photoFileList.get(i);
                    image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
                }*/
            }
        }

    }


/*
    public static int getDominantColor1(Bitmap bitmap) {
        Palette.generate(bitmap);
        if (bitmap == null)
            throw new NullPointerException();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);

        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);

        final List<HashMap<Integer, Integer>> colorMap = new ArrayList<HashMap<Integer, Integer>>();
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());

        int color = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        Integer rC, gC, bC;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            rC = colorMap.get(0).get(r);
            if (rC == null)
                rC = 0;
            colorMap.get(0).put(r, ++rC);

            gC = colorMap.get(1).get(g);
            if (gC == null)
                gC = 0;
            colorMap.get(1).put(g, ++gC);

            bC = colorMap.get(2).get(b);
            if (bC == null)
                bC = 0;
            colorMap.get(2).put(b, ++bC);
        }

        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            int max = 0;
            int val = 0;
            for (Map.Entry<Integer, Integer> entry : colorMap.get(i).entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    val = entry.getKey();
                }
            }
            rgb[i] = val;
        }

        int dominantColor = Color.rgb(rgb[0], rgb[1], rgb[2]);

        return dominantColor;
    }
*/

    public void createAccount (View view){
        setContentView(R.layout.create_account);
    }
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
        }
        else if(numberCheck == false){
                Toast.makeText(MainActivity.this, "Password must contain at least 1 digit.",
                        Toast.LENGTH_SHORT).show();
                ((EditText)findViewById(R.id.password)).setText("");

        }
        else if(capitalCheck == false){
            Toast.makeText(MainActivity.this, "Password must contain at least 1 capital letter.",
                    Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.password)).setText("");
        }
        else if (lowerCheck == false){
            Toast.makeText(MainActivity.this, "Password must contain at least 1 lower case letter.",
                    Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.password)).setText("");
        }else if(passwordConfirm != password){
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
    public void forgotPassword (View view){
        setContentView(R.layout.forgot_password);

        String email = ((EditText)findViewById(R.id.emailAddress)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });


    }
    public void signOut (View view){

        try {
            colorTag();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
//        mAuth.signOut();
//        setContentView(R.layout.login_screen);
    }
    public void saveData (View view){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("users").child(user.getUid()).child("testData").setValue("Hello, World!");
    }

}


