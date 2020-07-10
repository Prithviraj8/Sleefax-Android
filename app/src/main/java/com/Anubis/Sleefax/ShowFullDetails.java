package com.Anubis.Sleefax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

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



    TextView orderIDTV,filesTV,priceTotalTV,paymentModeTV;
    ListView billingView;
    ListView orderView;

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
    double pricePerFile[];

    String orderID;
    String name,loc,orderKey,orderStatus,shopKey,fileType,pagesize,orientation,username,email,paymentMode;

    int files;
    double price;
    long usernum,shopNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_full_details);

        connectViews();



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
        price = extras.getDouble("Price");
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
        price = extras.getDouble("TotalPrice");
        inititalizeInitialData();

    }

    public void inititalizeInitialData(){
        orderIDTV.setText("Order #"+orderID);
        filesTV.setText(files+" Files");
        paymentModeTV.setText(paymentMode);


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
            TextView Name,Price;
            Name = convertView.findViewById(R.id.pdfName);
            Price = convertView.findViewById(R.id.price);

            Name.setText(Data.get(position).getName());
            Price.setText(Data.get(position).getPrice());


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
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView =  getLayoutInflater().inflate(R.layout.order_info,null);
            TextView FileName, colorType,copies,pageSize,orientation,fileType;
            FileName = convertView.findViewById(R.id.pdfName);
            colorType = convertView.findViewById(R.id.Color);
            copies = convertView.findViewById(R.id.pdfCopies);
            pageSize = convertView.findViewById(R.id.pageSize);
            orientation = convertView.findViewById(R.id.pageOrientation);
            fileType = convertView.findViewById(R.id.FileType);


            FileName.setText(Data.get(position).getName());
            colorType.setText(Data.get(position).getColorType());
            copies.setText(Data.get(position).getCopies());
            orientation.setText(Data.get(position).getOrientation());
            pageSize.setText(Data.get(position).getPageSize());
            fileType.setText(Data.get(position).getFileType());



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

}
