package com.example.hotelreservaapp.cliente;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelreservaapp.cliente.TaxistaCliente.ClienteServicioTaxi;
import com.example.hotelreservaapp.Objetos.Notificaciones;
import com.example.hotelreservaapp.Objetos.NotificacionesStorageHelper;
import com.example.hotelreservaapp.Objetos.NotificationManagerNoAPP;
import com.example.hotelreservaapp.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HistorialEventos extends AppCompatActivity implements HistorialItemListener{

    BottomNavigationView bottomNav;
    String channelId = "ChannelRideAndRest"; // En cualquier otra Activity
    private boolean solicitarCheckout = false;
    private HistorialAdapter adapter;

    private String Tipo;
    private RecyclerView recyclerView;
    private List<HistorialItem> historialItems;
    private List<HistorialItem> historialItemsFiltrados;
    private String userId;
    private MaterialButton btnFiltroTodos;
    private MaterialButton btnFiltroPendientes;
    private MaterialButton btnFiltroEnProgreso;
    private MaterialButton btnFiltroTerminados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.cliente_activity_historial_eventos);

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, statusBars.top, 0, 0);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista vacía antes
        historialItems = new ArrayList<>();
        historialItemsFiltrados = new ArrayList<>(); // para mostrar en el adapter

        // Crear adapter con lista vacía
        adapter = new HistorialAdapter(this, historialItemsFiltrados, this);
        recyclerView.setAdapter(adapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            userId = currentUser.getUid();
        }

        cargarHistorial();

        btnFiltroTodos = findViewById(R.id.btnFiltroTodos);
        btnFiltroPendientes = findViewById(R.id.btnFiltroPendientes);
        btnFiltroEnProgreso = findViewById(R.id.btnFiltroEnProgreso);
        btnFiltroTerminados = findViewById(R.id.btnFiltroTerminados);

        btnFiltroTodos.setOnClickListener(v -> {
            aplicarFiltro("Todos");
            resaltarBotonSeleccionado(btnFiltroTodos);
        });

        btnFiltroPendientes.setOnClickListener(v -> {
            aplicarFiltro("Pendiente");
            resaltarBotonSeleccionado(btnFiltroPendientes);
        });

        btnFiltroEnProgreso.setOnClickListener(v -> {
            aplicarFiltro("En Progreso");
            resaltarBotonSeleccionado(btnFiltroEnProgreso);
        });

        btnFiltroTerminados.setOnClickListener(v -> {
            aplicarFiltro("Terminado");
            resaltarBotonSeleccionado(btnFiltroTerminados);
        });

        btnFiltroTodos.performClick();

        bottomNav = findViewById(R.id.bottonNavigationView);
        configurarBottomNav();

        // Recuperar la hora guardada de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ReservaPrefs", MODE_PRIVATE);
        // Boolean ServicioTaxi = sharedPreferences.getBoolean("ServicioTaxi", false);

        MaterialButton btnNotificaciones = findViewById(R.id.notificaciones_cliente);
        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialEventos.this, ClienteNotificaciones.class);
            startActivity(intent);
        });

        /*
        CardView cardView = findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones que quieres realizar cuando se haga clic
                Intent intent = new Intent(HistorialEventos.this, DetallesReserva.class);
                startActivity(intent);
            }
        });
        */

        //Verificacion

        // 1. Cargar lista guardada (si existe)
        NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(this);
        Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

        // 2. Crear o cargar NotificationManager con la lista actual
        NotificationManagerNoAPP notificationManagerNoAPP = new NotificationManagerNoAPP();
        if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
            for (Notificaciones n : notificacionesGuardadas) {
                Tipo = n.getTipo();
                notificationManagerNoAPP.getListaNotificaciones().add(n);
                if ("01".equals(n.getTipo().trim())) {
                    solicitarCheckout = true;
                }
            }
        }
        /*
        if(ServicioTaxi){
            btnTaxista.setEnabled(true);
            btnTaxista.setAlpha(1f);
        }

        if (solicitarCheckout) {
            btnCheckout.setEnabled(false);
            btnCheckout.setAlpha(0.5f);  // Establecer la opacidad al 50% (0.0f - completamente transparente, 1.0f - completamente opaco)
        }
        */

        /*
        if (btnCheckout.isEnabled()) {
            btnCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDialogoCheckout();
                }
            });
        }
        */
    }
/*
    private void cargarHistorial(){
        historialItems.clear();  // Limpia la lista antes de volver a llenarla

        // Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = "o60eTvckH0OpIkS29izDulVrsdC2";

        db.collection("usuarios")
                .document(userId)
                .collection("Reservas")
                .orderBy("fechaIni", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(reservasSnapshot -> {
                    if (!reservasSnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot reservaDoc : reservasSnapshot) {
                            String idReserva = reservaDoc.getId();
                            String hotelId = reservaDoc.getString("hotelId");
                            Timestamp fechaInicioTS = reservaDoc.getTimestamp("fechaIni");
                            Timestamp fechaFinTS = reservaDoc.getTimestamp("fechaFin");
                            String fechaInicio = getFechaBonitaDesdeTimestamp(fechaInicioTS);
                            String fechaFin = getFechaBonitaDesdeTimestamp(fechaFinTS);
                            Boolean checkoutSolicitado = reservaDoc.getBoolean("checkoutSolicitado");
                            String solicitarTaxista = reservaDoc.getString("solicitarTaxista");
                            String estado = reservaDoc.getString("estado");

                            // Buscar datos del hotel
                            db.collection("Hoteles").document(hotelId)
                                    .get()
                                    .addOnSuccessListener(hotelDoc -> {
                                        if (hotelDoc.exists()) {
                                            String nombreHotel = hotelDoc.getString("nombre");
                                            String ubicacion = hotelDoc.getString("ubicacion");
                                            Boolean servicioTaxi = hotelDoc.getBoolean("servicioTaxi");
                                            Boolean checkoutEnable = !(checkoutSolicitado);

                                            String fechas = "Desde el " + fechaInicio + " al " + fechaFin;

                                            // Imagen según el hotel
                                            int imagen = R.drawable.hotel1;
                                            if ("hotel2".equals(hotelId)) {
                                                imagen = R.drawable.hotel2;
                                            }

                                            // Agregar item y notificar cambios
                                            historialItems.add(new HistorialItem(idReserva, estado, nombreHotel, fechas, "📍 " + ubicacion, imagen, solicitarTaxista, checkoutEnable, servicioTaxi, fechaInicioTS, fechaFinTS));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar reservas", e));

    }
*/

    private void cargarHistorial() {
        historialItems.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(userId)
                .collection("Reservas")
                .orderBy("fechaIni", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(reservasSnapshot -> {
                    if (!reservasSnapshot.isEmpty()) {

                        // Aquí acumulamos las tareas de obtener hoteles
                        List<Task<DocumentSnapshot>> hotelTasks = new ArrayList<>();

                        // Mapa temporal para guardar datos de cada reserva junto con su hotelId
                        Map<String, QueryDocumentSnapshot> reservasMap = new HashMap<>();
                        // List<String> hotelIds = new ArrayList<>(); // <-- lista paralela

                        for (QueryDocumentSnapshot reservaDoc : reservasSnapshot) {
                            String hotelId = reservaDoc.getString("hotelId");
                            reservasMap.put(reservaDoc.getId(), reservaDoc);
                            // hotelIds.add(hotelId);
                            // Agregar tarea para obtener hotel
                            hotelTasks.add(db.collection("Hoteles").document(hotelId).get());
                        }

                        Tasks.whenAllSuccess(hotelTasks).addOnSuccessListener(results -> {
                            for (int i = 0; i < results.size(); i++) {
                                DocumentSnapshot hotelDoc = (DocumentSnapshot) results.get(i);
                                // Aquí obtenemos la reserva correspondiente
                                DocumentSnapshot reservaDoc = reservasSnapshot.getDocuments().get(i);
                                String idReserva = reservaDoc.getId();
                                String estadoActual = reservaDoc.getString("estado");
                                Timestamp fechaInicioTS = reservaDoc.getTimestamp("fechaIni");
                                Timestamp fechaFinTS = reservaDoc.getTimestamp("fechaFin");
                                String fechaInicio = getFechaBonitaDesdeTimestamp(fechaInicioTS);
                                String fechaFin = getFechaBonitaDesdeTimestamp(fechaFinTS);
                                Boolean checkoutSolicitado = reservaDoc.getBoolean("checkoutSolicitado");
                                String solicitarTaxista = reservaDoc.getString("solicitarTaxista");

                                if (hotelDoc.exists()) {
                                    String nombreHotel = hotelDoc.getString("nombre");
                                    String distrito = hotelDoc.getString("distrito");
                                    String provincia = hotelDoc.getString("provincia");
                                    Double valoracion = hotelDoc.getDouble("valoracion");
                                    String ubicacion = distrito+", "+provincia;
                                    String UrlHotel = hotelDoc.getString("UrlFotoHotel");
                                    Boolean servicioTaxi = hotelDoc.getBoolean("servicioTaxi");
                                    //Boolean checkoutEnable = !(checkoutSolicitado);
                                    // Comparar si hoy es el mismo día que fechaFin
                                    Calendar hoy = Calendar.getInstance();
                                    Calendar fechaFinCalendar = Calendar.getInstance();
                                    fechaFinCalendar.setTime(fechaFinTS.toDate());
                                    Date ahora = new Date(); // hora actual del sistema

                                    boolean esMismoDia = hoy.get(Calendar.YEAR) == fechaFinCalendar.get(Calendar.YEAR) &&
                                            hoy.get(Calendar.MONTH) == fechaFinCalendar.get(Calendar.MONTH) &&
                                            hoy.get(Calendar.DAY_OF_MONTH) == fechaFinCalendar.get(Calendar.DAY_OF_MONTH);

                                    Boolean checkoutEnable;
                                    if (esMismoDia) {
                                        checkoutEnable = !checkoutSolicitado; // Puede pedir checkout solo si no lo solicitó
                                    } else {
                                        checkoutEnable = false;

                                        // Solo forzar a false si hoy es ANTES del día de salida (aún no se puede solicitar)
                                        if (ahora.before(fechaFinTS.toDate()) && checkoutSolicitado != null && checkoutSolicitado) {
                                            db.collection("usuarios")
                                                    .document(userId)
                                                    .collection("Reservas")
                                                    .document(idReserva)
                                                    .update("checkoutSolicitado", false)
                                                    .addOnSuccessListener(aVoid -> Log.d("Checkout", "Resetado a false porque aún no es el día"))
                                                    .addOnFailureListener(e -> Log.e("Checkout", "Error al resetear checkoutSolicitado", e));
                                        }
                                    }

                                    // Verificación del estado según fecha actual
                                    String estadoEsperado;

                                    if (ahora.before(fechaInicioTS.toDate())) {
                                        estadoEsperado = "Pendiente";
                                    } else if (ahora.after(fechaFinTS.toDate())) {
                                        estadoEsperado = "Terminado";
                                    } else {
                                        estadoEsperado = "En Progreso";
                                    }

                                    // Si el estado guardado no es el correcto, actualizar en Firestore
                                    if (!estadoEsperado.equals(estadoActual)) {
                                        db.collection("usuarios")
                                                .document(userId)
                                                .collection("Reservas")
                                                .document(idReserva)
                                                .update("estado", estadoEsperado)
                                                .addOnSuccessListener(aVoid -> Log.d("Estado", "Actualizado a " + estadoEsperado))
                                                .addOnFailureListener(e -> Log.e("Estado", "Error al actualizar estado", e));
                                    }

                                    HistorialItem historialItem = new HistorialItem(idReserva, estadoEsperado, nombreHotel, "📍 " + ubicacion, solicitarTaxista, checkoutEnable, servicioTaxi, fechaInicioTS, fechaFinTS);
                                    historialItem.setUrlImage(UrlHotel);
                                    historialItem.setValoracion(valoracion);
                                    historialItems.add(historialItem);
                                    Log.d("HistorialEventos", "Item cargado: idReserva=" + idReserva + ", hotel=" + nombreHotel + ", estado=" + estadoEsperado + ", ubicacion=" + ubicacion);
                                }
                            }
                            // Línea: justo antes de actualizar historialItemsFiltrados y llamar adapter
                            Log.d("HistorialEventos", "Historial cargado. Items totales: " + historialItems.size());

                            aplicarFiltro("Todos");


                            // Actualizar también la lista filtrada con todos los elementos
                            //historialItemsFiltrados.clear();
                            //historialItemsFiltrados.addAll(historialItems);

                            // Mostrar los datos filtrados
                            //adapter.setItems(historialItemsFiltrados); // <- este método lo agregas en el paso 3
                            //adapter.notifyDataSetChanged();
                        });

                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar reservas", e));
    }

    @Override
    public void onItemClicked(HistorialItem item) {
        Intent intent = new Intent(this, DetallesReserva.class);
        intent.putExtra("idReserva", item.getIdReserva());
        startActivity(intent);
    }
    @Override
    public void onTaxiClicked(HistorialItem item) {
        Intent intent = new Intent(this, ClienteServicioTaxi.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.historialCliente);
    }
    private void configurarBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.historialCliente) {
                return true; // Ya estás en esta pantalla
            } else if (id == R.id.chat_cliente) {
                startActivity(new Intent(this, ClienteChat.class));
            } else if (id == R.id.inicioCliente) {
                startActivity(new Intent(this, HomeCliente.class));
            } else if (id == R.id.perfilCliente) {
                startActivity(new Intent(this, PerfilCliente.class));
            }
            return true;
        });
    }
    @Override
    public void onCheckoutClicked(HistorialItem item) {
        mostrarDialogoCheckout(item.getIdReserva()); // ya lo tienes implementado
    }

    public String getFechaBonitaDesdeTimestamp(Timestamp timestamp) {
        if (timestamp == null) return ""; // seguridad por si viene nulo
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("d 'de' MMMM", new Locale("es", "ES"));
        return sdf.format(date);
    }
    private void mostrarDialogoCheckout(String IdReserva) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_dialog_checkout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Obtener la vista del diálogo
        View modalView = dialog.findViewById(android.R.id.content);

        // Ahora puedes acceder al botón dentro del diálogo
        Button btnContinuar = modalView.findViewById(R.id.btn_continue);
        btnContinuar.setOnClickListener(v -> {
            // Cierra el modal actual
            dialog.dismiss();

            // Quitar el blur cuando se cierra el modal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getWindow().getDecorView().setRenderEffect(null);
            }

            // Llamar al método para abrir el nuevo modal "cliente_consumosextras"
            mostrarClienteConsumoExtras(IdReserva);
        });
    }
    // Método para mostrar el nuevo modal
    private void mostrarClienteConsumoExtras(String IdReserva) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_consumosextras);
        AlertDialog dialog = builder.create();
        dialog.show();
        // Ahora accedemos al botón btn_solicitar_checkout dentro del nuevo modal
        Button btnSolicitarCheckout = dialog.findViewById(R.id.btn_solicitar_checkout);
        btnSolicitarCheckout.setOnClickListener(v -> {
            // Cerrar el modal
            dialog.dismiss();
            // Deshabilitamos el botón original
            if (IdReserva != null) {

                // Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Obtener la hora actual en formato "HH:mm"
                String horaActual = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                //String checkin = "No especificado";

                // Crear mapa con los campos a actualizar
                Map<String, Object> updates = new HashMap<>();
                updates.put("checkoutSolicitado", true);
                //updates.put("CheckOutHora", horaActual);  // <-- esto será tipo String
                //updates.put("CheckInHora", checkin);

                db.collection("usuarios")
                        .document(userId)
                        .collection("Reservas")
                        .document(IdReserva)
                        .update(updates)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Reserva actualizada correctamente"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar reserva", e));
            }

            // Aquí iría la validación real, por ahora mostramos mensaje:
            Toast.makeText(HistorialEventos.this, "¡Solicitud registrada correctamente!", Toast.LENGTH_SHORT).show();
            LanzarNotificacionSolicitarCheckout();
            LanzarValoracion();
        });
    }

    public void LanzarValoracion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.cliente_valoracion);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Obtener referencias a los elementos en el diálogo
        EditText comentarioText = dialog.findViewById(R.id.comentario_valo);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        ratingBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
        Button btnEnviarValo = dialog.findViewById(R.id.btn_enviar_valo);

        btnEnviarValo.setOnClickListener(v -> {
            // Obtener el valor de la calificación
            float calificacion = ratingBar.getRating();
            String comentario = comentarioText.getText().toString().trim();

            // Validar que haya dado una calificación
            if (calificacion == 0) {
                Toast.makeText(this, "Por favor, califica nuestro servicio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí puedes guardar la calificación y el comentario
            // Por ejemplo, enviarlos a tu servidor o guardarlos localmente

            cargarHistorial();

            // Mostrar mensaje de confirmación
            Toast.makeText(this, "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show();
            // Cerrar el diálogo
            dialog.dismiss();
        });
    }
    private void aplicarFiltro(String estadoFiltrado) {
        historialItemsFiltrados.clear();
        Log.d("HistorialEventos", "Aplicando filtro: " + estadoFiltrado);
        Log.d("HistorialEventos", "Items antes de filtrar: " + historialItems.size());
        if (estadoFiltrado.equals("Todos")) {
            historialItemsFiltrados.addAll(historialItems);
        } else {
            for (HistorialItem item : historialItems) {
                if (item.getEstado().equalsIgnoreCase(estadoFiltrado)) {
                    historialItemsFiltrados.add(item);
                }
            }
        }
        Log.d("HistorialEventos", "Items después de filtrar: " + historialItemsFiltrados.size());

        adapter.setItems(historialItemsFiltrados); // necesitas este método en el adapter (lo harás en el paso 3)
    }

    private void resaltarBotonSeleccionado(MaterialButton botonSeleccionado) {
        btnFiltroTodos.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBotonInactivo));
        btnFiltroPendientes.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBotonInactivo));
        btnFiltroEnProgreso.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBotonInactivo));
        btnFiltroTerminados.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBotonInactivo));

        botonSeleccionado.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBotonActivo));
    }


    public void LanzarNotificacionSolicitarCheckout() {
        // Tipo Solicitar Checkout: 01
        String tipo = "01";
        String titulo = "Solicitar Checkout";
        String tituloAmigable = "¡Se ha realizado el checkout correctamente!";
        String mensaje = "El checkout fue solicitado correctamente. Cuando este proceso termine se le notificará por este medio para que pueda realizar su pago.";
        String mensajeExtra = "Este proceso podría tardar alrededor de 1 hora, por favor estar pendiente a la siguiente notificación.";
        Long fecha = System.currentTimeMillis();

        // 1. Cargar lista guardada (si existe)
        NotificacionesStorageHelper storageHelper = new NotificacionesStorageHelper(this);
        Notificaciones[] notificacionesGuardadas = storageHelper.leerArchivoNotificacionesDesdeSubcarpeta();

        // 2. Crear o cargar NotificationManager con la lista actual
        NotificationManagerNoAPP notificationManagerNoAPP = new NotificationManagerNoAPP();
        if (notificacionesGuardadas != null && notificacionesGuardadas.length > 0) {
            for (Notificaciones n : notificacionesGuardadas) {
                notificationManagerNoAPP.getListaNotificaciones().add(n);
            }
        }

        // 3. Agregar la nueva notificación a la lista
        notificationManagerNoAPP.agregarNotificacion(tipo, titulo, tituloAmigable, mensaje, mensajeExtra, fecha);

        // 4. Guardar la lista actualizada
        Notificaciones[] arregloParaGuardar = notificationManagerNoAPP.getListaNotificaciones()
                .toArray(new Notificaciones[0]);
        storageHelper.guardarArchivoNotificacionesEnSubcarpeta(arregloParaGuardar);

        // 5. Lanzar la notificación visual como se hace normalmente
        Intent intent = new Intent(HistorialEventos.this, ClienteNotificaciones.class);
        intent.putExtra("Case", "01");
        String ContentTitle="¡Se ha realizado el checkout correctamente!";
        String ContentText="Porfavor estar pendiente a las notificaciones, ya que por este medio se le notificará cuando se haya terminado el proceso.";
        //intent.putExtra("ContentTitle",ContentTitle);
        //intent.putExtra("ContentText",ContentText);
        //Pero esto no se va a lanzar hasta que se clickee en las notificaciones por eso se quedara pendiente
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_r_r_2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.checkout_image_v2))
                .setContentTitle(ContentTitle)
                .setContentText(ContentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat  = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(1, builder.build());
        }
        /*
        WorkRequest notificacionRetrasada = new OneTimeWorkRequest.Builder(NotificacionCheckoutWorker.class)
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(this).enqueue(notificacionRetrasada);
        */
    }
}