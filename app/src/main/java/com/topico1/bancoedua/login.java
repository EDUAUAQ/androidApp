package com.topico1.bancoedua;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class login extends AppCompatActivity {

    private Button btnIngresar;
    private EditText textMail, textPassword;

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
        textMail = findViewById(R.id.textMail);
        textPassword = findViewById(R.id.textPassword);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textMail.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Ingrese el correo",Toast.LENGTH_LONG).show();
                else if (textPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Ingrese la contraseña",Toast.LENGTH_LONG).show();
                }
                else
                    login(textMail.getText().toString(), textPassword.getText().toString());
            }
        });
    }

    private void login(String email, String password) {
        String url = "http://192.168.1.70:3000/user/login";

        Log.d("Login Request", "Email: " + email + ", Password: " + password);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Server Response", response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int code = jsonResponse.getInt("code");
                            String message = jsonResponse.getString("message");

                            if (code == 200) {
                                // Obtener el token de acceso de la respuesta
                                String accessToken = jsonResponse.getString("token");

                                // Obtener el user_id
                                String userId = jsonResponse.getString("user_id");

                                // Guardar el token de acceso en EncryptedSharedPreferences
                                saveToken(accessToken);

                                // Guardar el user_id en las preferencias
                                SharedPreferences sharedPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPrefs.edit();
                                editor.putString("user_id", userId);
                                editor.apply();

                                // Obtener información del perfil
                                getProfile(userId);

                                // Redirigir al index después de iniciar sesión correctamente
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
                    jsonBody.put("user_mail", email);
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

    // Método para guardar el token de acceso en EncryptedSharedPreferences
    private void saveToken(String accessToken) {
        try {
            // Generar o recuperar la clave maestra
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Crear las preferencias encriptadas
            SharedPreferences encryptedPrefs = EncryptedSharedPreferences.create(
                    "UserSession", // Nombre del archivo de preferencias
                    masterKeyAlias, // Clave maestra
                    this, // Contexto
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Esquema de encriptación de claves
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Esquema de encriptación de valores
            );

            // Guardar el token de acceso en las preferencias encriptadas
            SharedPreferences.Editor editor = encryptedPrefs.edit();
            editor.putString("access_token", accessToken);
            editor.apply();

            Log.d("EncryptedPrefs", "Token de acceso guardado de manera segura");

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Log.e("EncryptedPrefsError", "Error al guardar el token de acceso: " + e.getMessage());
        }
    }

    public void irRegister(View view){
        Log.i("Info","Boton presionado");
        Intent miIntent = new Intent(this, register.class);
        startActivity(miIntent);
    }

    private void getProfile(String userId) {
        String url = "http://192.168.1.70:3000/user/profile/" + userId;

        StringRequest profileRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getInt("code") == 200) {
                                JSONObject userData = jsonResponse.getJSONObject("data");
                                String firstName = userData.getString("first_name");
                                String lastName = userData.getString("last_name");
                                String email = userData.getString("email");

                                // Guardar nombre y correo en SharedPreferences
                                SharedPreferences sharedPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPrefs.edit();
                                editor.putString("first_name", firstName);
                                editor.putString("last_name", lastName);
                                editor.putString("email", email);
                                editor.apply();

                            } else {
                                Toast.makeText(getApplicationContext(), "Error al obtener perfil", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "Error al obtener perfil", Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(profileRequest);
    }
}