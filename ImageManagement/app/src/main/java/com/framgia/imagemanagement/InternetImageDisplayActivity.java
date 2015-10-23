package com.framgia.imagemanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.imagemanagement.adapters.GoogleImageBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by FRAMGIA\luu.vinh.loc on 23/10/2015.
 */
public class InternetImageDisplayActivity extends Activity{
    //
    private final static byte defauseValue = 0;
    private String imgUrl;
    private String imgTitle;
    private TextView imgName;
    private ImageView imgView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_image_display);
        //Set id to items
        setFindViewById();
        //receive intent to set mode
        getIntentFromPreviousScreen();
        //set listener for buttons
        setImageContext();
    }
    private void setFindViewById() {
        // get controller in this layout
        imgName = (TextView) findViewById(R.id.imgName);
        imgView = (ImageView) findViewById(R.id.soloImageView);
    }
    private void getIntentFromPreviousScreen() {
        Intent intent = getIntent();
        imgTitle = intent.getStringExtra(getString(R.string.imageTitle));
        imgUrl = intent.getStringExtra(getString(R.string.imageUrl));
        Log.e("----check----", imgUrl);
    }
    private void setImageContext() {
        // set chosen image to ImageView
        Picasso.with(this)
            .load(imgUrl)
            .into(imgView);
        // set text for a image
        imgName.setText(imgTitle);

    }
}
