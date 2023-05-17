package com.example.kpcateringservices;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;

import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MainActivity mainActivity;
    TextView changeFragment;
    Button signin,google;
    ProgressBar loginProgressBar;
    GoogleSignInClient gsc;
    AlertHandling alert;
    EditText email,password;

    FirebaseAuth login_authentication;

    DatabaseReference database;



    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view=inflater.inflate(R.layout.fragment_login, container, false);
        mainActivity=(MainActivity)getActivity();
        changeFragment=(TextView)view.findViewById(R.id.changeFragment);
        signin=(Button)view.findViewById(R.id.signin);
        loginProgressBar=view.findViewById(R.id.progressBar);
        email=(EditText)view.findViewById(R.id.email);
        password=(EditText)view.findViewById(R.id.password);
        google=(Button)view.findViewById(R.id.google);
        login_authentication=FirebaseAuth.getInstance();
        alert=new AlertHandling(getContext());

        changeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeFragment("Register");
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgressBar.setVisibility(View.VISIBLE);
                login_validation(email.getText().toString(),password.getText().toString());

            }
        });

        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()

                .build();
        gsc= GoogleSignIn.getClient(getActivity().getApplicationContext(),gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLoginValidation();
            }
        });

        return view ;

    }

    private void googleLoginValidation() {
        Intent intent=gsc.getSignInIntent();
        startActivityForResult(intent,1001);

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1001)
        {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account=task.getResult(ApiException.class);
                account.getIdToken();
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch(ApiException e){
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();;
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        login_authentication.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                       {
                                           @Override
                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                               if(task.isSuccessful())
                                               {
                                                   FirebaseUser users =login_authentication.getCurrentUser();
                                                   GoogleSignInAccount accountinfo =GoogleSignIn.getLastSignedInAccount(getContext());
                                                   String userId = accountinfo.getId().toString();
                                                   String userEId = accountinfo.getEmail().toString();
                                                   String[] name=userEId.split("@");
                                                   mainActivity.user=new UserInfo(name[0],userEId,userId);
                                                   try{
                                                       database= FirebaseDatabase.getInstance().getReference();
                                                       if(database.child("Users").child(userId).child("UserInfo").setValue(mainActivity.user).isSuccessful())
                                                       {
//                                                           Toast.makeText(getContext(),"Account create sucessfully.",Toast.LENGTH_SHORT).show();

                                                       }else{
//                                                          Toast.makeText(getContext(),"Failed to create Account.",Toast.LENGTH_SHORT).show();
                                                       }
                                                   }
                                                   catch(Exception e){
                                                      Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                                   }
                                                   Intent i=new Intent(getContext(), HomeActivity.class);

                                                   i.putExtra("UserId", userId);
//
                                                   i.putExtra("UserEId", userEId);
                                                   startActivity(i);

                                                   onDestroy();

                                               }
                                               else {
                                                   Toast.makeText(getContext(),"Failed to Login",Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }
                );
    }
    private void login_validation(String email, String password) {
        if(email.isEmpty())
        {
            alert.emailRequiredDialog();
            loginProgressBar.setVisibility(View.GONE);
            return;
        }

        if(password.isEmpty())
        {
            alert.passwordRequiredDialog();
            loginProgressBar.setVisibility(View.GONE);
            return;
        }
        login_authentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(getActivity().getApplicationContext(), "Login Succesful.", Toast.LENGTH_SHORT).show();
                            loginProgressBar.setVisibility(View.GONE);
                            Intent i=new Intent(getContext(), HomeActivity.class);

                            i.putExtra("UserId", task.getResult().getUser().getUid().toString());
                            i.putExtra("UserEId", email);
                            startActivity(i);
                            onDestroy();

                        } else {
                            // If sign in fails, display a message to the user.
                            alert.loginFailedDialog();
                            loginProgressBar.setVisibility(View.GONE);
                            // Toast.makeText(getActivity().getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            return ;

                        }
                    }

                });
    }
}