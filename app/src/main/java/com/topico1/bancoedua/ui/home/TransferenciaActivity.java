package com.topico1.bancoedua.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.topico1.bancoedua.R;

import com.topico1.bancoedua.ui.profile.ProfileFragment;

public class TransferenciaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transferencia);

        // Asegúrate de que la vista a la que intentas acceder no sea null
        View mainView = findViewById(R.id.main); // Asegúrate de que 'main' esté definido en tu XML

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.e("TransferenciaActivity", "La vista 'main' es null. Verifica el layout.");
        }
    }

    public void irPerfil(View view) {
        Log.i("Info", "Botón presionado");
        ProfileFragment profileFragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, profileFragment) // 'container' debe ser el ID de tu contenedor de fragments
                .addToBackStack(null) // Opcional, para que puedas volver
                .commit();
    }

}