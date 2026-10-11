package com.ispc.florappmovil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class NavbarManager {
    public static void setupNavbar(Activity activity, String pestanaActual) {
        // 1. Referencias de los botones de la navbar
        LinearLayout btnFichas = activity.findViewById(R.id.btnNavFichas);
        LinearLayout btnContacto = activity.findViewById(R.id.btnNavContacto);
        LinearLayout btnNavIngreso = activity.findViewById(R.id.btnNavIngreso);
        LinearLayout btnGestion = activity.findViewById(R.id.layoutBtnGestion);

        ImageView imgFichas = activity.findViewById(R.id.imgFichas);
        TextView txtFichas = activity.findViewById(R.id.txtFichas);

        ImageView imgContacto = activity.findViewById(R.id.imgContacto);
        TextView txtContacto = activity.findViewById(R.id.txtContacto);

        ImageView imgPerfilLogin = activity.findViewById(R.id.imgPerfilLogin);
        TextView txtIngreso = activity.findViewById(R.id.txtIngreso);

        ImageView imgGestion = activity.findViewById(R.id.imgGestion);
        TextView txtGestion = activity.findViewById(R.id.txtGestion);

        // 2. Control de visibilidad según el rol (Admin/Docente vs Invitado)
        SessionManager sessionManager = new SessionManager(activity);
        boolean esAdminDocente = sessionManager.haySesion();

        if (esAdminDocente) {
            if (btnGestion != null) btnGestion.setVisibility(View.VISIBLE);
            if (txtIngreso != null) txtIngreso.setText("Mi Perfil");
        } else {
            if (btnGestion != null) btnGestion.setVisibility(View.GONE);
            if (txtIngreso != null) txtIngreso.setText("Ingreso");
        }

        // 3. Restaurar todos a estado inactivo (negro y sin fondo)
        restaurarInactivo(btnFichas, imgFichas, txtFichas);
        restaurarInactivo(btnContacto, imgContacto, txtContacto);
        restaurarInactivo(btnNavIngreso, imgPerfilLogin, txtIngreso);
        restaurarInactivo(btnGestion, imgGestion, txtGestion);

        // 4. Activar visualmente la pestaña actual y pintarla con su color correspondiente
        switch (pestanaActual) {
            case "fichas":
                if (btnFichas != null) {
                    btnFichas.setBackgroundResource(R.drawable.navbar_btn);
                    btnFichas.getBackground().setTint(androidx.core.content.ContextCompat.getColor(activity, R.color.violeta));                    if (imgFichas != null) imgFichas.setColorFilter(Color.WHITE);
                    if (txtFichas != null) txtFichas.setTextColor(Color.WHITE);
                }
                break;
            case "contacto":
                if (btnContacto != null) {
                    btnContacto.setBackgroundResource(R.drawable.navbar_btn);
                    btnContacto.getBackground().setTint(androidx.core.content.ContextCompat.getColor(activity, R.color.naranja));                    if (imgContacto != null) imgContacto.setColorFilter(Color.WHITE);
                    if (txtContacto != null) txtContacto.setTextColor(Color.WHITE);
                }
                break;
            case "perfil":
            case "ingreso":
                if (btnNavIngreso != null) {
                    btnNavIngreso.setBackgroundResource(R.drawable.navbar_btn);
                    btnNavIngreso.getBackground().setTint(androidx.core.content.ContextCompat.getColor(activity, R.color.celeste));                    if (imgPerfilLogin != null) imgPerfilLogin.setColorFilter(Color.WHITE);
                    if (txtIngreso != null) txtIngreso.setTextColor(Color.WHITE);
                }
                break;
            case "gestion":
                if (btnGestion != null) {
                    btnGestion.setBackgroundResource(R.drawable.navbar_btn);
                    btnGestion.getBackground().setTint(androidx.core.content.ContextCompat.getColor(activity, R.color.amarillo));                    if (imgGestion != null) imgGestion.setColorFilter(Color.WHITE);
                    if (txtGestion != null) txtGestion.setTextColor(Color.WHITE);
                }
                break;
        }

        // 5. Configurar los eventos de clic para navegar entre pantallas
        if (btnFichas != null) {
            btnFichas.setOnClickListener(v -> {
                if (!(activity instanceof GaleriaActivity)) {
                    activity.startActivity(new Intent(activity, GaleriaActivity.class));
                    activity.finish();
                }
            });
        }

        if (btnContacto != null) {
            btnContacto.setOnClickListener(v -> {
                if (!(activity instanceof ContactoActivity)) {
                    activity.startActivity(new Intent(activity, ContactoActivity.class));
                    activity.finish();
                }
            });
        }

        if (btnNavIngreso != null) {
            btnNavIngreso.setOnClickListener(v -> {
                if (esAdminDocente) {
                    if (!(activity instanceof ProfileActivity)) {
                        activity.startActivity(new Intent(activity, ProfileActivity.class));
                        activity.finish();
                    }
                } else {
                    if (!(activity instanceof IngresoActivity)) {
                        activity.startActivity(new Intent(activity, IngresoActivity.class));
                        activity.finish();
                    }
                }
            });
        }
    }

    private static void restaurarInactivo(LinearLayout layout, ImageView img, TextView txt) {
        if (layout != null) layout.setBackground(null);
        if (img != null) img.setColorFilter(Color.BLACK);
        if (txt != null) txt.setTextColor(Color.BLACK);
    }
}
