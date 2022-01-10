package com.example.helloandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class SignInActivity : AppCompatActivity() {
    private val TAG = "SignInActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //在用户输入时监听用户的输入
        val editEmail=findViewById<EditText>(R.id.editEmail)
        editEmail.addTextChangedListener {
            Log.d(TAG,"用户输入的邮箱地址为:${it}")
        }

        val editPwd=findViewById<EditText>(R.id.editPwd)
        editPwd.addTextChangedListener {
            Log.d(TAG,"用户输入的密码是:${it}")
        }

        //监听登录按钮
        val signIn=findViewById<Button>(R.id.signIn)
        signIn.setOnClickListener {
            //取得用户输入的邮箱和密码 并与存储的数据进行比较
            val email=editEmail.text.toString()
            val password=editPwd.text.toString()
            val user=UserCenter.getUserByEmail(email)
            Log.d(TAG,"${user?.password},${user?.email}")
            if(user!=null && user.password==password){
                //登录成功，进行页面跳转
                Toast.makeText(this,R.string.sign_in_success,Toast.LENGTH_SHORT).show()
                UserCenter.signInUser=user
                val jumpIntent=Intent(this,AlbumActivity::class.java)
                startActivity(jumpIntent)
                finish()
            } else{
                Toast.makeText(this,R.string.sign_in_fail,Toast.LENGTH_SHORT).show()
            }
        }

        //监听点击事件，进行显示跳转
        val forgetPwd=findViewById<TextView>(R.id.forgetPwd)
        forgetPwd.setOnClickListener {
            //跳转到找回密码页面
            val jumpIntent=Intent(this,ForgetPasswordActivity::class.java)
            startActivity(jumpIntent)
        }

        //通过谷歌登录
        val signwithGoogle=findViewById<Button>(R.id.signwithGoogle)
        signwithGoogle.setOnClickListener {
            //跳转到谷歌页面
            val uri = Uri.parse("https://www.google.com/account")
            val jumpIntent= Intent(Intent.ACTION_VIEW, uri)
            startActivity(jumpIntent)
        }

        val signUp=findViewById<TextView>(R.id.signUp)
        signUp.setOnClickListener {
            //跳转到注册页面
            val jumpIntent=Intent(this,SignUpActivity::class.java)
            startActivity(jumpIntent)
        }



    }


}