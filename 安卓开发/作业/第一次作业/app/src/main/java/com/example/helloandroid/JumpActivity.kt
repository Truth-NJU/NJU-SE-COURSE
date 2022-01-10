package com.example.helloandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class JumpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 判断当前有无用户登录，进行跳转
        if(UserCenter.signInUser==null){
            val jumpIntent=Intent(this,SignInActivity::class.java)
            startActivity(jumpIntent)
        }else{
            val jumpIntent=Intent(this,AlbumActivity::class.java)
            startActivity(jumpIntent)
        }
        finish()
    }
}