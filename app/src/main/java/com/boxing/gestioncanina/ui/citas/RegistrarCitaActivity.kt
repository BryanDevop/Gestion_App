package com.boxing.gestioncanina.ui.citas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.models.Cita
import com.boxing.gestioncanina.data.repository.CitasRepository
import com.boxing.gestioncanina.data.repository.MascotasRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegistrarCitaActivity : AppCompatActivity() {

    private val TAG = "RegistrarCitaActivity"
    private val citasRepository = CitasRepository()
    private val mascotasRepository = MascotasRepository()

    private var mascotaSeleccionadaId: String? = null
    private var fechaSeleccionada: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "════════════════════════════════════════")
        Log.d(TAG, "onCreate INICIADO")
        Log.d(TAG, "════════════════════════════════════════")

        try {
            Log.d(TAG, "Intentando cargar layout...")
            setContentView(R.layout.activity_fragment_citas_register)
            Log.d(TAG, "✅ Layout cargado exitosamente")

            Log.d(TAG, "Configurando Toolbar...")
            setupToolbar()
            Log.d(TAG, "✅ Toolbar configurado")

            Log.d(TAG, "Configurando Dropdowns...")
            setupDropdowns()
            Log.d(TAG, "✅ Dropdowns configurados")

            Log.d(TAG, "Configurando DateTimePickers...")
            setupDateTimePickers()
            Log.d(TAG, "✅ DateTimePickers configurados")

            Log.d(TAG, "Configurando Botones...")
            setupButtons()
            Log.d(TAG, "✅ Botones configurados")

            Log.d(TAG, "Cargando mascotas...")
            cargarMascotas()
            Log.d(TAG, "✅ Proceso de carga de mascotas iniciado")

            Log.d(TAG, "════════════════════════════════════════")
            Log.d(TAG, "onCreate COMPLETADO SIN ERRORES")
            Log.d(TAG, "════════════════════════════════════════")

        } catch (e: Exception) {
            Log.e(TAG, "❌❌❌ ERROR FATAL EN onCreate ❌❌❌")
            Log.e(TAG, "Tipo de error: ${e.javaClass.simpleName}")
            Log.e(TAG, "Mensaje: ${e.message}")
            Log.e(TAG, "Stack trace completo:")
            e.printStackTrace()

            Toast.makeText(
                this,
                "Error al cargar la pantalla: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }

    private fun setupToolbar() {
        try {
            val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
            Log.d(TAG, "Toolbar encontrado: ${toolbar != null}")
            toolbar.setNavigationOnClickListener {
                Log.d(TAG, "Click en navegación - Cerrando activity")
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en setupToolbar: ${e.message}", e)
            throw e
        }
    }

    private fun setupDropdowns() {
        try {
            val actvTipoCita = findViewById<AutoCompleteTextView>(R.id.actvTipoCita)
            val actvTiempoRecordatorio = findViewById<AutoCompleteTextView>(R.id.actvTiempoRecordatorio)
            val switchRecordatorio = findViewById<SwitchMaterial>(R.id.switchRecordatorio)
            val tilTiempoRecordatorio = findViewById<TextInputLayout>(R.id.tilTiempoRecordatorio)

            Log.d(TAG, "Vistas encontradas - TipoCita: ${actvTipoCita != null}, TiempoRec: ${actvTiempoRecordatorio != null}")

            // Tipos de cita
            val tiposCita = arrayOf(
                "Consulta general",
                "Vacunación",
                "Desparasitación",
                "Cirugía",
                "Emergencia",
                "Control",
                "Peluquería",
                "Análisis de laboratorio"
            )
            val tiposAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiposCita)
            actvTipoCita.setAdapter(tiposAdapter)

            // Tiempos de recordatorio
            val tiemposRecordatorio = arrayOf(
                "15 minutos antes",
                "30 minutos antes",
                "1 hora antes",
                "2 horas antes",
                "1 día antes"
            )
            val tiemposAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiemposRecordatorio)
            actvTiempoRecordatorio.setAdapter(tiemposAdapter)

            // Configurar listener para el switch de recordatorio
            switchRecordatorio.setOnCheckedChangeListener { _, isChecked ->
                tilTiempoRecordatorio.isEnabled = isChecked
                actvTiempoRecordatorio.isEnabled = isChecked
            }

            Log.d(TAG, "Adapters configurados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error en setupDropdowns: ${e.message}", e)
            throw e
        }
    }

    private fun cargarMascotas() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Iniciando carga de mascotas...")
                showLoading(true)

                Log.d(TAG, "Llamando a mascotasRepository.obtenerMascotasUsuario()")
                val mascotas = mascotasRepository.obtenerMascotasUsuario()
                Log.d(TAG, "Mascotas obtenidas: ${mascotas.size}")

                if (mascotas.isEmpty()) {
                    Log.w(TAG, "No hay mascotas registradas")
                    mostrarDialogoSinMascotas()
                    return@launch
                }

                val actvMascota = findViewById<AutoCompleteTextView>(R.id.actvMascota)
                val nombresMascotas = mascotas.map { "${it.nombre} - ${it.especie}" }.toTypedArray()
                Log.d(TAG, "Nombres de mascotas: ${nombresMascotas.joinToString()}")

                val mascotasAdapter = ArrayAdapter(
                    this@RegistrarCitaActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    nombresMascotas
                )
                actvMascota.setAdapter(mascotasAdapter)

                // Guardar referencia de IDs
                actvMascota.setOnItemClickListener { _, _, position, _ ->
                    mascotaSeleccionadaId = mascotas[position].id
                    Log.d(TAG, "Mascota seleccionada: ${mascotas[position].nombre} (ID: ${mascotaSeleccionadaId})")
                }

                showLoading(false)
                Log.d(TAG, "✅ Mascotas cargadas exitosamente")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cargar mascotas: ${e.message}", e)
                showLoading(false)
                showError("Error al cargar mascotas: ${e.message}")
            }
        }
    }

    private fun setupDateTimePickers() {
        try {
            val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
            val etHora = findViewById<TextInputEditText>(R.id.etHora)
            val tilFecha = findViewById<TextInputLayout>(R.id.tilFecha)
            val tilHora = findViewById<TextInputLayout>(R.id.tilHora)

            Log.d(TAG, "DateTimePickers encontrados - Fecha: ${etFecha != null}, Hora: ${etHora != null}")

            // Fecha
            etFecha.setOnClickListener {
                Log.d(TAG, "Click en campo fecha")
                mostrarDatePicker()
            }
            tilFecha.setEndIconOnClickListener {
                Log.d(TAG, "Click en icono fecha")
                mostrarDatePicker()
            }

            // Hora
            etHora.setOnClickListener {
                Log.d(TAG, "Click en campo hora")
                mostrarTimePicker()
            }
            tilHora.setEndIconOnClickListener {
                Log.d(TAG, "Click en icono hora")
                mostrarTimePicker()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en setupDateTimePickers: ${e.message}", e)
            throw e
        }
    }

    private fun mostrarDatePicker() {
        val minDate = Calendar.getInstance()
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                fechaSeleccionada.set(year, month, dayOfMonth)
                etFecha.setText(dateFormat.format(fechaSeleccionada.time))
                Log.d(TAG, "Fecha seleccionada: ${dateFormat.format(fechaSeleccionada.time)}")
            },
            fechaSeleccionada.get(Calendar.YEAR),
            fechaSeleccionada.get(Calendar.MONTH),
            fechaSeleccionada.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = minDate.timeInMillis
            show()
        }
    }

    private fun mostrarTimePicker() {
        val etHora = findViewById<TextInputEditText>(R.id.etHora)

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                fechaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay)
                fechaSeleccionada.set(Calendar.MINUTE, minute)
                etHora.setText(timeFormat.format(fechaSeleccionada.time))
                Log.d(TAG, "Hora seleccionada: ${timeFormat.format(fechaSeleccionada.time)}")
            },
            fechaSeleccionada.get(Calendar.HOUR_OF_DAY),
            fechaSeleccionada.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun setupButtons() {
        try {
            val btnCancelar = findViewById<MaterialButton>(R.id.btnCancelar)
            val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardar)

            Log.d(TAG, "Botones encontrados - Cancelar: ${btnCancelar != null}, Guardar: ${btnGuardar != null}")

            btnCancelar.setOnClickListener {
                Log.d(TAG, "Click en Cancelar")
                finish()
            }

            btnGuardar.setOnClickListener {
                Log.d(TAG, "Click en Guardar - iniciando validación")
                validarYGuardarCita()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en setupButtons: ${e.message}", e)
            throw e
        }
    }

    private fun validarYGuardarCita() {
        Log.d(TAG, "Validando formulario...")

        val tilMascota = findViewById<TextInputLayout>(R.id.tilMascota)
        val tilTipoCita = findViewById<TextInputLayout>(R.id.tilTipoCita)
        val tilMotivo = findViewById<TextInputLayout>(R.id.tilMotivo)
        val tilFecha = findViewById<TextInputLayout>(R.id.tilFecha)
        val tilHora = findViewById<TextInputLayout>(R.id.tilHora)
        val tilVeterinario = findViewById<TextInputLayout>(R.id.tilVeterinario)
        val tilClinica = findViewById<TextInputLayout>(R.id.tilClinica)

        val actvTipoCita = findViewById<AutoCompleteTextView>(R.id.actvTipoCita)
        val etMotivo = findViewById<TextInputEditText>(R.id.etMotivo)
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val etHora = findViewById<TextInputEditText>(R.id.etHora)
        val etVeterinario = findViewById<TextInputEditText>(R.id.etVeterinario)
        val etClinica = findViewById<TextInputEditText>(R.id.etClinica)
        val etNotas = findViewById<TextInputEditText>(R.id.etNotas)
        val switchRecordatorio = findViewById<SwitchMaterial>(R.id.switchRecordatorio)
        val actvTiempoRecordatorio = findViewById<AutoCompleteTextView>(R.id.actvTiempoRecordatorio)

        // Limpiar errores previos
        tilMascota.error = null
        tilTipoCita.error = null
        tilMotivo.error = null
        tilFecha.error = null
        tilHora.error = null
        tilVeterinario.error = null
        tilClinica.error = null

        // Validaciones
        if (mascotaSeleccionadaId == null) {
            tilMascota.error = "Selecciona una mascota"
            Log.w(TAG, "Validación fallida: No hay mascota seleccionada")
            return
        }

        val tipoCita = actvTipoCita.text.toString().trim()
        if (tipoCita.isEmpty()) {
            tilTipoCita.error = "Selecciona el tipo de cita"
            Log.w(TAG, "Validación fallida: Tipo de cita vacío")
            return
        }

        val motivo = etMotivo.text.toString().trim()
        if (motivo.isEmpty()) {
            tilMotivo.error = "Ingresa el motivo de la cita"
            Log.w(TAG, "Validación fallida: Motivo vacío")
            return
        }

        val fecha = etFecha.text.toString().trim()
        if (fecha.isEmpty()) {
            tilFecha.error = "Selecciona la fecha"
            Log.w(TAG, "Validación fallida: Fecha vacía")
            return
        }

        val hora = etHora.text.toString().trim()
        if (hora.isEmpty()) {
            tilHora.error = "Selecciona la hora"
            Log.w(TAG, "Validación fallida: Hora vacía")
            return
        }

        val veterinario = etVeterinario.text.toString().trim()
        if (veterinario.isEmpty()) {
            tilVeterinario.error = "Ingresa el nombre del veterinario"
            Log.w(TAG, "Validación fallida: Veterinario vacío")
            return
        }

        val clinica = etClinica.text.toString().trim()
        if (clinica.isEmpty()) {
            tilClinica.error = "Ingresa la clínica o ubicación"
            Log.w(TAG, "Validación fallida: Clínica vacía")
            return
        }

        Log.d(TAG, "✅ Validación exitosa - Creando objeto Cita")

        // Crear objeto Cita
        val cita = Cita(
            mascotaId = mascotaSeleccionadaId!!,
            tipoCita = tipoCita,
            motivo = motivo,
            fecha = dateFormat.format(fechaSeleccionada.time),
            hora = timeFormat.format(fechaSeleccionada.time),
            veterinario = veterinario,
            clinica = clinica,
            recordatorioActivo = switchRecordatorio.isChecked,
            tiempoRecordatorio = if (switchRecordatorio.isChecked)
                actvTiempoRecordatorio.text.toString() else null,
            notas = etNotas.text.toString().trim(),
            estado = "Programada"
        )

        Log.d(TAG, "Cita creada: $cita")
        guardarCita(cita)
    }

    private fun guardarCita(cita: Cita) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Guardando cita...")
                showLoading(true)
                citasRepository.crearCita(cita)
                showLoading(false)
                Log.d(TAG, "✅ Cita guardada exitosamente")

                MaterialAlertDialogBuilder(this@RegistrarCitaActivity)
                    .setTitle("¡Cita registrada!")
                    .setMessage("La cita se ha registrado exitosamente")
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .show()

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al guardar la cita: ${e.message}", e)
                showLoading(false)
                showError("Error al guardar la cita: ${e.message}")
            }
        }
    }

    private fun mostrarDialogoSinMascotas() {
        Log.d(TAG, "Mostrando diálogo: Sin mascotas")
        MaterialAlertDialogBuilder(this)
            .setTitle("No tienes mascotas")
            .setMessage("Necesitas registrar al menos una mascota antes de crear una cita")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showLoading(show: Boolean) {
        val progressBar = findViewById<CircularProgressIndicator>(R.id.progressBar)
        val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = findViewById<MaterialButton>(R.id.btnCancelar)

        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !show
        btnCancelar.isEnabled = !show

        Log.d(TAG, "Loading: $show")
    }

    private fun showError(message: String) {
        Log.e(TAG, "Mostrando error: $message")
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() llamado")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() llamado")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() llamado")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() llamado")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() llamado")
    }
}