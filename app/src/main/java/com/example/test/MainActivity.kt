package com.example.test

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import com.uttampanchasara.pdfgenerator.CreatePdf
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.round


class MainActivity : AppCompatActivity() {

    // Vistas
    private lateinit var seekBarP: SeekBar;
    private lateinit var porcentajeText: TextView;
    private lateinit var consumicion: TextInputEditText;
    private lateinit var propina: TextView;
    private lateinit var total: TextView;
    private lateinit var propinaCheck: CheckBox;
    private lateinit var propinaLayout: LinearLayout
    private lateinit var comensalesCheck: CheckBox
    private lateinit var comensalesLayout: LinearLayout
    private lateinit var seekBarC: SeekBar
    private lateinit var comensalesText: TextView
    private lateinit var comensalesTotal: TextView
    private lateinit var cambioCheck: CheckBox
    private lateinit var btnPdf: Button

    // seekBarPropina
    var minP = 10;
    var maxP = 100;
    var stepP = 5;

    // seekBarComensales
    var minC = 1;
    var maxC = 15;
    var stepC = 1;

    // Variables internas para hacer cuentas
    var consumicionNumber: Double = 0.0
    var propinaPorcNumber: Double = 0.0
    var propinaNumber: Double = 0.0
    var totalNumber: Double = 0.0
    var totalPorCNumber: Double = 0.0
    var comensalesCantidad: Int = minC

    // CheckBoxes
    var isPropina: Boolean = false
    var isComensales: Boolean = false
    var isCambio: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()

        seekBarP.max = (maxP - minP) / stepP
        seekBarC.max = (maxC - minC) / stepC

        propinaPorcNumber = (minP + (seekBarP.progress * stepP)).toDouble()

        seekBarP.setOnSeekBarChangeListener(object :
        SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                val progress = minP + (p1 * stepP)

                porcentajeText.text = "$progress%"

                hacerCuentas()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) { }
        })

        seekBarC.setOnSeekBarChangeListener(object :
        SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                comensalesCantidad = minC + (p1 * stepC)

                comensalesText.text = "$comensalesCantidad"

                hacerCuentas()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) { }
        })

        consumicion.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                hacerCuentas()
            }
        })

        propinaCheck.setOnCheckedChangeListener { _, _ ->

            isPropina = propinaCheck.isChecked

            if (isPropina) { propinaLayout.visibility = View.VISIBLE }

            else { propinaLayout.visibility = View.GONE }

            hacerCuentas()
        }

        comensalesCheck.setOnCheckedChangeListener { _, _ ->

            isComensales = comensalesCheck.isChecked

            if (isComensales) { comensalesLayout.visibility = View.VISIBLE }

            else { comensalesLayout.visibility = View.GONE }

            hacerCuentas()
        }

        cambioCheck.setOnCheckedChangeListener { _, _ ->

            isCambio = cambioCheck.isChecked

            hacerCuentas()
        }

        btnPdf.setOnClickListener {

            if (totalNumber > 0.0) generarPDF()

            else {
                Toast.makeText(this@MainActivity, "Debe ingresar consumicion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.recomendarApp -> {

                compartirApp()
            }
            else -> {

                true
            }
        }
    }

    private fun compartirApp(): Boolean {

        val compartir = Intent(Intent.ACTION_SEND)

        compartir.type = "text/plain"

        val mensaje = "Te recomiendo Tips Calculadora, para calcular propinas y dividir la cuenta con tus amigos. http://play.google.com/store/apps/details?id=com.google.android.apps.maps"

        compartir.putExtra(Intent.EXTRA_SUBJECT, "Tips Calculadora")

        compartir.putExtra(Intent.EXTRA_TEXT, mensaje)

        startActivity(Intent.createChooser(compartir, "Compartir vía"))

        return true
    }

    private fun setViews() {

        seekBarP = findViewById(R.id.seekBarPorcentaje)
        porcentajeText = findViewById(R.id.porcSeekBar)
        consumicion = findViewById(R.id.inputConsumicion)
        propina = findViewById(R.id.inputPropina)
        total = findViewById(R.id.inputTotal)
        propinaCheck = findViewById(R.id.propinaCheck)
        propinaLayout = findViewById(R.id.propinaLayout)
        comensalesCheck = findViewById(R.id.comensalesCheck)
        comensalesLayout = findViewById(R.id.comensalesLayout)
        seekBarC = findViewById(R.id.seekBarComensales)
        comensalesText = findViewById(R.id.comensalesText)
        comensalesTotal = findViewById(R.id.inputComensalTotal)
        cambioCheck = findViewById(R.id.cambioCheck)
        btnPdf = findViewById(R.id.btnPdf)
    }

    private fun calcularPorcentaje(): Double {

        propinaPorcNumber = (minP + (seekBarP.progress * stepP)).toDouble()

        return round((consumicionNumber * (propinaPorcNumber / 100.0)) * 100) / 100
    }

    private fun hacerCuentas() {

        if (!consumicion.text.isNullOrBlank()) {

            consumicionNumber = consumicion.text.toString().toDouble()

            if (isPropina && !isComensales) {

                cuentaConPropinaSinComensales()
            }
            else {

                if (isPropina && isComensales) {

                    cuentaConPropinaConComensales()
                }
                else {

                    if (!isPropina && isComensales) {

                        cuentaSinPropinaConComensales()
                    }
                    else {
                        cuentaSinPropinaSinComensales()
                    }
                }
            }
        }
        else {
            resetValores()
        }
    }

    private fun resetValores() {

        propina.text = "0.0"

        total.text = "0.0"

        comensalesTotal.text = "0.0"

        propinaNumber = 0.0

        consumicionNumber = 0.0

        totalNumber = 0.0
    }

    private fun cuentaSinPropinaSinComensales() {

        totalNumber = consumicionNumber

        if (isCambio) {

            totalNumber = ceil(consumicionNumber)
        }

        total.text = (round(totalNumber * 100) / 100).toString()

        totalPorCNumber = round(totalNumber * 100) / 100
    }

    private fun cuentaSinPropinaConComensales() {

        totalPorCNumber = consumicionNumber / comensalesCantidad

        totalNumber = totalPorCNumber * comensalesCantidad

        if (isCambio) {

            totalPorCNumber = ceil(totalPorCNumber)

            totalNumber = totalPorCNumber * comensalesCantidad
        }

        comensalesTotal.text = (round(totalPorCNumber * 100) / 100).toString()

        total.text = (round(totalNumber * 100) / 100).toString()
    }

    private fun cuentaConPropinaConComensales() {

        propinaNumber = calcularPorcentaje()

        propina.text = propinaNumber.toString()

        totalNumber = propinaNumber + consumicionNumber

        totalPorCNumber = totalNumber / comensalesCantidad

        if (isCambio) {

            totalPorCNumber = ceil(totalPorCNumber)

            totalNumber = totalPorCNumber * comensalesCantidad

            propinaNumber = totalNumber - consumicionNumber

            propina.text = (round(propinaNumber * 100) / 100).toString()
        }

        comensalesTotal.text = (round(totalPorCNumber * 100) / 100).toString()

        total.text = (round(totalNumber * 100) / 100).toString()
    }

    private fun cuentaConPropinaSinComensales() {

        propinaNumber = calcularPorcentaje()

        propina.text = propinaNumber.toString()

        totalNumber = propinaNumber + consumicionNumber

        if (isCambio) {

            totalNumber = ceil(totalNumber)

            propinaNumber = totalNumber - consumicionNumber

            propina.text = (round(propinaNumber * 100) / 100).toString()
        }

        total.text = (round(totalNumber * 100) / 100).toString()

        totalPorCNumber = round(totalNumber * 100) / 100
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generarPDF() {

        getExternalFilesDir("TipsCalculadora")?.absolutePath?.let {

            val cont = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                "<p style=\"font-size:30px; text-align: center; font-weight: bold;\">TIPS CALCULADORA</p>\n" +

                 "<p style=\"text-align: right; background: #E1E1E1; padding: 1%; padding-right: 2%\">" +
                        "Fecha: ${getFecha()} &nbsp;&nbsp; Hora: ${getHora()}</p>\n" +

                    "<table style=\"width:100%\">" +

                        "<tr><td><br></td></tr>" +
                        "<tr style=\"text-align: center; font-size:20px;\">\n" +
                        "   <td></td>" +
                        "    <td style=\"text-align: right\">Consumicion:</td>\n" +
                        "    <td style=\"text-align: right\">$ ${consumicion.text}</td>\n" +
                        "</tr>" +

                        "<tr><td><br></td></tr>" +
                        "<tr style=\"text-align: center; font-size:20px;\">\n" +
                        "   <td></td>" +
                        "    <td style=\"text-align: right\">Propina " +
                        "       ${if (propinaCheck.isChecked) porcentajeText.text else "0%"}</td>\n" +
                        "    <td style=\"text-align: right\">$ " +
                        "       ${if (propinaCheck.isChecked) propina.text else "0.0"}</td>\n" +
                        "</tr>" +
                        "<tr><td><br></td></tr>" +
                        "</table>" +

                        "<p style=\"text-align: center; background: #424874; padding: 1%; font-size:20px; color: white\">" +
                        "Comensales</p>\n" +

                        "<table style=\"width:100%\">" +

                        "<tr style=\"text-align: center; font-size:20px;\">\n" +
                        "    <td style=\"text-align: right\">Cantidad: " +
                        "       ${if (comensalesCheck.isChecked) comensalesCantidad else 1}</td>\n" +
                        "    <td style=\"text-align: right\">Total por comensal:</td>\n" +
                        "    <td style=\"text-align: right\">$ ${round(totalPorCNumber * 100) / 100}</td>\n" +
                        "</tr>" +
                        "<tr><td><br><br></td></tr>" +
                        "</table>" +

                "<p style=\"font-size:30px; text-align: center; background: #424874; padding: 2%; color: white; font-weight: bold;\">" +
                        "Total a pagar: $ ${total.text}</p>\n"
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            CreatePdf(this)
                .setPdfName("cuenta_${getFecha()}_${getHora()}")
                .openPrintDialog(false)
                .setContentBaseUrl(null)
                .setPageSize(PrintAttributes.MediaSize.ISO_A4)
                .setContent(cont)
                .setFilePath(it)
                .setCallbackListener(object : CreatePdf.PdfCallbackListener {

                    override fun onFailure(errorMsg: String) {
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(filePath: String) {

                        try {
                            val f = File(filePath)

                            val uri = FileProvider.getUriForFile(
                                this@MainActivity,
                                this@MainActivity.packageName.toString() + ".provider",
                                f
                            )

                            val share = Intent()
                            share.action = Intent.ACTION_SEND
                            share.type = "application/pdf"
                            share.putExtra(Intent.EXTRA_STREAM, uri)
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            startActivity(Intent.createChooser(share, "Compartir"))
                        }
                        catch (e: Exception) {

                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                })
                .create()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getFecha(): String {

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        return current.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHora(): String {

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        return current.format(formatter)
    }
}
