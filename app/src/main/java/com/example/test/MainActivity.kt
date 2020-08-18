package com.example.test

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    private lateinit var seekBarP: SeekBar;
    private lateinit var porcentajeText: TextView;
    private lateinit var consumicion: TextInputEditText;
    private lateinit var propina: TextInputEditText;
    private lateinit var total: TextInputEditText;
    private lateinit var propinaCheck: CheckBox;
    private lateinit var propinaLayout: LinearLayout
    private lateinit var comensalesCheck: CheckBox
    private lateinit var comensalesLayout: LinearLayout
    private lateinit var seekBarC: SeekBar
    private lateinit var comensalesText: TextView
    private lateinit var comensalesTotal: TextInputEditText

    var minP = 10;
    var maxP = 100;
    var stepP = 5;

    var minC = 1;
    var maxC = 15;
    var stepC = 1;

    var consumicionNumber: Double = 0.0
    var propinaPorcNumber: Double = 0.0
    var propinaNumber: Double = 0.0
    var totalNumber: Double = 0.0

    var isPropina: Boolean = false
    var isComensales: Boolean = false

    var totalPorCNumber: Double = 0.0

    var comensalesCantidad: Int = minC


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

                hacerCuentas()
            }
            else {
                propinaLayout.visibility = View.GONE

                isPropina = false

                hacerCuentas()
            }
        }

        comensalesCheck.setOnCheckedChangeListener { _, _ ->

            if (comensalesCheck.isChecked) {

                comensalesLayout.visibility = View.VISIBLE

                isComensales = true

                hacerCuentas()
            }
            else {
                comensalesLayout.visibility = View.GONE

                isComensales = false

                hacerCuentas()
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

                propinaNumber = calcularPorcentaje()

                propina.setText(propinaNumber.toString())

                totalNumber = propinaNumber + consumicionNumber

                total.setText(totalNumber.toString())
            }
            else {

                // Hay propina y comensales

                if (isPropina && isComensales) {

                    propinaNumber = calcularPorcentaje()

                    propina.setText(propinaNumber.toString())

                    totalNumber = propinaNumber + consumicionNumber

                    total.setText(totalNumber.toString())

                    totalPorCNumber = totalNumber / comensalesCantidad

                    comensalesTotal.setText(totalPorCNumber.toString())
                }
                else {

                    // No hay propina pero si comensales

                    if (isComensales && !isPropina) {

                        consumicionNumber = consumicion.text.toString().toDouble()

                        totalPorCNumber = consumicionNumber / comensalesCantidad

                        comensalesTotal.setText(totalPorCNumber.toString())

                        total.setText(consumicion.text.toString())
                    }
                    else {

                        // Ninguno

                        total.setText(consumicion.text.toString())
                    }
                }
            }
        }
        else {
            propina.setText("0.0")

            total.setText("0.0")

            propinaNumber = 0.0

            consumicionNumber = 0.0

            totalNumber = 0.0
        }
    }
}
