package com.example.kpcateringservices;

import static android.view.View.*;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.ProgressBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button login,register;
    MainActivity mainActivity;
    FirebaseAuth register_authentication;
    EditText userName,email,password,confirmpassword;
    ProgressBar progressBar;
    AlertHandling alert;
    DatabaseReference database;
    String userId;


    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view=inflater.inflate(R.layout.fragment_register, container, false);

         mainActivity=(MainActivity)getActivity();
      login=(Button) view.findViewById(R.id.LOGIN);
      register=(Button) view.findViewById(R.id.REGISTER);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        userName=(EditText)view.findViewById(R.id.UserName);
        email=(EditText)view.findViewById(R.id.EmailID);
        password=(EditText)view.findViewById(R.id.password);
        confirmpassword=(EditText)view.findViewById(R.id.passconfirm);
        register_authentication=FirebaseAuth.getInstance();
        alert=new AlertHandling(getContext());

      login.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
             mainActivity.changeFragment("Login");
          }
      });

      register.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
              progressBar.setVisibility(VISIBLE);
              registerValidity(userName.getText().toString(),email.getText().toString(),password.getText().toString(),confirmpassword.getText().toString());

          }
      });


        return view;
    }

    private void registerValidity(String userName,String email,String password,String confirmpassword) {

        if (email.isEmpty()) {
            alert.emailRequiredDialog();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (userName.isEmpty()) {
            alert.UserNameRequiredDialog();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (password.isEmpty() || confirmpassword.isEmpty()) {
            alert.passwordRequiredDialog();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(getContext(), "Password Must be 8 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!(password.equals(confirmpassword))) {
            alert.passwordConfirmationFailedDialog();
            progressBar.setVisibility(View.GONE);
            return;
        }
        register_authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userId = task.getResult().getUser().getUid().toString();
                            String userEmail = task.getResult().getUser().getEmail().toString();
                            mainActivity.user = new UserInfo(userName, userEmail, userId);
                            Toast.makeText(getActivity().getApplicationContext(), "Registered Succesfully.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                            try {
                                database = FirebaseDatabase.getInstance().getReference();
                                database.child("Users").child(userId).child("userInfo").setValue(mainActivity.user);

                            } catch (Exception e) {
                                Log.d("Error : ", e.toString());
                            }
                            mainActivity.changeFragment("LoginFragment");

                        } else {
                            // If sign in fails, display a message to the user.
                            alert.registrationFailedDialog();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    }