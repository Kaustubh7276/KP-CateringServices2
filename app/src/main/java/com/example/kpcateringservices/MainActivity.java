package com.example.kpcateringservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    UserInfo user;
    AlertHandling alert;

    @Override
    public void onBackPressed() {
        alert=new AlertHandling(MainActivity.this);
        alert.exitAppAlertDialog();
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        LoginFragment login=new LoginFragment();

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.LoginRegister,login);

        fragmentTransaction.commit();
    }

    public void changeFragment(String id)
    {
        if(id=="Register") {
            RegisterFragment register = new RegisterFragment();
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.LoginRegister, register);
            fragmentTransaction1.commit();
        }
        else if(id=="Login"){
            LoginFragment login =new LoginFragment();
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.LoginRegister, login);
            fragmentTransaction1.commit();
        }

    }
}
