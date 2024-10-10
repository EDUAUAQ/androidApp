package com.topico1.bancoedua.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.topico1.bancoedua.R;
import com.topico1.bancoedua.databinding.FragmentHomeBinding;
import com.topico1.bancoedua.ui.home.TransferenciaActivity;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnTransferencia = binding.btnTransferencia;

        btnTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irTransferencia();
            }
        });

        // Recuperar el nombre del usuario de SharedPreferences
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("UserSession", MODE_PRIVATE);
        String firstName = sharedPrefs.getString("first_name", "Usuario");
        String lastName = sharedPrefs.getString("last_name", "");
        String nombreUsuario = firstName + " " + lastName; // Concatenar nombres

        // Configurar el mensaje en el TextView de saludo
        TextView textViewSaludo = binding.textSaludo; // Usa el ID correcto
        textViewSaludo.setText("Hola, " + nombreUsuario);

        return root;
    }

    public void irTransferencia() {
        Log.i("Info", "Botón de Servicio al Cliente presionado");
        Intent miIntent = new Intent(getActivity(), TransferenciaActivity.class);
        startActivity(miIntent); // Iniciar ServicioActivity
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
