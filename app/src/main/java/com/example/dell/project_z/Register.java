package com.example.dell.project_z;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText edit_email;
    EditText edit_pass;
    Spinner edit_spinner;
    Button btn_submit;
    AlertDialog.Builder builder;
    String email,password,type;
    String inserturl= "http://192.168.12.5:8081/myApp/public/api/register";
    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edit_email = (EditText) findViewById(R.id.id_email);
        edit_pass = (EditText) findViewById(R.id.id_password);
        edit_spinner = (Spinner) findViewById(R.id.id_spinner);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        builder = new AlertDialog.Builder(Register.this);
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("students");
        typeList.add("teachers");
        typeList.add("parents");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, typeList);
        edit_spinner.setAdapter(adapter);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=edit_email.getText().toString();
                password=edit_pass.getText().toString();
                type=edit_spinner.getSelectedItem().toString();

                if(email.equals("")||password.equals(""))
                {
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Please Fill all" );
                    displayAlert("input_error");

                }
                else{
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, inserturl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d(TAG, "onResponse: ");
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                int user_id=jsonObject.getInt("user_id");
                                String status_code=jsonObject.getString("status_code");
                                String message=jsonObject.getString("message");
                                builder.setTitle("Server Responding");
                                builder.setMessage(message);
                                displayAlert(status_code);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d(TAG, "onErrorResponse: ");
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params= new HashMap<String, String>();
                            params.put("email", email);
                            params.put("password", password);
                            params.put("type",type);
                            return params;
                        }
                    };

                    MySingleton.getmInstance(Register.this).addToRequestQue(stringRequest);
                }
            }
        });

    }

    public void displayAlert(final String status_code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( status_code.equals("input_error"))
                {
                    edit_email.setText("");
                }
                else  if (status_code.equals("200")){
                    finish();
                }
                else if (status_code.equals("304"));
                {
                    edit_email.setText("");
                    edit_pass.setText("");
                }}
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }


}
