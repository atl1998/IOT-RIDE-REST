package com.example.hotelreservaapp.superadmin;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hotelreservaapp.R;
import com.example.hotelreservaapp.adapter.LogsAdapter;
import com.example.hotelreservaapp.databinding.SuperadminLogsFragmentBinding;
import com.example.hotelreservaapp.databinding.SuperadminReportesFragmentBinding;
import com.example.hotelreservaapp.model.LogItem;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class LogsFragment extends Fragment {
    private SuperadminLogsFragmentBinding binding;
    private List<LogItem> todosLosLogs = new ArrayList<>();
    private LogsAdapter adapter;

    public LogsFragment() {
        // Especifica el layout directamente si quieres evitar inflate manual
        super(R.layout.superadmin_logs_fragment); // Este es tu antiguo layout de logs
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SuperadminLogsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new LogsAdapter(new ArrayList<>());
        binding.recyclerLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLogs.setAdapter(adapter);
        LinearLayout opcionReportes = binding.opcionReportes;
        LinearLayout opcionLogs = binding.opcionLogs;

        ImageView iconHelp = view.findViewById(R.id.iconHelp);
        iconHelp.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sección Logs")
                    .setMessage("Aquí puedes visualizar todos los eventos realizados mediante la app.\nPodrás obtener detalles acerca de cada evento e incluso tienes la opción de descarga en formato PDF.")
                    .setPositiveButton("Entendido", null)
                    .show();
        });


        //Función descarga pdf
        binding.btnExportarPdf.setOnClickListener(v -> exportarLogsComoPDF());

        opcionLogs.setBackgroundResource(R.drawable.bg_opcion_selected);
        opcionReportes.setBackgroundResource(R.drawable.bg_opcion_unselected);

        opcionReportes.setOnClickListener(v -> {
            opcionReportes.setBackgroundResource(R.drawable.bg_opcion_selected);
            opcionLogs.setBackgroundResource(R.drawable.bg_opcion_unselected);

            // Navega al fragmento de reportes
            Fragment reportesFragment = new ReportesFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, reportesFragment)
                    .addToBackStack(null)
                    .commit();
        });
        // Cargar logs demo
        cargarLogsDesdeFirestore();

        // Picker de fecha
        binding.etFecha.setOnClickListener(v -> {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (dialogView, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        binding.etFecha.setText(fechaFormateada);
                        filtrarPorFecha(fechaFormateada);
                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });
        binding.btnLimpiarFecha.setOnClickListener(v -> {
            binding.etFecha.setText("");
            adapter.actualizarLista(todosLosLogs);
        });
    }
    private void cargarLogsDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("logs")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<LogItem> logs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        LogItem log = doc.toObject(LogItem.class);
                        logs.add(log);
                    }
                    todosLosLogs = logs;
                    adapter.actualizarLista(todosLosLogs);
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al cargar logs", Toast.LENGTH_SHORT).show());
    }

    private void filtrarPorFecha(String fecha) {
        List<LogItem> filtrados = new ArrayList<>();
        for (LogItem log : todosLosLogs) {
            if (fecha.equals(log.getFechaFormateada())) {
                filtrados.add(log);
            }
        }
        adapter.actualizarLista(filtrados);
    }

    private void exportarLogsComoPDF() {
        List<LogItem> logs = adapter.getListaActual();

        if (logs.isEmpty()) {
            Toast.makeText(requireContext(), "No hay logs para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument documento = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595, pageHeight = 842;
        int x = 40, y = 60;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = documento.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // 🟨 Header background
        paint.setColor(getResources().getColor(R.color.amarillo_dorado, null));
        canvas.drawRect(0, 0, pageWidth, 100, paint);

        // 🖊 Título principal
        paint.setColor(Color.BLACK);
        paint.setTextSize(20f);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("RIDE & REST", pageWidth / 2f, 40, paint);

        // 🖊 Subtítulo
        paint.setTextSize(14f);
        paint.setFakeBoldText(false);
        canvas.drawText("Tu plataforma confiable para gestión hotelera", pageWidth / 2f, 70, paint);

        // 📌 Logo (justo debajo del header, a la derecha)
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.burbuja_ride_rest);
        int logoWidth = 120, logoHeight = 120; // tamaño mejorado para evitar pixelado
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoWidth, logoHeight, true);
        canvas.drawBitmap(scaledLogo, pageWidth - logoWidth - 20, 105, null);  // fuera del header

        // 🔽 Reinicio para contenido de logs
        y = 160;

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(12f);
        paint.setFakeBoldText(false);

        for (LogItem log : logs) {
            String linea = log.getFechaFormateada() + " " + log.getHoraFormateada() + " - " + log.getUsuario() + ": " + log.getAccion();
            canvas.drawText(linea, x, y, paint);
            y += 25;

            if (y > pageHeight - 60) {
                documento.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, documento.getPages().size() + 1).create();
                page = documento.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 60;
            }
        }

        // 🕓 Fecha de generación
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(10f);
        String fechaGenerado = "Generado el " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText(fechaGenerado, x, pageHeight - 30, paint);

        documento.finishPage(page);

        File pdfFile = new File(requireContext().getExternalFilesDir(null),
                "logs_exportados_" + System.currentTimeMillis() + ".pdf");

        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            documento.writeTo(fos);
            fos.close();
            documento.close();

            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    pdfFile
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Abrir PDF con..."));

            Toast.makeText(requireContext(), "PDF generado exitosamente", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al generar PDF", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No se encontró una app para abrir PDF", Toast.LENGTH_LONG).show();
        }
    }
}