package com.example.helloandroid

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.widget.addTextChangedListener

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //获得用户输入的数据
        var name=""
        findViewById<EditText>(R.id.name).addTextChangedListener {
            name=it.toString()
        }

        var email=""
        findViewById<EditText>(R.id.email).addTextChangedListener{
            email=it.toString()
        }

        var password=""
        findViewById<EditText>(R.id.password).addTextChangedListener{
            password=it.toString()
        }

        var age=0
        findViewById<EditText>(R.id.age).addTextChangedListener{
            age=it.toString().toInt()
        }

        var address=""
        findViewById<EditText>(R.id.address).addTextChangedListener{
            address=it.toString()
        }

        var gender=""
        findViewById<RadioGroup>(R.id.gender).setOnCheckedChangeListener{ group ,checkedId->
            if(checkedId==R.id.genderMale){
                gender="male"
            }
            else if(checkedId==R.id.genderFemale){
                gender="female"
            }
        }

        // 取消注册则返回登录页面
        findViewById<Button>(R.id.cancel).setOnClickListener {
            val jumpIntent=Intent(this,SignInActivity::class.java)
            startActivity(jumpIntent)
        }

        findViewById<TextView>(R.id.licenceDetail).setOnClickListener {
            // 跳转到详细的用户手册界面
            val uri = Uri.parse("https://www.bytedance.com")
            val jumpIntent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(jumpIntent)
        }

        //点击确认按钮
        findViewById<Button>(R.id.confirm1).setOnClickListener {
            //用户同意用户手册
            if(findViewById<CheckBox>(R.id.licence).isChecked){
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || gender.isEmpty() || address.isEmpty()) {
                    Toast.makeText(this, R.string.signup_fail_data_invalid, Toast.LENGTH_SHORT)
                        .show()
                }
                else{
                    val user = User(name, email, password, age, gender, address)
                    UserCenter.addUser(user)
                    Log.d("SignUpActivity","${user.name},${user.email},${user.password}")
                    Toast.makeText(this, R.string.signup_success, Toast.LENGTH_SHORT).show()
                    val jumpIntent=Intent(this,SignInActivity::class.java)
                    startActivity(jumpIntent)
                }
            }
            else{
                Toast.makeText(this, R.string.signup_fail_not_agree, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}