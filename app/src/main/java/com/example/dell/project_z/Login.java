package com.example.dell.project_z;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    TextView textView;
    EditText Username,Password;
    String email,password;
    Button Login_btn,btn_register;
    String loginUrl="http://192.168.1.2:8081/myApp/public/api/login";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        builder=new AlertDialog.Builder(Login.this);

        Login_btn= (Button) findViewById(R.id.btn_register);
        Username= (EditText) findViewById(R.id.id_username);
        Password= (EditText) findViewById(R.id.id_password);
        btn_register= (Button) findViewById(R.id.Id_btn);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=Username.getText().toString();
                password=Password.getText().toString();

                if ( email.equals("")||password.equals("")){
                    builder.setTitle("Something Went wrong");
                    displayAlert("Enter the Valid ID and Password");
                }
                else{
                    StringRequest stringRequest= new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);

                                String status=jsonObject.getString("status");
                                String message=jsonObject.getString("message");
                                builder.setTitle("Server Responding");
                                builder.setMessage(message);
                                displayAlert(status);

                                if (status.equals("304")){
                                    builder.setTitle("Login Error");
                                    displayAlert(jsonObject.getString(message));
                                }
                                else {
                                    builder.setTitle("Login Done");
                                    displayAlert(jsonObject.getString(message));
                                     Intent intent=new Intent(Login.this,MainActivity.class);
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Login.this,"Error",Toast.LENGTH_LONG).show();
                            error.printStackTrace();

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params= new HashMap<String, String>();
                            params.put("email",email);
                            params.put("passsword",password);

                            return params;
                        }
                    };
                    MySingleton.getmInstance(Login.this).addToRequestQue(stringRequest);
                }
                btn_register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Login.this,Register.class);

                    }
                });
            }
        });


    }

    public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Username.setText("");
                Password.setText("");

            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}


