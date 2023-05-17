package com.example.kpcateringservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends AppCompatActivity {
    Button Continue;
    AlertHandling alert;
    EditText numberOfPlates,contents;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AlertHandling a=new AlertHandling(HomeActivity.this);
        a.exitAppAlertDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Continue=findViewById(R.id.Continue);
        numberOfPlates=findViewById(R.id.numberOfPlates);
        contents=findViewById(R.id.Content);

        alert=new AlertHandling(HomeActivity.this);

        Intent intent=getIntent();
        String userId=intent.getStringExtra("UserId");
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numberOfPlates.getText().toString().isEmpty()){
                    alert.numberOfPlateRequired();
                    return;
                }
                if(contents.getText().toString().isEmpty()){
                    alert.plateContentRequired();
                    return;
                }
                Intent i=new Intent(getApplicationContext(),Order.class);
                i.putExtra("UserId",userId);
                i.putExtra("number of plate",numberOfPlates.getText().toString());
                i.putExtra("contents of plate",contents.getText().toString());
                startActivity(i);
            }
        });
    }
}