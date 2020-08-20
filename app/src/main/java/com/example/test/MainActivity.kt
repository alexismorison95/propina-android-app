package com.example.test

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
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

            if (propinaCheck.isChecked) {

                propinaLayout.visibility = View.VISIBLE

                isPropina = true
            }
            else {
                propinaLayout.visibility = View.GONE

                isPropina = false
            }

            hacerCuentas()
        }

        comensalesCheck.setOnCheckedChangeListener { _, _ ->

            if (comensalesCheck.isChecked) {

                comensalesLayout.visibility = View.VISIBLE

                isComensales = true
            }
            else {
                comensalesLayout.visibility = View.GONE

                isComensales = false
            }

            hacerCuentas()
        }

        cambioCheck.setOnCheckedChangeListener { _, _ ->

            isCambio = cambioCheck.isChecked

            hacerCuentas()
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

                val compartir = Intent(Intent.ACTION_SEND)

                compartir.type = "text/plain"

                val mensaje = "Te recomiendo Tips Calculadora, para calcular propinas y dividir la cuenta con tus amigos. http://play.google.com/store/apps/details?id=com.google.android.apps.maps"

                compartir.putExtra(Intent.EXTRA_SUBJECT, "Tips Calculadora")

                compartir.putExtra(Intent.EXTRA_TEXT, mensaje)

                startActivity(Intent.createChooser(compartir, "Compartir vía"))

                true
            }
            else -> {

                true
            }
        }
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
    }

    private fun calcularPorcentaje(): Double {

        consumicionNumber = consumicion.text.toString().toDouble()

        propinaPorcNumber = (minP + (seekBarP.progress * stepP)).toDouble()

        return round((consumicionNumber * (propinaPorcNumber / 100.0)) * 100) / 100
    }

    private fun hacerCuentas() {

        if (!consumicion.text.isNullOrBlank()) {

            // Hay propina y no comensales

            if (isPropina && !isComensales) {

                cuentaConPropinaSinComensales()
            }
            else {
                // Hay propina y comensales

                if (isPropina && isComensales) {

                    cuentaConPropinaConComensales()
                }
                else {
                    // No hay propina pero si comensales

                    if (!isPropina && isComensales) {

                        cuentaSinPropinaConComensales()
                    }
                    else {
                        // Ninguno

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

        consumicionNumber = consumicion.text.toString().toDouble()

        totalNumber = consumicionNumber

        if (isCambio) {

            totalNumber = ceil(consumicionNumber)
        }

        total.text = (round(totalNumber * 100) / 100).toString()
    }

    private fun cuentaSinPropinaConComensales() {

        consumicionNumber = consumicion.text.toString().toDouble()

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
    }
}
