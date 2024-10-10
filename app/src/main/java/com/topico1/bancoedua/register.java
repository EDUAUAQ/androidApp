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

public class register extends AppCompatActivity {

    private Button btnRegistrar;
    private EditText textUsername, textMail, textPassword, textFirstName, textLastName;

    // SharedPreferences Encriptadas
    private SharedPreferences encryptedPreferences;

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

        // Inicializar EncryptedSharedPreferences
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            encryptedPreferences = EncryptedSharedPreferences.create(
                    "UserSession",
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar que todos los campos estén llenos
                if (textUsername.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "Ingrese el nombre de usuario", Toast.LENGTH_LONG).show();
                else if (textMail.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese el correo", Toast.LENGTH_LONG).show();
                } else if (textPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese la contraseña", Toast.LENGTH_LONG).show();
                } else if (textFirstName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese su(s) nombre(s)", Toast.LENGTH_LONG).show();
                } else if (textLastName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese sus apellidos", Toast.LENGTH_LONG).show();
                } else {
                    // Si todo está correcto, llamar al método de registro
                    register(textUsername.getText().toString(), textMail.getText().toString(), textPassword.getText().toString(), textFirstName.getText().toString(), textLastName.getText().toString());
                }
            }
        });
    }

    // Método para registrar al usuario en el servidor
    private void register(String username, String mail, String password, String firstName, String lastName) {
        // URL del endpoint para el registro
        String url = "http://192.168.1.70:3000/user/signup";

        // Crear una solicitud POST
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Server Response", response);

                            // Procesar la respuesta JSON
                            JSONObject jsonResponse = new JSONObject(response);
                            int code = jsonResponse.getInt("code");

                            // Si el registro es exitoso
                            if (code == 201) {
                                Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();

                                // Obtener el user_id para consultar el perfil
                                String userId = jsonResponse.optString("user_id", null);
                                String firstName = jsonResponse.optString("first_name", null);
                                String lastName = jsonResponse.optString("last_name", null);

                                // Guardar el user_id y nombres en las preferencias encriptadas
                                SharedPreferences.Editor editor = encryptedPreferences.edit();
                                editor.putString("user_id", userId);
                                editor.putString("first_name", firstName);
                                editor.putString("last_name", lastName);
                                editor.apply();

                                // Redirigir al índice si todo es correcto
                                Intent intent = new Intent(register.this, index.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // Si ocurre un error en el registro
                                Toast.makeText(getApplicationContext(), "Error al registrar usuario", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON Error", "Error procesando respuesta JSON: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Error procesando la respuesta del servidor", Toast.LENGTH_LONG).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error al registrar usuario";
                        // Manejar errores de la solicitud
                        if (error.networkResponse != null) {
                            Log.e("Volley Error", "Código de respuesta: " + error.networkResponse.statusCode);
                            // Extraer mensaje de error detallado
                            if (error.networkResponse.data != null) {
                                errorMessage = new String(error.networkResponse.data);
                            }
                        }
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    // Crear el cuerpo de la solicitud JSON
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
                return "application/json; charset=utf-8"; // Tipo de contenido de la solicitud
            }
        };

        // Crear una cola de solicitudes y añadir la solicitud de registro
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }
}