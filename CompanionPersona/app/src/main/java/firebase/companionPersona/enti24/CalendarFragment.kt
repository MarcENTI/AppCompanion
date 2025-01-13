package firebase.companionPersona.enti24

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var monthTitle: TextView
    private lateinit var prevMonthBtn: ImageButton
    private lateinit var nextMonthBtn: ImageButton
    private lateinit var daysContainer: GridLayout

    private var calendar: Calendar = Calendar.getInstance()

    // Eventos: "YYYY-M-D" -> Pair(Título, Descripción)
    private val eventsMap: Map<String, Pair<String, String>> = mapOf(
        "2009-4-15" to ("Evento Especial" to "Descripción del evento especial"),
        "2009-5-24" to ("Nochebuena" to "Cena con la familia"),
        "2009-5-31" to ("Fin de Año" to "Fiesta de fin de año con amigos")
    )

    private val dayHeaders = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        monthTitle = view.findViewById(R.id.monthTitle)
        prevMonthBtn = view.findViewById(R.id.prevMonthBtn)
        nextMonthBtn = view.findViewById(R.id.nextMonthBtn)
        daysContainer = view.findViewById(R.id.daysContainer)

        // Ajustar la fecha inicial para el ejemplo (2009)
        calendar.add(Calendar.YEAR, -15)
        calendar.add(Calendar.MONTH, -8)
        updateCalendar()

        prevMonthBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        nextMonthBtn.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        // Llamar a la API de Steam para obtener noticias
        fetchSteamNews()
    }

    private fun updateCalendar() {
        daysContainer.removeAllViews()

        // Título del mes
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTitle.text = ("\n\n\n" + sdf.format(calendar.time).replaceFirstChar { it.uppercase() })

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val tempCal = Calendar.getInstance()
        tempCal.time = calendar.time
        tempCal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Primera fila: encabezados de día
        for (col in dayHeaders.indices) {
            val headerView = TextView(requireContext())
            headerView.text = dayHeaders[col]
            headerView.setPadding(16, 16, 16, 16)

            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(0)
            params.columnSpec = GridLayout.spec(col)
            headerView.layoutParams = params

            val white = Color.WHITE
            headerView.setTextColor(white)
            headerView.textSize = 20f
            headerView.setShadowLayer(3f, 0f, 0f, Color.BLACK)
            daysContainer.addView(headerView)
        }

        // Offset para el primer día
        val offset = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7

        var dayCount = 1
        for (row in 1..6) {
            for (col in 0..6) {
                val cellView = TextView(requireContext())
                cellView.setPadding(16, 16, 16, 16)

                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(row)
                params.columnSpec = GridLayout.spec(col)

                val white = Color.WHITE
                cellView.setTextColor(white)
                cellView.setShadowLayer(7f, 0f, 0f, Color.BLACK)
                cellView.layoutParams = params

                if ((row == 1 && col < offset) || dayCount > daysInMonth) {
                    cellView.text = ""
                } else {
                    val dayNumber = dayCount
                    val key = "$year-${month + 1}-$dayNumber"
                    val eventData = eventsMap[key]
                    cellView.text = if (eventData != null) {
                        "$dayNumber*" // Marca con un asterisco el día con evento
                    } else {
                        dayNumber.toString()
                    }

                    if (eventData != null) {
                        cellView.setOnClickListener {
                            showEventDialog(eventData.first, eventData.second)
                        }
                    }

                    dayCount++
                }

                daysContainer.addView(cellView)
            }
        }
    }

    private fun showEventDialog(title: String, description: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun fetchSteamNews() {
        val steamApiKey = "6A79FCC794BED5E95808D6E683D80AD9"

        SteamRetrofitInstance.api.getNewsForApp(
            appid = 440,
            count = 3,
            maxlength = 300,
            format = "json",
            apiKey = steamApiKey
        ).enqueue(object : Callback<SteamNewsResponse> {
            override fun onResponse(call: Call<SteamNewsResponse>, response: Response<SteamNewsResponse>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    val newsItems = newsResponse?.appnews?.newsitems
                    if (!newsItems.isNullOrEmpty()) {
                        for ((index, item) in newsItems.withIndex()) {
                            Log.d("SteamAPI", "Noticia #$index: Título=${item.title}, Autor=${item.author}")
                        }
                        Toast.makeText(requireContext(), "Primera noticia: ${newsItems[0].title}", Toast.LENGTH_LONG).show()
                    } else {
                        Log.w("SteamAPI", "No hay noticias o la respuesta está vacía.")
                    }
                } else {
                    Log.e("SteamAPI", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SteamNewsResponse>, t: Throwable) {
                Log.e("SteamAPI", "Fallo en la red: ${t.message}", t)
            }
        })
    }
}
