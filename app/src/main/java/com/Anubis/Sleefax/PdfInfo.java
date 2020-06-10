package com.Anubis.Sleefax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.material.internal.ViewUtils.dpToPx;

public class PdfInfo extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId;
    int ShopsCnt=0,pdfCnt=0;

//    String colorType;

    RelativeLayout done, viewFileRL;
    LinearLayout mainUI;

    Spinner pageSizeSpinner, orientSpinner;
    View colorsTV,bwTV,h,v;
    ToggleButton bothSidePrint;
    ImageButton back,scrollDown;
    Button viewPdf, dismissViewer;
    ScrollView scrollView,viewFileScrollView;
    PDFView pdfView;
    WebView webView;
    EditText customValue1,customValue2;
    RelativeLayout bottomRelativeView;
    RelativeLayout rootLayout;
    RelativeLayout upperLayout;
    int copy,custValue1,custValue2;


    //    String pdf_url;
    String pdf_url,pdf_uri;
    int resultCode,requestCode;
    int mScreenHeight;

    String colour, pagesize;

    ArrayList<String> pdfURL = new ArrayList<>();
    ArrayList<String> fileType = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<String> customValues = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();


    EditText copiesTV,pageCount;
    String URI = "";
    String orientation,fileName;
    String username,email,custom,shopType,customVal;
//    boolean bothSides = false;
    boolean isTester,newUser;

    ArrayList<String> storeID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_info);

        bottomRelativeView = findViewById(R.id.bottom_view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;

        upperLayout = findViewById(R.id.pdfsettingsRL2);
        if(upperLayout.getHeight() == 0)
            expandView(upperLayout,0,mScreenHeight/3);

        rootLayout = findViewById(R.id.pdfsettingsRL);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
                if(diff > dpToPx(getApplicationContext(),200)){
                    bottomRelativeView.setVisibility(View.INVISIBLE);
                }
                else
                    bottomRelativeView.setVisibility(View.VISIBLE);
            }
        });
//        getSupportActionBar().hide();

        pdfCnt = 0;

        colorsTV = findViewById(R.id.Pdf_Colors);
        bwTV = findViewById(R.id.Pdf_Black_White);
        done = findViewById(R.id.pdfDone);
        copiesTV = findViewById(R.id.PDF_copies);
        pageSizeSpinner = findViewById(R.id.sizeSpinner);
        orientSpinner = findViewById(R.id.orientationSpinner);
        scrollView = findViewById(R.id.scrollViewPdfs);
        scrollDown = findViewById(R.id.scrollDownPdf);

        viewFileRL = findViewById(R.id.viewFileBtnRL);
        viewPdf = findViewById(R.id.viewPdfBtn);
        pageCount = findViewById(R.id.PageCount);
        pdfView = findViewById(R.id.viewPDF);
        dismissViewer = findViewById(R.id.DismissViewer);
        viewFileScrollView = findViewById(R.id.ViewerScollView);
        mainUI = findViewById(R.id.ViewerLinearLayout);


        final String[] sizes = new String[]{"A4", "A3", "A2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sizes);

        final String[] orientations = new String[]{"Portrait", "Landscape"};
        ArrayAdapter<String> orientationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, orientations);

        pageSizeSpinner.setAdapter(adapter);
        orientSpinner.setAdapter(orientationsAdapter);

        customValue1 = findViewById(R.id.customValue1);
        customValue2 = findViewById(R.id.customValue2);



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pdfURL = extras.getStringArrayList("URLS");
        fileType = extras.getStringArrayList("FileType");
        username = extras.getString("username");
        email = extras.getString("email");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");
        numberOfPages = extras.getIntegerArrayList("Pages");
        isTester = extras.getBoolean("IsTester");
        newUser = extras.getBoolean("NewUser");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");

        bothSides = new boolean[pdfURL.size()];

        customValue1.setText("1");
        if(numberOfPages == null){
            numberOfPages.add(pdfCnt,10);
            customValue2.setText(String.valueOf(1));

        }else{
            customValue2.setText(numberOfPages.get(pdfCnt) != null ? (String.valueOf((int) numberOfPages.get(pdfCnt))) : "1");

        }

        if(!newUser){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        if(isTester){
            shopType = "TestStores";
        }else{
            shopType = "Stores";
        }
//        fileName = extras.getString("FileName");
//        pdf_uri = extras.getParcelable("PDFUri");


//        pdfURL.add((pdf_url));

        back = findViewById(R.id.back);
        back.setOnClickListener(BtnListener);

        bothSidePrint = findViewById(R.id.bothSidesToggle);

        bothSidePrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                // The toggle is disabled


                bothSides[pdfCnt] = isChecked;

            }
        });

//        if(pdfURL.size() == 1){
//            done.setVisibility(View.VISIBLE);
//        }else{
//            done.setVisibility(View.GONE);
//        }

        colorsTV.setOnTouchListener(touchListener);
        bwTV.setOnTouchListener(touchListener);
        done.setOnClickListener(BtnListener);
        scrollDown.setOnClickListener(BtnListener);
        viewPdf.setOnClickListener(BtnListener);
        dismissViewer.setOnClickListener(BtnListener);


    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(view == findViewById(R.id.Pdf_Black_White)){

                colour = ("Black/White");
                colors.add(pdfCnt,colour);

                bwTV.setBackgroundResource(R.drawable.b_w_border);
                colorsTV.setBackgroundResource(R.drawable.black_white_view_backgroud);
            }
            if(view == findViewById(R.id.Pdf_Colors)){

                colour = "Colors";
                colors.add(pdfCnt,colour);

                colorsTV.setBackgroundResource(R.drawable.colors_border);
                bwTV.setBackgroundResource(R.drawable.black_white_view_backgroud);

            }

            return false;
        }
    };

    //Create an anonymous implementation of OnClickListener
    private View.OnClickListener BtnListener = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onClick(View v) {


            if(v == findViewById(R.id.back)) {
                if(pdfCnt>0){
                    pdfCnt = pdfCnt - 1;
                }else{
                    finish();
                }
            }

            if(v == findViewById(R.id.pdfDone)) {
                Toast.makeText(PdfInfo.this, "PDFCNT "+customValue2.getText(), Toast.LENGTH_SHORT).show();
                if(pdfCnt == pdfURL.size()-1) {
                    new findShops().execute();
                }

                copy = (Integer.parseInt(copiesTV.getText().toString()));
                if (copy == 0) {
                    copy = 1;
                }
                copies.add(pdfCnt,copy);

//                custom = customPages.getText().toString();
                final String[] sizes = new String[]{"A4", "A3", "A2"};
                pagesize = sizes[pageSizeSpinner.getSelectedItemPosition()];
                pageSize.add(pdfCnt,pagesize);

                final String[] ots = new String[]{"Portrait", "Landscape"};
                orientation = ots[orientSpinner.getSelectedItemPosition()];

//                if (customPages.getText().toString().equals("")) {
//                    custom = "All";
//                }

                custValue1 = 1;
                custValue2 = 2;

                if(customValue1.getText().toString().equals("1")){
                    custValue1 = 1;
                }else{
                    custValue1 = Integer.parseInt(customValue1.getText().toString());
                }

                if(customValue2.getText().toString().equals(String.valueOf(numberOfPages))){
                    custValue2 = (int) numberOfPages.get(pdfCnt);
                }else{
                    custValue2 = Integer.parseInt(customValue2.getText().toString());
                }


                if(orientation == null){
                    orientation = "Portrait";
                }
                orientations.add(pdfCnt,orientation);

                if(colour == null){
                    colour = "Black/White";
                }
                colors.add(pdfCnt,colour);

                if(custValue1 == 1){
                    if(pdfCnt < numberOfPages.size() && custValue2 == (int) numberOfPages.get(pdfCnt)){
                        custom = "All";
                        customPages.add(pdfCnt,custom);
                    }else {
                        custom = "All";
                        customPages.add(pdfCnt,custom);
                    }

                }else if(custValue1 != 1 && custValue2 != (int) numberOfPages.get(pdfCnt)){

                    if(custValue1 > custValue2){
                        int temp = custValue1;
                        custValue1 = custValue2;
                        custValue2 = temp;

                        custom = String.valueOf(custValue1)+"-"+String.valueOf(custValue2);
                        customPages.add(pdfCnt,custom);

                        customVal = String.valueOf(custValue2-custValue1 + 1);
                        customValues.add(pdfCnt,customVal);

                    }else{
                        custom = String.valueOf(custValue1)+"-"+String.valueOf(custValue2);
                        customPages.add(pdfCnt,custom);

                        customVal = String.valueOf(custValue2-custValue1 + 1);
                        customValues.add(pdfCnt,customVal);

                    }
                    pdfCnt = pdfCnt + 1;
                    customValue2.setText((int) numberOfPages.get(pdfCnt));
                    Toast.makeText(PdfInfo.this, "CUSV2 "+customValue2.getText(), Toast.LENGTH_SHORT).show();

//                    if(pdfCnt == pdfURL.size()) {
//                        new findShops().execute();
//                    }
                }
                else {

                    if (custValue1 > custValue2) {
                        int temp = custValue1;
                        custValue1 = custValue2;
                        custValue2 = temp;

                        custom = (custValue1) + "-" + (custValue2);
                        customPages.add(pdfCnt, custom);

                        customVal = String.valueOf(custValue2 - custValue1 + 1);
                        customValues.add(pdfCnt, customVal);

                    } else {
                        custom = (custValue1) + "-" + (custValue2);
                        customPages.add(pdfCnt, custom);

                        customVal = String.valueOf(custValue2 - custValue1 + 1);
                        customValues.add(pdfCnt, customVal);

                    }
                }
                Toast.makeText(PdfInfo.this, "CUSV2 "+customValue2.getText(), Toast.LENGTH_SHORT).show();
                pdfCnt = pdfCnt + 1;
                    if(pdfCnt < pdfURL.size() ) {
                        if(pdfCnt < numberOfPages.size()) {
                            customValue2.setText(String.valueOf((int) numberOfPages.get(pdfCnt)));
                        }else{
                            customValue2.setText(String.valueOf(1));
                        }
                    }


            }


            if(v == findViewById(R.id.scrollDownPdf)){
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            if(v == findViewById(R.id.viewPdfBtn)) {
                dismissViewer.setVisibility(View.VISIBLE);
                viewFileRL.setVisibility(View.GONE);

                Toast.makeText(PdfInfo.this, "DISMISS BTN  ", Toast.LENGTH_SHORT).show();
                if (fileType.get(pdfCnt).contains("pdf")) {
                    viewPdf.setVisibility(View.INVISIBLE);
                    bothSidePrint.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.INVISIBLE);
                    pdfView.setVisibility(View.VISIBLE);


                    pdfView.fromUri(Uri.parse(pdfURL.get(pdfCnt)))
                            .enableSwipe(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
                            .enableDoubletap(true)
                            .onPageError(new OnPageErrorListener() {
                                @Override
                                public void onPageError(int page, Throwable t) {
                                    Log.d("PAGE ERROR", String.valueOf(page));
                                    Log.d("ERROR IS", String.valueOf(t));
                                }
                            })
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    Log.d("PDFNOP", String.valueOf(pdfView.getPageCount()));
//                                numberOfPages = pdfView.getPageCount();
                                }
                            })
                            .load();

                }else if(fileType.get(pdfCnt).contains("document")){

                    mainUI.removeAllViewsInLayout();
                    Toast.makeText(PdfInfo.this, "OPENING DOCX ", Toast.LENGTH_SHORT).show();
                    new OpenDocFile(findViewById(R.id.ViewerLinearLayout),findViewById(R.id.ViewerScollView),Uri.parse(pdfURL.get(pdfCnt)),"DOCX");


                }else if(fileType.get(pdfCnt).contains("msword")){
                    mainUI.removeAllViewsInLayout();
                    Toast.makeText(PdfInfo.this, "OPENING WORD ", Toast.LENGTH_SHORT).show();
                    new OpenDocFile(findViewById(R.id.ViewerLinearLayout),findViewById(R.id.ViewerScollView),Uri.parse(pdfURL.get(pdfCnt)),"WORD");

                }
                else if(fileType.get(pdfCnt).contains("powerpoint") || fileType.get(pdfCnt).contains("msword")){
                    Toast.makeText(PdfInfo.this, "YUP "+fileType.get(pdfCnt), Toast.LENGTH_SHORT).show();
                    ViewFileFromAnotherApp(pdfURL.get(pdfCnt),fileType.get(pdfCnt));
                }
            }else if(v == findViewById(R.id.DismissViewer)){

                viewFileScrollView.setVisibility(View.INVISIBLE);
                viewFileRL.setVisibility(View.VISIBLE);
                dismissViewer.setVisibility(View.GONE);
                upperLayout.setVisibility(View.VISIBLE);

            }
        }
    };

    @Override
    public void onBackPressed() {
        if(pdfView.VISIBLE == 0){
            viewPdf.setVisibility(View.VISIBLE);
            bothSidePrint.setVisibility(View.VISIBLE);
            done.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.INVISIBLE);
        }
    }



    public void ViewFileFromAnotherApp(String word, String mimeType) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(word),mimeType);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Open File with"));
        }
        else {
            Toast.makeText(this, "No app found for opening this document", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void alertBox(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PdfInfo.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Oh! Got it",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    KProgressHUD hud;

public class findShops extends AsyncTask<Void,Void,Integer>{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
          hud = KProgressHUD.create(PdfInfo.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Finding stores")
                .setMaxProgress(100)
                .show();
    }

    int shopsCount;
    @Override
    protected Integer doInBackground(Void... integers) {
        getShopsCount();
//        Log.d("Shops Count is ", String.valueOf(shopsCount));
        return ShopsCnt;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
//        hud.dismiss();
    }
}


    private void getShopsCount(){


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                if(isTester){
                    if(dataSnapshot.getKey().equals(shopType)) {
                        ShopsCnt = (int) dataSnapshot.getChildrenCount();

                        Intent intent = new Intent(PdfInfo.this, ShopsActivity.class);
                        Bundle extras = new Bundle();
                        Toast.makeText(PdfInfo.this, "PDFCNT2 "+pdfURL.size(), Toast.LENGTH_SHORT).show();
                        extras.putInt("ShopCount", ShopsCnt);
                        extras.putStringArrayList("StoreID", storeID);

                        extras.putStringArrayList("URLS", pdfURL);
                        extras.putIntegerArrayList("Pages", numberOfPages);
                        extras.putBooleanArray("BothSides", bothSides);
                        extras.putIntegerArrayList("Copies", copies);
                        extras.putStringArrayList("ColorType", colors);
                        extras.putStringArrayList("FileType", fileType);
                        extras.putStringArrayList("PageSize", pageSize);
                        extras.putStringArrayList("Orientation", orientations);
                        extras.putStringArrayList("FileNames",fileNames);
                        extras.putStringArrayList("FileSizes",fileSizes);
                        extras.putBoolean("NewUser",newUser);

                        extras.putStringArrayList("Custom",customPages);
                        extras.putStringArrayList("CustomValue",customValues);
//                        if(custom == "All"){
//                            extras.putString("Custom", customPages);
//                        }else {
//                            extras.putString("CustomValue",customVal);
//                            extras.putString("Custom", custom);
//                        }
                        extras.putBoolean("IsTester",isTester);
                        intent.putExtras(extras);
                        hud.dismiss();
                        startActivity(intent);
                    }

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void expandView(final View v, int initialHt, int finalHt){




        ValueAnimator slideAnimator = ValueAnimator.ofInt(initialHt,finalHt + 20).setDuration(1000);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                v.getLayoutParams().height = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                v.requestLayout();

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }


    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }



    public class OpenDocFile{



        /// Creating In-App Document and PowerPoint Viewer
        LinearLayout mainUI;
        List<XWPFPictureData> picList;
        ScrollView viewerScollView;
        Uri file;
        String whatFile;

        public OpenDocFile(View mainUI, View viewerScollView, Uri file, String whatFile) {

            this.mainUI = (LinearLayout) mainUI;
            this.viewerScollView = (ScrollView) viewerScollView;
            this.file = file;
            this.whatFile = whatFile;

            openDocxViewer(file);

        }



        {
            System.setProperty(
                    "org.apache.poi.javax.xml.stream.XMLInputFactory",
                    "com.fasterxml.aalto.stax.InputFactoryImpl"
            );
            System.setProperty(
                    "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                    "com.fasterxml.aalto.stax.OutputFactoryImpl"
            );
            System.setProperty(
                    "org.apache.poi.javax.xml.stream.XMLEventFactory",
                    "com.fasterxml.aalto.stax.EventFactoryImpl"
            );
        }


        public void openDocxViewer(Uri file){

//            ScrollView viewerScollView = findViewById(R.id.ViewerScollView);
//            mainUI = findViewById(R.id.ViewerLinearLayout);
            upperLayout.setVisibility(View.GONE);


            try {
                //this is action performed after openDocumentFromFileManager() when doc is selected

                FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(file);
                setUpDocx docxObj = new setUpDocx();
                setUpWord wordObj = new setUpWord();

                if(whatFile.equals("DOCX") || whatFile.contains("WORD")) {

                    Toast.makeText(PdfInfo.this,"SETUP WORD",Toast.LENGTH_LONG).show();

                    XWPFDocument docx = new XWPFDocument(inputStream);

                    docxObj.traverseBodyElements(docx.getBodyElements());
                    picList = docx.getAllPackagePictures();
                }
//                else if(whatFile.equals("WORD")){
//                        Toast.makeText(PdfInfo.this,"SETUP DOCX",Toast.LENGTH_LONG).show();
//
//                    HWPFDocument wordDoc = new HWPFDocument(inputStream);
//                    WordExtractor extractor = new WordExtractor(wordDoc);
//
//
//                    Range range = wordDoc.getRange();
//                    String[] paragraphs = extractor.getParagraphText();
//
//                    PicturesTable picturesTable = wordDoc.getPicturesTable();
//                    List<Picture> all = picturesTable.getAllPictures();
//
//                    for(int i =0;i < paragraphs.length;i++){
//                        Paragraph pr = range.getParagraph(i);
//
////                            Log.i("text",pr.text());
//                        int j =0 ;
//
//                        while(true){
//                            CharacterRun run = pr.getCharacterRun(j++);
//
//                            StyleDescription style = wordDoc.getStyleSheet().getStyleDescription(run.getSubSuperScriptIndex());
//                            String styleName = style.getName();
//                            String font = run.getFontName();
//                            int size = run.getFontSize();
//                            String paraText = pr.text();
//                            Boolean b = run.isBold();
//                            int u = run.getUnderlineCode();
//
//                            wordObj.addTextViews(paraText,size,b,u,font);
//
//                            if(picturesTable.hasPicture(run)){
//                                Picture p = picturesTable.extractPicture(run,true);
//                                wordObj.traversePictures(p);
//                            }
//
//                            Log.i("name",styleName);
//                            Log.i("font",Integer.toString(size));
//                            Log.i("family",font);
//                            Log.i("text",paraText);
//
//
////
////                                List<Picture> pictures = wordDoc.getPicturesTable().getAllPictures();
////                                traversePictures(pictures);
//
//                            if (run.getEndOffset() == pr.getEndOffset()) {
//                                break;
//                            }
//                        }
//                    }
//                }
                viewerScollView.setVisibility(View.VISIBLE);




            } catch (IOException e) {
                e.printStackTrace();
                ViewFileFromAnotherApp(file.toString(),fileType.get(pdfCnt));
            } catch (Exception e) {
                e.printStackTrace();
                ViewFileFromAnotherApp(file.toString(),fileType.get(pdfCnt));

            }

        }


        //// Setting up functions for MSWORD

    public class setUpWord {
        private void traversePictures(Picture pic) {

            Log.i("pictureData",pic.getContent().toString());

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;


            long w = pic.getWidth();
            long h = pic.getHeight();

            addElements(pic,w,h,height,width);
        }

        int TagCnt = 0;

        public void addTextViews(String content, int s, Boolean b,int u,String f) {
            TextView text = new TextView(PdfInfo.this);
            text.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
            SpannableString c = new SpannableString(content);
            c.setSpan(new UnderlineSpan(),0,content.length(),0);
            text.setTag(TagCnt);

            ArrayList<TextView> textViews = new ArrayList<>();
//        TextView myText = (TextView) mainUI.findViewWithTag(TagCnt - i); // get the element

            for (int i=TagCnt - 1;i >= 0;i--){

                TextView myText = (TextView) mainUI.findViewWithTag(i); // get the element
                if(myText != null) {
                    Log.d("CONT ",String.valueOf(content));
                    if((content.equals(myText.getText().toString()))){
                        Log.d("MYTXT ",myText.getText().toString());
                        Log.d("ICNT ",String.valueOf(i));
                        break;

                    }else{
                        setProperText(text, content, s, b, u, f);
                        break;
                    }
                }else{
                    Log.d("NTAGCNT ",String.valueOf(TagCnt));
                    Log.d("NICNT ",String.valueOf(i));
                    Log.d("NULLTXT ",content);
                }
            }
            if(TagCnt == 0 ){
                setProperText(text, content, s, b, u, f);
            }
            TagCnt = TagCnt + 1;
        }

        public void setProperText(TextView text,String content, int s, Boolean b, int u,String fontFamily) {
            SpannableString c = new SpannableString(content);
            c.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            text.setPadding(50, 10, 50, 10);
            text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            if (b) {
                text.setTextColor(Color.BLACK);
                text.setTextSize((int) ((3 * s) / 4));
                //text.setTypeface(null, FontStyle.fontFamily);
                text.setTypeface(null, Typeface.BOLD);

                if (u > 0) {
                    text.setText(c);
                } else {
                    //text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    text.setText(content);
//                text.setGravity(Gravity.CENTER);
                }
//                if(!c.equals(myText.getText().toString()) || !content.equals(myText.getText().toString())) {
                mainUI.addView(text);
//                }
            } else {
                text.setTextColor(Color.BLACK);
                text.setTextSize((int) ((3 * s) / 4));

                if (u > 0) {
                    text.setText(c);
                } else {
                    //               text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    text.setText(content);
//                text.setGravity(Gravity.CENTER);
                }
//                if(!c.equals(myText.getText().toString()) || !content.equals(myText.getText().toString())) {
                mainUI.addView(text);
//                }
            }

            // Adds the view to the layout
            LinearLayout textLayout = new LinearLayout(getApplicationContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, text.getId());
            textLayout.setLayoutParams(params);
            mainUI.addView(textLayout);

        }

        public  void addElements(Picture pictureData,long w,long h,int height,int width){

            ArrayList<ImageView> imageViews = new ArrayList<>();
//
            ImageView image = new ImageView(PdfInfo.this);

            if((int)w >= width){
                w = width - 90;
                h = h / (width-100);
            }
            else if((int)w < width){
                w = width ;
                h = height / 3;
//            h = height;
            }

//        image.setForegroundGravity(Gravity.CENTER);
            image.setPadding(50,10,50,10);
            image.setLayoutParams(new RelativeLayout.LayoutParams((int)w,(int)h));
            image.setMaxHeight((int)h);
            image.setMaxWidth((int)w);
//        image.setAdjustViewBounds(true);
//        image.setScaleType(ImageView.ScaleType.MATRIX);
            InputStream inputStream = new ByteArrayInputStream(pictureData.getContent());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

//        Matrix matrix = new Matrix();
//        matrix.postScale(1/2800, 1/2800);
//        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap,0,0,(int)w,(int)h,matrix,true);

            image.setImageBitmap(bitmap);
            mainUI.addView(image);


            // Adds the view to the layout
            LinearLayout imageLayout = new LinearLayout(getApplicationContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, image.getId());
            imageLayout.setLayoutParams(params);
            mainUI.addView(imageLayout);

        }

    }



        ////// Setting up DOCX Functions

    public class setUpDocx {
        private void textViews(XWPFParagraph paragraph, List<IRunElement> runElements) {
            String paragraphText = paragraph.getParagraphText();

            int size;
            UnderlinePatterns u;
            Boolean b;
            String ff;

            for (IRunElement runElement : runElements) {
                if (runElement instanceof XWPFRun) {
                    XWPFRun run = (XWPFRun) runElement;
                    System.out.println("runClassName " + run.getClass().getName());
                    System.out.println("run " + run);

                    //Appending text to paragraph
                    para.append(run);
                    paras.add(para);

                    size = run.getFontSize();
                    u = run.getUnderline();
                    b = run.isBold();
                    ff = run.getFontFamily();

                    if (paragraphText.length() > 1) {
                        addTextViews(paragraphText, size, b, u, ff);
                    }

                }
            }

        }

        public void traversePictures(List<XWPFPicture> pictures)  {
            for (XWPFPicture picture : pictures) {

                System.out.println("Picture "+picture);
                XWPFPictureData pictureData = picture.getPictureData();
                Log.i("PictureData ", pictureData.toString());

                long w = picture.getCTPicture().getSpPr().getXfrm().getExt().getCx();
                long h = picture.getCTPicture().getSpPr().getXfrm().getExt().getCy();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                addElements(pictureData,w,h,height,width);
//            addElementsUI(null,pictureData);
            }

        }

        StringBuilder para = new StringBuilder();
        private  ArrayList<StringBuilder> paras = new ArrayList<>();
        private  int paraIndex = 0;

        public void traverseRunElements(List<IRunElement> runElements) throws Exception {

            System.out.println("PARAINDICES"+ paras.size());
            System.out.println("TRAVERSE RUN ELEMENTS");

            for (IRunElement runElement : runElements) {
//            if (runElement instanceof XWPFFieldRun) {
//                XWPFFieldRun fieldRun = (XWPFFieldRun)runElement;
//                System.out.println("fieldRunClassName "+fieldRun.getClass().getName());
//                System.out.println("fieldName "+fieldRun);
//                traversePictures(fieldRun.getEmbeddedPictures());
//            }
//            else if (runElement instanceof XWPFHyperlinkRun) {
//                XWPFHyperlinkRun hyperlinkRun = (XWPFHyperlinkRun)runElement;
//                System.out.println("hyperLinkRunClassName "+ hyperlinkRun.getClass().getName());
//                System.out.println("hyperlinkRun "+hyperlinkRun);
//                traversePictures(hyperlinkRun.getEmbeddedPictures());
//            } else
                if (runElement instanceof XWPFRun) {
                    XWPFRun run = (XWPFRun)runElement;
                    System.out.println("runClassName "+run.getClass().getName());
                    System.out.println("run "+run);

                    //Appending text to paragraph
                    para.append(run);
                    paras.add(para);
                    Log.i("font family",run.getFontFamily());
                    traversePictures(run.getEmbeddedPictures());

                } else if (runElement instanceof XWPFSDT) {
                    XWPFSDT sDT = (XWPFSDT)runElement;
                    System.out.println("sDT"+sDT);
                    System.out.println("SDT_CONTENT "+sDT.getContent());
                    //ToDo: The SDT may have traversable content too.
                }
            }
        }

//    public void traverseTableCells(List<ICell> tableICells) throws Exception {
//        for (ICell tableICell : tableICells) {
//            if (tableICell instanceof XWPFSDTCell) {
//                XWPFSDTCell sDTCell = (XWPFSDTCell)tableICell;
//                System.out.println("sDTCELL "+sDTCell);
//                //ToDo: The SDTCell may have traversable content too.
//            } else if (tableICell instanceof XWPFTableCell) {
//                XWPFTableCell tableCell = (XWPFTableCell)tableICell;
//                System.out.println("TableCell "+tableCell);
//                traverseBodyElements(tableCell.getBodyElements());
//            }
//        }
//    }

        public void traverseTableRows(List<XWPFTableRow> tableRows) throws Exception {
            for (XWPFTableRow tableRow : tableRows) {
                System.out.println("TableRow "+tableRow);
//            traverseTableCells(tableRow.getTableICells());
            }
        }

        public void traverseBodyElements(List<IBodyElement> bodyElements) throws Exception {
            System.out.println("TRAVERSE BODY ELEMENTS");

            for (IBodyElement bodyElement : bodyElements) {
                if (bodyElement instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph)bodyElement;
                    System.out.println("PARA "+paragraph);

                    //Creating textView & paragraph using String Builder
                    paras.add(new StringBuilder());
                    textViews(paragraph,paragraph.getIRuns());
                    traverseRunElements(paragraph.getIRuns());
                    paraIndex = paraIndex + 1;

                } else if (bodyElement instanceof XWPFSDT) {
                    XWPFSDT sDT = (XWPFSDT)bodyElement;
                    System.out.println("SDT"+sDT);
                    System.out.println("SDT_CONTENT "+sDT.getContent());
                    //ToDo: The SDT may have traversable content too.
                } else if (bodyElement instanceof XWPFTable) {
                    XWPFTable table = (XWPFTable)bodyElement;
                    System.out.println("TABLE"+table);
                    traverseTableRows(table.getRows());
                }
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        public  void addElements(XWPFPictureData pictureData, long w, long h, int height, int width){

            ArrayList<ImageView> imageViews = new ArrayList<>();
//
            ImageView image = new ImageView(PdfInfo.this);

            if((int)w > width){
                w = width - 100;
                h = h / (5 *(width-100));
            }

            image.setForegroundGravity(Gravity.CENTER);
            image.setPadding(100,30,50,30);
            image.setLayoutParams(new RelativeLayout.LayoutParams((int)(w),(int)(h)));
            image.setMaxHeight((int)h/2800);
            image.setMaxWidth((int)w/2800);
            InputStream inputStream = new ByteArrayInputStream(pictureData.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            image.setImageBitmap(bitmap);
            mainUI.addView(image);


            // Adds the view to the layout
            LinearLayout imageLayout = new LinearLayout(getApplicationContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, image.getId());
            imageLayout.setLayoutParams(params);
            mainUI.addView(imageLayout);

        }

        int TagCnt = 0;
        public void addTextViews(String content, int s, Boolean b, UnderlinePatterns u,String f){


            TextView text = new TextView(PdfInfo.this);
            text.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
            SpannableString c = new SpannableString(content);
            c.setSpan(new UnderlineSpan(),0,content.length(),0);
            text.setTag(TagCnt);

            ArrayList<TextView> textViews = new ArrayList<>();
//        TextView myText = (TextView) mainUI.findViewWithTag(TagCnt - i); // get the element

            for (int i=TagCnt - 1;i >= 0;i--){

                TextView myText = (TextView) mainUI.findViewWithTag(i); // get the element
                if(myText != null) {
                    Log.d("CONT ",String.valueOf(content));
                    if((content.equals(myText.getText().toString()))){
                        Log.d("MYTXT ",myText.getText().toString());
                        Log.d("ICNT ",String.valueOf(i));
                        break;

                    }else{
                        setProperText(text, content, s, b, u, f);
                        break;
                    }
                }else{
                    Log.d("NTAGCNT ",String.valueOf(TagCnt));
                    Log.d("NICNT ",String.valueOf(i));
                    Log.d("NULLTXT ",content);
                }
            }
            if(TagCnt == 0 ){
                setProperText(text, content, s, b, u, f);
            }
            TagCnt = TagCnt + 1;

        }

        public void setProperText(TextView text,String content, int s, Boolean b, UnderlinePatterns u,String fontFamily){
            SpannableString c = new SpannableString(content);
            c.setSpan(new UnderlineSpan(),0,content.length(),0);
            text.setPadding(60, 10, 50, 10);

            if (b) {
                text.setTextColor(Color.BLACK);
                text.setTextSize((int) ((3 * s) / 2));
                //text.setTypeface(null, FontStyle.fontFamily);
                text.setTypeface(null, Typeface.BOLD);

                if (u == UnderlinePatterns.NONE) {
                    text.setText(content);
                } else {
                    //text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    text.setText(c);
                    text.setGravity(Gravity.CENTER);
                }
//                if(!c.equals(myText.getText().toString()) || !content.equals(myText.getText().toString())) {
                mainUI.addView(text);
//                }
            } else {
                text.setTextColor(Color.BLACK);
                text.setTextSize((int) ((3 * s) / 2));

                if (u == UnderlinePatterns.NONE) {
                    text.setText(content);
                } else {
                    //               text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    text.setText(c);
                    text.setGravity(Gravity.CENTER);
                }
//                if(!c.equals(myText.getText().toString()) || !content.equals(myText.getText().toString())) {
                mainUI.addView(text);
//                }
            }


            // Adds the view to the layout
            LinearLayout textLayout = new LinearLayout(getApplicationContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, text.getId());
            textLayout.setLayoutParams(params);
            mainUI.addView(textLayout);


        }
    }
    }





}



