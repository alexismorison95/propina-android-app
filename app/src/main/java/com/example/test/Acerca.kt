package com.example.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class Acerca : AppCompatActivity() {

    private lateinit var btnGithub: ImageButton
    private lateinit var btnMail: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acerca)

        btnGithub = findViewById(R.id.btnGithub)
        btnMail = findViewById(R.id.btnMail)

        btnGithub.setOnClickListener {

            val uri = Uri.parse("https://github.com/alexismorison95/propina-android-app")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        btnMail.setOnClickListener {

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, listOf("alexismorison83@gmail.com").toTypedArray())
            intent.putExtra(Intent.EXTRA_SUBJECT, "TIPS CALCULADORA")
            intent.putExtra(Intent.EXTRA_TEXT, "Tu mensaje:")
            intent.type = "message/rfc822"

            startActivity(Intent.createChooser(intent, "Enviar"));
        }
    }
}