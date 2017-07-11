package com.example.harperm.mydress;

import android.os.Bundle;
import java.util.List;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.firebase.auth.FirebaseAuth; //For Firbase Login
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import android.widget.Toast;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;



public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    //private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
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
     //We only need it to take one image capture
    /*public void openCamera (View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        //dispatchTakePictureIntent();
        //galleryAddPic();
    }*/

    String mCurrentPhotoPath;

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



    static final int REQUEST_TAKE_PHOTO = 1;

    public List<File> photoFileList = new ArrayList<File>();

    static final int REQUEST_IMAGE_CAPTURE = 1;

    File photoFile = null;
    public void openCamera (View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
                photoFileList.add(photoFile);
                Toast.makeText(getApplicationContext(), String.valueOf(photoFile), Toast.LENGTH_LONG).show();
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

            Toast.makeText(getApplicationContext(), String.valueOf(photoFileList), Toast.LENGTH_LONG).show();

        }

    }


    public void viewItems(View view) {
        setContentView(R.layout.closet_page2);
        if(photoFile != null) {
            ImageView imageView = (ImageView) findViewById(R.id.photo1);
            File pathToPicture = photoFileList.get(0);
            imageView.setImageBitmap(BitmapFactory.decodeFile(pathToPicture.getAbsolutePath()));
        }
        /*if(photoFile!= null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.photo1);
            myImage.setImageBitmap(myBitmap);
        }*/


    }

    //@Override
   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //setContentView(R.layout.closet_page2);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = (ImageView) findViewById(R.id.photo1);
            mImageView.setImageBitmap(imageBitmap);
        }
    }*/


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
    }
    public void signOut (View view){
        mAuth.signOut();
        setContentView(R.layout.login_screen);
    }
    public void saveData (View view){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("users").child(user.getUid()).child("testData").setValue("Hello, World!");
    }




}


