package com.topico1.bancoedua;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    public void irLogin(View view){
        Log.i("Info","Boton presionado");
        Intent miIntent = new Intent(this, login.class);
        startActivity(miIntent);
    }

    private Button btnRegistrar;
    private EditText textUsername, textMail, textPassword, textFirstName, textLastName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegistrar = findViewById(R.id.btnRegistrar);
        textUsername = findViewById(R.id.textUsername);
        textMail = findViewById(R.id.textMail);
        textPassword = findViewById(R.id.textPassword);
        textFirstName = findViewById(R.id.textFirstName);
        textLastName = findViewById(R.id.textLastName);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textUsername.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Ingrese el nombre de usuario",Toast.LENGTH_LONG).show();
                else if (textMail.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Ingrese el correo",Toast.LENGTH_LONG).show();
                }else if (textPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Ingrese la contraseña",Toast.LENGTH_LONG).show();
                }else if (textFirstName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Ingrese su(s) nombre(s)",Toast.LENGTH_LONG).show();
                }else if (textLastName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Ingrese sus apellidos",Toast.LENGTH_LONG).show();
                }
                else
                    register(textUsername.getText().toString(),textMail.getText().toString(),textPassword.getText().toString(),textFirstName.getText().toString(),textLastName.getText().toString());
            }
        });
    }
    private void register(String username, String mail, String password, String firstName, String lastName) {
        String url = "http://192.168.3.11:3000/user/signup";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                        Log.d("Response", response);

                        Intent intent = new Intent(register.this, index.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error al registrar usuario", Toast.LENGTH_LONG).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("user_name", username);
                    jsonBody.put("user_mail", mail);
                    jsonBody.put("user_password", password);
                    jsonBody.put("first_name", firstName);
                    jsonBody.put("last_name", lastName);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (JSONException | UnsupportedEncodingException e) {
                    Log.e("JSONError", "Error en la creación del JSON", e);
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

}