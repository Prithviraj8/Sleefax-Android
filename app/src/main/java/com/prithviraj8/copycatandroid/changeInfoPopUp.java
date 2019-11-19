package com.prithviraj8.copycatandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class changeInfoPopUp extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    TextView email,name,num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info_pop_up);

        getSupportActionBar().hide();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        email = findViewById(R.id.emailTV);
        name = findViewById(R.id.nameTV);
        num = findViewById(R.id.numTV);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.8),(int) (height* 0.6));

        Button dismiss = findViewById(R.id.changeInfoBtn);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String,Object > updateinfo = new HashMap<>();
                if(email.getText().toString().length()>0){
                    updateinfo.put("email",email.getText().toString());
                }
                if(name.getText().toString().length()>0){
                    updateinfo.put("name",name.getText().toString());
                }
                if(num.getText().toString().length()>0){
                    updateinfo.put("num",num.getText().toString());
                }
                Log.d("USERSDATACHANGING",userId);
                ref.child("users").child(userId).updateChildren(updateinfo);

                Context context = getApplicationContext();
                CharSequence text = "Details changed successfully. Please Log in once more.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(changeInfoPopUp.this, MainPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);
                finish();
            }
        });
    }
}
