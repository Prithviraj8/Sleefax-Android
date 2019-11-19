package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class Pop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        getSupportActionBar().hide();

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int) (height));

        Button selectPhotos = findViewById(R.id.selectphotos);
        Button selectAttachment = findViewById(R.id.selectattachment);

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pop.this,Select.class);
                Bundle extras = new Bundle();
                extras.putInt("Photos",1);
                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        });
        selectAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pop.this,Select.class);
                Bundle extras = new Bundle();
                extras.putInt("Photos",0);
                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        });

    }
}
