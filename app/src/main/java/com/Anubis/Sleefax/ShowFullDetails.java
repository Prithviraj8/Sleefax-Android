package com.Anubis.Sleefax;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ShowFullDetails extends AppCompatActivity {
    public class pdfInfo {

        String Name;
        String Price;
        String Size;

        public pdfInfo() {
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getPrice() {
            return Price;
        }

        public void setPrice(String price) {
            Price = price;
        }

        public String getSize() {
            return Size;
        }

        public void setSize(String size) {
            Size = size;
        }

        public pdfInfo(String name, String price, String size) {
            Name = name;
            Price = price;
            Size = size;
        }
    }



    TextView orderIDTV,filesTV,priceTotalTV,paymentModeTV,OrderPrice;
    ListView billingView;
    ListView orderView;
    LinearLayout mainUI;
    PDFView pdfView;
    ScrollView ViewerScollView;
    Button dismissViewer;

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();
    ArrayList<Integer> copies = new ArrayList<>();
    ArrayList<String> pageSize = new ArrayList<>();
    ArrayList<String> orientations = new ArrayList<>();
    boolean bothSides[];
    ArrayList<String> customPages = new ArrayList<>();
    ArrayList<Integer> numberOfPages = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileSizes = new ArrayList<>();
    ArrayList<String> fileLocations = new ArrayList<>();

    double pricePerFile[];

    String orderID;
    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email,paymentMode;

    int files;
    double price;
    long usernum,shopNum;

    PdfInfo pdfInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_full_details);

        connectViews();
        pdfInfo = new PdfInfo();


//        arrayList1.add(new OrderInfo("Book.pdf","","","","",""));
//        arrayList1.add(new OrderInfo("Science.pdf","","","","",""));
//        arrayList1.add(new OrderInfo("BookExe.docx","","","","",""));
//


    }


    public void connectViews(){


        // Connecting Views
        orderIDTV = findViewById(R.id.OrderId);
        filesTV = findViewById(R.id.Files);
        priceTotalTV = findViewById(R.id.price_total);
        paymentModeTV = findViewById(R.id.paymentMode);
        mainUI = findViewById(R.id.ViewerLinearLayout);
        pdfView = findViewById(R.id.viewPDF);
        ViewerScollView = findViewById(R.id.ViewerScollView);
        dismissViewer = findViewById(R.id.DismissViewer);
        OrderPrice = findViewById(R.id.OrderPrice);

        dismissViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getData();

    }


    public void getData(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //////////////////////////////////////////////////Shop Info//////////////////////////////////////////
//        shopLat = extras.getDouble("ShopLat");
//        shopLong = extras.getDouble("ShopLong");
        name = extras.getString("ShopName");
        loc = extras.getString("Location");
        orderID = extras.getString("OrderID");
        files = extras.getInt("Files");
        orderStatus = extras.getString("OrderStatus");
//        userLat = extras.getDouble("User Lat");
//        userLong = extras.getDouble("User Long");
//        FromYourOrders = extras.getBoolean("FromYourOrders");

        /////////////////////////////////////////////////Order info////////////////////////////////////////

        fileTypes = extras.getStringArrayList("FileType");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        bothSides = extras.getBooleanArray("BothSides");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");
//        username = extras.getString("Username");
//        email = extras.getString("email");
//        usernum = extras.getLong("UserNumber");
//        shopNum = extras.getLong("ShopNum");
        paymentMode = extras.getString("PaymentMode");
//        isTester = extras.getBoolean("IsTester");


        pricePerFile = new double[files];
        pricePerFile = extras.getDoubleArray("PricePerFile");
        fileLocations = extras.getStringArrayList("FileLocations");
        price = extras.getDouble("TotalPrice");
        inititalizeInitialData();

    }

    public void inititalizeInitialData(){
        orderIDTV.setText("Order #"+orderID);
        filesTV.setText(files+" Files");
        paymentModeTV.setText(paymentMode);
        OrderPrice.setText(String.valueOf(price));

        ArrayList<pdfInfo> arrayList = new ArrayList<>();


        for(int i =0;i< fileNames.size();i++){
            arrayList.add(new pdfInfo(fileNames.get(i),String.valueOf(pricePerFile[i]),fileSizes.get(i)));

            if( i == fileNames.size() - 1){
                priceTotalTV.setText(String.valueOf(price));
                billingViewAdapter billingViewAdapter = new billingViewAdapter(arrayList);
                billingView = findViewById(R.id.billing_pdf_listview);
                billingView.setAdapter(billingViewAdapter);
                setDynamicHeight(billingView);
            }
        }


        orderView = findViewById(R.id.OrdersLV);
        ArrayList<OrderInfo> arrayList1 = new ArrayList<>();
        for(int i =0;i< fileNames.size();i++){
            Log.d("COPIES",colors.get(i));
            arrayList1.add(new OrderInfo(fileNames.get(i),"Page Size : "+String.valueOf(pageSize.get(i)),"Orientation: "+String.valueOf(orientations.get(i)),"File Type: "+String.valueOf(fileTypes.get(i)),"Colour type: "+String.valueOf(colors.get(i)),"Copies : "+String.valueOf(copies.get(i))));

            if(i == fileNames.size() - 1){
                OrderViewAdapter orderViewAdapter = new OrderViewAdapter(arrayList1);
                orderView.setAdapter(orderViewAdapter);
                setDynamicHeight(orderView);
            }
        }


    }


    private class billingViewAdapter extends BaseAdapter {

        ArrayList<pdfInfo> Data;

        public billingViewAdapter(ArrayList<pdfInfo> data) {
            Data = data;
        }

        @Override
        public int getCount() {
            return Data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView =  getLayoutInflater().inflate(R.layout.book_size_price,null);
            TextView Name,Price,FileSize;
            Name = convertView.findViewById(R.id.pdfName);
            Price = convertView.findViewById(R.id.price);
            FileSize = convertView.findViewById(R.id.FileSize);

            Name.setText(Data.get(position).getName());
            Price.setText(Data.get(position).getPrice());
            FileSize.setText(Data.get(position).getSize());

            return convertView;
        }
    }

    private class OrderViewAdapter extends BaseAdapter {

        ArrayList<OrderInfo> Data;

        public OrderViewAdapter(ArrayList<OrderInfo> data) {
            Data = data;
        }

        @Override
        public int getCount() {
            return Data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView =  getLayoutInflater().inflate(R.layout.order_info,null);
            TextView FileName, colorType,copies,pageSize,orientation,fileType;
            FileName = convertView.findViewById(R.id.pdfName);
            colorType = convertView.findViewById(R.id.Color);
            copies = convertView.findViewById(R.id.pdfCopies);
            pageSize = convertView.findViewById(R.id.pageSize);
            orientation = convertView.findViewById(R.id.pageOrientation);
            fileType = convertView.findViewById(R.id.FileType);
            Button viewFile = convertView.findViewById(R.id.ViewFileBtn);

            FileName.setText(Data.get(position).getName());
            colorType.setText(Data.get(position).getColorType());
            copies.setText(Data.get(position).getCopies());
            orientation.setText(Data.get(position).getOrientation());
            pageSize.setText(Data.get(position).getPageSize());
            fileType.setText(Data.get(position).getFileType());



            viewFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("FILESELECT",fileLocations.get(position));
                    if (fileTypes.get(position).equals("PDF")) {
                        pdfView.setVisibility(View.VISIBLE);
                        pdfView.fromUri(Uri.parse(fileLocations.get(position)))
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

                    }else if(fileTypes.get(position).equals("Docx")){

                        mainUI.removeAllViewsInLayout();
                        Toast.makeText(ShowFullDetails.this, "OPENING DOCX ", Toast.LENGTH_SHORT).show();
                        alertBoxForViewer("Docx",position);


                        ////// Call to open file in our in app viewer
//                    new OpenDocFile(findViewById(R.id.ViewerLinearLayout),findViewById(R.id.ViewerScollView),Uri.parse(pdfURL.get(pdfCnt)),"DOCX");


                    }else if(fileTypes.get(position).equals("Word")){
                        mainUI.removeAllViewsInLayout();
                        Toast.makeText(ShowFullDetails.this, "OPENING WORD ", Toast.LENGTH_SHORT).show();
                        alertBoxForViewer("Word",position);

                        ////// Call to open file in our in app viewer
//                    new OpenDocFile(findViewById(R.id.ViewerLinearLayout),findViewById(R.id.ViewerScollView),Uri.parse(pdfURL.get(pdfCnt)),"WORD");

                    }
                    else if(fileTypes.get(position).equals("PPT") || fileTypes.get(position).equals("PPTX")){
                        Toast.makeText(ShowFullDetails.this, "YUP "+fileTypes.get(position), Toast.LENGTH_SHORT).show();
                        ViewFileFromAnotherApp(fileLocations.get(position),fileTypes.get(position));
                    }else {
                        Toast.makeText(ShowFullDetails.this, "YUP "+fileTypes.get(position), Toast.LENGTH_SHORT).show();
                        ViewFileFromAnotherApp(fileLocations.get(position),fileTypes.get(position));
                    }
                }
            });

            return convertView;
        }
    }

    private void setDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();


    }

    protected void alertBoxForViewer(final String whatFile, final int pdfCnt) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Use In-App Viewer ? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        new OpenDocFile(findViewById(R.id.ViewerLinearLayout),findViewById(R.id.ViewerScollView), Uri.parse(fileLocations.get(pdfCnt)),whatFile,pdfCnt);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismissViewer.setVisibility(View.GONE);

                ViewFileFromAnotherApp(fileLocations.get(pdfCnt),fileTypes.get(pdfCnt));
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void ViewFileFromAnotherApp(String word, String mimeType) {

        if(mimeType.equals("Docx")){
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }else if(mimeType.equals("Word")){
            mimeType = "application/msword";
        }else if(mimeType.equals("PPT")){
            mimeType = "application/vnd.ms-powerpoint";
        }else if(mimeType.equals("PPTX")){
            mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }

        Log.d("OPEN_IN_ANOTHER_APP",word);

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

    public class OpenDocFile{



        /// Creating In-App Document and PowerPoint Viewer
        LinearLayout mainUI;
        List<XWPFPictureData> picList;
        ScrollView viewerScollView;
        Uri file;
        int pdfCnt;
        String whatFile;

        public OpenDocFile(View mainUI, View viewerScollView, Uri file, String whatFile, int pdfCnt) {


            this.mainUI = (LinearLayout) mainUI;
            this.viewerScollView = (ScrollView) viewerScollView;
            this.file = file;
            this.whatFile = whatFile;
            this.pdfCnt = pdfCnt;

            openViewer(file);

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


        public void openViewer(Uri file){


            try {
                //this is action performed after openDocumentFromFileManager() when doc is selected

                FileInputStream inputStream = (FileInputStream) getContentResolver().openInputStream(file);
                ShowFullDetails.OpenDocFile.setUpDocx docxObj = new ShowFullDetails.OpenDocFile.setUpDocx();
                ShowFullDetails.OpenDocFile.setUpWord wordObj = new ShowFullDetails.OpenDocFile.setUpWord();

                if(whatFile.equals("Docx")) {

                    Toast.makeText(ShowFullDetails.this,"SETUP DOCX",Toast.LENGTH_LONG).show();
                    XWPFDocument docx = new XWPFDocument(inputStream);

                    docxObj.traverseBodyElements(docx.getBodyElements());
                    picList = docx.getAllPackagePictures();
                }
                else if(whatFile.equals("Word")){
                    Toast.makeText(ShowFullDetails.this,"SETUP WORD",Toast.LENGTH_LONG).show();

                    HWPFDocument wordDoc = new HWPFDocument(inputStream);
                    WordExtractor extractor = new WordExtractor(wordDoc);


                    Range range = wordDoc.getRange();
                    String[] paragraphs = extractor.getParagraphText();

                    PicturesTable picturesTable = wordDoc.getPicturesTable();
                    List<Picture> all = picturesTable.getAllPictures();

                    for(int i =0;i < paragraphs.length;i++){
                        Paragraph pr = range.getParagraph(i);

//                            Log.i("text",pr.text());
                        int j =0 ;

                        while(true){
                            CharacterRun run = pr.getCharacterRun(j++);

                            StyleDescription style = wordDoc.getStyleSheet().getStyleDescription(run.getSubSuperScriptIndex());
                            String styleName = style.getName();
                            String font = run.getFontName();
                            int size = run.getFontSize();
                            String paraText = pr.text();
                            Boolean b = run.isBold();
                            int u = run.getUnderlineCode();

                            wordObj.addTextViews(paraText,size,b,u,font);

                            if(picturesTable.hasPicture(run)){
                                Picture p = picturesTable.extractPicture(run,true);
                                wordObj.traversePictures(p);
                            }

                            Log.i("name",styleName);
                            Log.i("font",Integer.toString(size));
                            Log.i("family",font);
                            Log.i("text",paraText);


//
//                                List<Picture> pictures = wordDoc.getPicturesTable().getAllPictures();
//                                traversePictures(pictures);



                            if (run.getEndOffset() == pr.getEndOffset()) {
                                break;
                            }
                        }
                    }
                }
                viewerScollView.setVisibility(View.VISIBLE);




            } catch (IOException e) {
                e.printStackTrace();
                ViewFileFromAnotherApp(file.toString(),fileTypes.get(pdfCnt));
            } catch (Exception e) {
                e.printStackTrace();
                ViewFileFromAnotherApp(file.toString(),fileTypes.get(pdfCnt));

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
                TextView text = new TextView(ShowFullDetails.this);
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
                            break;

                        }else{
                            setProperText(text, content, s, b, u, f);
                            break;
                        }
                    }else{
//                    Log.d("NTAGCNT ",String.valueOf(TagCnt));
//                    Log.d("NICNT ",String.valueOf(i));
//                    Log.d("NULLTXT ",content);
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
                ImageView image = new ImageView(ShowFullDetails.this);

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
                        traversePictures(run.getEmbeddedPictures());

                    } else if (runElement instanceof XWPFSDT) {
                        XWPFSDT sDT = (XWPFSDT)runElement;
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
                ImageView image = new ImageView(ShowFullDetails.this);

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


                TextView text = new TextView(ShowFullDetails.this);
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
