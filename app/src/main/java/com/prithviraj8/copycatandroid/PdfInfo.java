package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class PdfInfo extends AppCompatActivity {

    TextView colorsTV,bwTV;
    Button done;
//    String colorType;
    String pdf_url;
    ArrayList<String> colorType = new ArrayList<String>();
    ArrayList<Integer> pdfCopies = new ArrayList<Integer>();
    ArrayList<String> pdfURL = new ArrayList<String>();
    EditText copies;
    String URI = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_info);

        colorsTV = findViewById(R.id.Pdf_Colors);
        bwTV = findViewById(R.id.Pdf_Black_White);
        done = findViewById(R.id.Pdf_done);
        copies = findViewById(R.id.PDF_copies);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pdf_url = extras.getString("PdfURL");
        URI = extras.getString("URI");

        pdfURL.add(pdf_url);


        colorsTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0xFA9A0A,0xD15DF8});
                Log.d("Colors","Pressed");

                colorType.add("Colors");
                colorsTV.setBackgroundColor(Color.parseColor("#FA9A0A"));
                bwTV.setBackgroundColor(Color.parseColor("#10000000"));
                return false;
            }
        });

        bwTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0x000000,0x616061});
                Log.d("Black/White","Pressed");

                colorType.add("Black/White");
                bwTV.setBackgroundColor(Color.parseColor("#616061"));
                colorsTV.setBackgroundColor(Color.parseColor("#10000000"));
                return false;
            }
        });



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfCopies.add(Integer.parseInt(copies.getText().toString()));

                Intent intent = new Intent(PdfInfo.this,ShopsActivity.class);
//                   intent.putParcelableArrayListExtra("FilesInfo",allInfo);
                intent.putStringArrayListExtra("URLS",pdfURL);
                intent.putIntegerArrayListExtra("Copies",pdfCopies);
                intent.putStringArrayListExtra("ColorTypes",colorType);
                startActivity(intent);
            }
        });
    }
}



