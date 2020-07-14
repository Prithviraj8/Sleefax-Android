package com.Anubis.Sleefax;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.stream.IntStream;

class fileInfo {

    String Name;
    String Price;
    String Size;


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

    public fileInfo(String name, String price, String size) {
        Name = name;
        Price = price;
        Size = size;
    }
}
public class ReviewOrderActivity extends AppCompatActivity {

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> fileTypes = new ArrayList<>();
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
    ArrayList<Integer> customPage1 = new ArrayList<>();
    ArrayList<Integer> customPage2 = new ArrayList<>();

    double pricePerFile[];
    double totalPrice = 0.0;


    boolean newUser,isTester,addingMoreFiles, selectingFile;
    int shopCnt;


    ListView pdfView,billingView;
    Button selectShopBtn,addMoreFilesBtn;
    ImageButton backBtn;
    TextView priceTotal;

    pdfViewAdapter pdfViewAdapter;
    billingViewAdapter billingViewAdapter;
    ArrayList<fileInfo> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);

        selectShopBtn = findViewById(R.id.SelectShops);
        backBtn = findViewById(R.id.back_button);
        addMoreFilesBtn = findViewById(R.id.addMoreFilesBtn);
        priceTotal = findViewById(R.id.price_total);

        getOrderInfo();

        setFileListView();

        Log.d("FILESIZE",String.valueOf(fileSizes.size()));
        Log.d("FILENAME",String.valueOf(fileNames.size()));
        Log.d("PRICEPERFILE",String.valueOf(pricePerFile.length));




        // Attaching btn listeners.
        selectShopBtn.setOnClickListener(Listener);
        backBtn.setOnClickListener(Listener);
        addMoreFilesBtn.setOnClickListener(Listener);



    }


    public void setFileListView(){
        arrayList.clear();
        for(int i = 0;i< fileNames.size();i++) {
//            arrayList.add(new fileInfo(fileNames.get(i),pricePerFile.get(i),fileSizes.get(i)));
            arrayList.add(new fileInfo(fileNames.get(i),"₹ "+String.valueOf(pricePerFile[i]),fileSizes.get(i)));
//            totalPrice = pricePerFile[i] + totalPrice;

            if(i == fileNames.size()-1){
                Log.d("ARRAYSIZE",String.valueOf(arrayList.size()));


                pdfViewAdapter = new pdfViewAdapter(arrayList);
                billingViewAdapter = new billingViewAdapter(arrayList);


                pdfView = findViewById(R.id.pdfListView);
                pdfView.setAdapter(pdfViewAdapter);
                setDynamicHeight(pdfView);

                billingView = findViewById(R.id.billing_pdf_listview);
                billingView.setAdapter(billingViewAdapter);
                setDynamicHeight(billingView);

                calculateTotalPrice(pricePerFile);

            }
        }
    }


    private View.OnClickListener Listener = new View.OnClickListener() {

        public void onClick(View v) {

            if(v == findViewById(R.id.SelectShops)){
                addingMoreFiles = false;
                sendOrderInfo(false,0);
            }
            else if(v == findViewById(R.id.back_button)){
                finish();
            }
            else if(v == findViewById(R.id.addMoreFilesBtn)){
                addingMoreFiles = true;

                if(fileTypes.get(0).contains("IMAGE")){
                    selectingFile = false;
                }else {
                    selectingFile = true;
                }
                sendOrderInfo(false,0);
            }
        }
    };

    public void selectImages(boolean isImage){
        if(isImage) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);
        }else{
            Intent intent = new Intent();
            intent.setType("application/*");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select files"), 1);
        }

    }

//        if(addingMoreFiles){
//            ArrayList<File_Settings> data = new ArrayList<>();
//            Log.d("GETTING DATA ",String.valueOf(data));
//        }


    public void getOrderInfo(){

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        addingMoreFiles = extras.getBoolean("AddingMoreFiles");

        urls = extras.getStringArrayList("URLS");
        copies = extras.getIntegerArrayList("Copies");
        colors = extras.getStringArrayList("ColorType");
        fileTypes = extras.getStringArrayList("FileType");
        shopCnt = extras.getInt("ShopCount");
        pageSize = extras.getStringArrayList("PageSize");
        orientations = extras.getStringArrayList("Orientation");
        bothSides = extras.getBooleanArray("BothSides");
        customPages = extras.getStringArrayList("Custom");
        numberOfPages = extras.getIntegerArrayList("Pages");
        newUser = extras.getBoolean("NewUser");
        customValues = extras.getStringArrayList("CustomValue");
        fileNames = extras.getStringArrayList("FileNames");
        fileSizes = extras.getStringArrayList("FileSizes");
        isTester = extras.getBoolean("IsTester");
        customPage1 = extras.getIntegerArrayList("CustomPages1");
        customPage2 = extras.getIntegerArrayList("CustomPages2");

        pricePerFile = new double[urls.size()];
        pricePerFile = extras.getDoubleArray("PricePerFile");


    }

    public void sendOrderInfo(boolean customize,int pdfCnt){
        Intent intent = null;
        Bundle extras = new Bundle();

//        Toast.makeText(this, "ADDINGMORE "+addingMoreFiles, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "CUSTOMIZE "+customize, Toast.LENGTH_SHORT).show();
        if(addingMoreFiles && !customize) {
            intent = new Intent(ReviewOrderActivity.this, Pop.class);
            extras.putBoolean("File",false);
            extras.putBoolean("AddingMoreFiles",true);
            extras.putInt("FileCount",urls.size());
            extras.putBoolean("File",selectingFile);
        }else if(!customize){
            intent = new Intent(ReviewOrderActivity.this, ShopsActivity.class);
        }else
            if(customize){
                intent = new Intent(ReviewOrderActivity.this, PdfInfo.class);
                extras.putBoolean("Customize",true);
                extras.putInt("FileCount",urls.size());
                extras.putInt("PdfCnt",pdfCnt);
             }

        extras.putInt("ShopCount", shopCnt);
        extras.putStringArrayList("URLS", urls);
        extras.putIntegerArrayList("Pages", numberOfPages);
        extras.putBooleanArray("BothSides", bothSides);
        extras.putIntegerArrayList("Copies", copies);
        extras.putStringArrayList("ColorType", colors);
        extras.putStringArrayList("FileType", fileTypes);
        extras.putStringArrayList("PageSize", pageSize);
        extras.putStringArrayList("Orientation", orientations);
        extras.putStringArrayList("FileNames",fileNames);
        extras.putStringArrayList("FileSizes",fileSizes);
        extras.putBoolean("NewUser",newUser);
        extras.putStringArrayList("Custom",customPages);
        extras.putStringArrayList("CustomValue",customValues);

        extras.putIntegerArrayList("CustomPages1",customPage1);
        extras.putIntegerArrayList("CustomPages2",customPage2);

        extras.putDoubleArray("PricePerFile",pricePerFile);
        extras.putDouble("TotalPrice",totalPrice);

        extras.putBoolean("IsTester",isTester);

        intent.putExtras(extras);
        startActivity(intent);
    }

    public void removeFileInfoFromOrder(int index){
        urls.remove(index);
        if(!fileTypes.get(0).equals("IMAGE")) {
            numberOfPages.remove(index);
            customPages.remove(index);
            customPage1.remove(index);
            customPage2.remove(index);
        }
        copies.remove(index);
        colors.remove(index);
        fileNames.remove(index);
        fileTypes.remove(index);
        fileSizes.remove(index);
        pageSize.remove(index);
        orientations.remove(index);

        pricePerFile = removePriceOfDeletedFile(pricePerFile,index);

        calculateTotalPrice(pricePerFile);
        setFileListView();
    }

    // Function to remove the element
    public double[] removePriceOfDeletedFile(double[] arr,
                                         int index)
    {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null
                || index < 0
                || index >= arr.length) {

            return arr;
        }



        // return the resultant array
        return IntStream.range(0, arr.length)
                .filter(i -> i != index)
                .mapToDouble(i -> arr[i])
                .toArray();

    }


    public void calculateTotalPrice(double[] pricePerFile){
        totalPrice = 0.0;
        for(int i = 0;i < pricePerFile.length;i++){
            totalPrice = pricePerFile[i] + totalPrice;
            Log.d("FILE_PER_PRICE ",String.valueOf(pricePerFile[(i)]));


            if(i == pricePerFile.length - 1){
                Log.d("TOTAL_PRICE",String.valueOf(totalPrice));
                //setting total Price
                priceTotal.setText(String.valueOf(totalPrice));
            }
        }
    }


    public class pdfViewAdapter extends BaseAdapter {

        ArrayList<fileInfo> Data;

        public pdfViewAdapter(ArrayList<fileInfo> data) {
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

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.file_row,null);

            if(convertView != null) {
                TextView Name, Size, Price;
                Button removeFileBtn;

                Name = convertView.findViewById(R.id.pdfName);
                Size = convertView.findViewById(R.id.size);
                Price = convertView.findViewById(R.id.price);
                Button customize = convertView.findViewById(R.id.customise);
                removeFileBtn = convertView.findViewById(R.id.RemoveFileBtn);

                Name.setText(Data.get(position).getName());
                Price.setText(Data.get(position).getPrice());

                removeFileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(arrayList.size()>1) {
                            removeFileInfoFromOrder(position);
                        }else {
                            alertMessage();
                        }
                    }
                });
                customize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Send user to file or image setting page based on the users selection.
//                        Intent intent = new Intent(ReviewOrderActivity.this,PdfInfo.class);
//                        Bundle extras = new Bundle();
//                        extras.putInt("PdfCnt",position);

                        sendOrderInfo(true, position);

                    }
                });

                Name.setText(Data.get(position).getName());
                Size.setText(Data.get(position).getSize());
                Price.setText(Data.get(position).getPrice());

            }
            return convertView;
        }
    }

    public class billingViewAdapter extends BaseAdapter{

        ArrayList<fileInfo> Data;

        public billingViewAdapter(ArrayList<fileInfo> data) {
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

            convertView =  getLayoutInflater().inflate(R.layout.book_and_price,null);
            TextView Name,Price;
            Name = convertView.findViewById(R.id.pdfName);
            Price = convertView.findViewById(R.id.price);

            Name.setText(Data.get(position).getName());
            Price.setText(Data.get(position).getPrice());



            return convertView;
        }
    }


    public void setDynamicHeight(ListView listView) {
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

    protected void alertMessage() {

        Log.d("ONLY_1_FILE","OH OH");
        new AlertDialog.Builder(ReviewOrderActivity.this)
        .setMessage("This is the only file left in your cart.\n Are you sure you want to remove it ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(ReviewOrderActivity.this,Select.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
                }).create().show();

    }
}
