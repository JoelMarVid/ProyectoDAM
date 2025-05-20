package com.example.android.dialog_fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.android.databinding.DialogCreateTournamentBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.util.Calendar

class CreateTournamentDialogFragment : DialogFragment() {
    private lateinit var binding: DialogCreateTournamentBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        binding = DialogCreateTournamentBinding.inflate(layoutInflater)

        binding.etStartDateInscription.setOnClickListener {
            showDatePickerDialog { date -> binding.etStartDateInscription.setText(date) }
        }

        binding.etEndDateInscription.setOnClickListener {
            showDatePickerDialog { date -> binding.etEndDateInscription.setText(date) }
        }

        binding.etDateTournament.setOnClickListener {
            showDatePickerDialog { date -> binding.etDateTournament.setText(date) }
        }

        builder.setView(binding.root)
            .setTitle("Crear Torneo")
            .setPositiveButton("Crear") { _, _ ->
                val name = binding.etTournamentName.text.toString()
                val game = binding.spinnerGame.selectedItem.toString()
                val startDate = binding.etStartDateInscription.text.toString()
                val endDate = binding.etEndDateInscription.text.toString()
                val dateTournament = binding.etDateTournament.text.toString()
                saveTournament(name, game, startDate, endDate, dateTournament)
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveTournament(name: String, game: String, startDate: String, endDate: String, dateTournament: String) {
        val SERVER_URL = "http://10.0.2.2:3000/auth"
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("nombre", name)
            put("nombre_juego", game)
            put("fecha_ini",startDate)
            put("fecha_fin", endDate)
            put("dia_torn", dateTournament)
        }

        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )

        val request = Request.Builder()
            .url("${SERVER_URL}/tournaments")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API", "Error al guardar torneo", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("API", "Torneo creado correctamente")
                }
            }
        })
    }
}