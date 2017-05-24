package com.example.harperm.mydress;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.util.Log;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //mTextMessage = (TextView) findViewById(R.id.message);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

}
