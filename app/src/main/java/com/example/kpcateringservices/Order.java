package com.example.kpcateringservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Order extends AppCompatActivity {

    EditText name,date,email,phone,address;
    Button  placeOrder;

    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Intent i=getIntent();
        String userId=i.getStringExtra("UserId");
        String plates=i.getStringExtra("number of plate");
        String Contents=i.getStringExtra("contents of plate");
        name=findViewById(R.id.name);
        phone=findViewById(R.id.Phone);
        email=findViewById(R.id.email);
        date=findViewById(R.id.date);
        address=findViewById(R.id.address);
        placeOrder=(Button)findViewById(R.id.placeorder);
        database= FirebaseDatabase.getInstance().getReference();


        AlertHandling alert=new AlertHandling(Order.this);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(name.getText().toString().isEmpty()) {
                   alert.UserNameRequiredDialog();
                   return;
               }
               if(phone.getText().toString().length()!=10){
                   alert.invalidPhoneNumber();
                   return;
               }
               if(email.getText().toString().isEmpty()){
                   alert.emailRequiredDialog();

                   return;
               }
               if(date.getText().toString().isEmpty()){
                   Toast.makeText(getApplicationContext(),"Enter Delivery date.",Toast.LENGTH_LONG).show();
                   return;
               }
               if(address.getText().toString().isEmpty())
               {
                   Toast.makeText(getApplicationContext(),"Enter Delivery Address.",Toast.LENGTH_LONG).show();
                   return;
               }
               String customerMessage="Dear customer, Your Order has been placed Succesfully.\n If you have any Query Contact US : 7276377562"+"";

               String ownerMessage="Customer name : "+name.getText().toString()+"\nMobile Number : "+phone.getText().toString()+
                       "\nEmail Address : "+email.getText().toString()+"\nDelivery Date : "+date.getText().toString()+
                       "\nDelivery Address:"+address.getText().toString()+"\nOrder Details: \n\tNumber of Plates : "+plates+"Plate Contents : "+Contents;

               orderDetailModel order=new orderDetailModel(name.getText().toString(),email.getText().toString(),date.getText().toString(),address.getText().toString(),phone.getText().toString(),plates,Contents);


               try {
                    SmsManager smsManager1 = SmsManager.getDefault();
                    smsManager1.sendTextMessage(phone.getText().toString(), null, customerMessage, null, null);

                    database.child("Users").child(userId).child("OrderDetail").setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            alert.details();
                        }
                    });

                   SmsManager smsManager2 = SmsManager.getDefault();
                   smsManager2.sendTextMessage("7276377562", null, userId, null, null);
                   alert.details();



               }
                catch(Exception e)
                {
                    Log.d("Error............... :",e.toString());
                }
            }
        });
    }
}