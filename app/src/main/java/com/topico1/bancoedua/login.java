package com.topico1.bancoedua;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    public void irRegister(View view){
        Log.i("Info","Boton presionado");
        Intent miIntent = new Intent(this, register.class);
        startActivity(miIntent);
    }

    private Button btnIngresar;
    private EditText textUsername, textPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIngresar = findViewById(R.id.btnIngresar);
        textUsername = findViewById(R.id.textUsername);
        textPassword = findViewById(R.id.textPassword);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textUsername.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Ingrese el nombre de usuario",Toast.LENGTH_LONG).show();
                else if (textPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Ingrese la contraseña",Toast.LENGTH_LONG).show();
                }
                else
                    login(textUsername.getText().toString(),textPassword.getText().toString());
            }
        });
    }
    private void login(String username, String password) {
        String url = "http://192.168.1.70:3000/user/login";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int code = jsonResponse.getInt("code");
                            String message = jsonResponse.getString("message");

                            if (code == 200) {
                                Intent intent = new Intent(login.this, index.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error al iniciar sesión", Toast.LENGTH_LONG).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("user_name", username);
                    jsonBody.put("user_password", password);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }


}