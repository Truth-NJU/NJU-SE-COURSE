package com.example.helloandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ForgetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        findViewById<Button>(R.id.cancel1).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.confirm).setOnClickListener {
            val name = findViewById<EditText>(R.id.name).text.toString()
            val email = findViewById<EditText>(R.id.email1).text.toString()
            val user = UserCenter.getUserByEmail(email)
            if (user == null) {
                Toast.makeText(this, R.string.forget_pass_no_user, Toast.LENGTH_SHORT).show()
            } else if (user.name != name) {
                Toast.makeText(this, R.string.forget_pass_name_not_match, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "${getString(R.string.forget_pass_result)}${user.password}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}