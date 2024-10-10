package com.topico1.bancoedua.ui.profile;

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

import com.topico1.bancoedua.databinding.FragmentProfileBinding;
import com.topico1.bancoedua.login;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView textNombre, textEmail;
    private Button btnCerrarSesion;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Obtener referencias a los TextViews y botones
        textNombre = binding.textNombre;
        textEmail = binding.textEmail;
        btnCerrarSesion = binding.btnCerrarSesion;
        Button btnServicioCliente = binding.btnServicioCliente; // Botón para Servicio al Cliente

        // Configurar el botón para redirigir al ServicioActivity
        btnServicioCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irServicio(); // Llamar al método para abrir ServicioActivity
            }
        });

        // Llamar al método para cargar los datos del perfil
        cargarDatosPerfil();

        // Configurar el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        return root;
    }

    private void cargarDatosPerfil() {
        // Recuperar SharedPreferences
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("UserSession", MODE_PRIVATE);

        // Obtener los datos almacenados
        String firstName = sharedPrefs.getString("first_name", "Nombre no disponible");
        String lastName = sharedPrefs.getString("last_name", "");
        String nombreUsuario = firstName + " " + lastName; // Concatenar nombres

        String correoUsuario = sharedPrefs.getString("email", "Correo no disponible");

        // Mostrar los datos en los TextViews
        textNombre.setText("Nombre: " + nombreUsuario);
        textEmail.setText("Correo electrónico: " + correoUsuario);
    }

    public void irServicio() {
        Log.i("Info", "Botón de Servicio al Cliente presionado");
        Intent miIntent = new Intent(getActivity(), ServicioActivity.class);
        startActivity(miIntent); // Iniciar ServicioActivity
    }

    private void cerrarSesion() {
        // Borrar los datos de sesión de SharedPreferences
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear(); // Eliminar todos los datos almacenados
        editor.apply();

        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(getActivity(), login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar el stack de actividades
        startActivity(intent);

        // Finalizar la actividad actual
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpiar la referencia de binding
    }
}
