package com.project.kongdy.circleimageview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.kongdy.circleimageview.view.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView civ_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        civ_icon = (CircleImageView) findViewById(R.id.civ_icon);
        civ_icon.openExternalCircle(Color.CYAN);
        civ_icon.setInnerCircleColor(Color.CYAN);
    }
}
