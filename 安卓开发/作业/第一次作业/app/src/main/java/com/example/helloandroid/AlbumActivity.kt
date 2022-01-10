package com.example.helloandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

//相册页面
class AlbumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        var index=0
        val imgList= mutableListOf(
            R.drawable.scene0,
            R.drawable.scene1,
            R.drawable.scene2,
            R.drawable.scene3
        )
        val album=findViewById<ImageView>(R.id.album)
        album.setImageResource(imgList[index])


        //查看前一张照片
        findViewById<Button>(R.id.prev).setOnClickListener {
        //需要判断是否还有前一张照片
            if(index==0){
                Toast.makeText(this, R.string.first_img, Toast.LENGTH_SHORT)
                    .show()
            }else{
                index--
            }
            album.setImageResource(imgList[index])
        }

        //查看后一张照片
        findViewById<Button>(R.id.next).setOnClickListener {
        //需要判断是否还有后一张照片
            if(index==imgList.size-1){
                Toast.makeText(this, R.string.last_img, Toast.LENGTH_SHORT)
                    .show()
            }else{
                index++
            }
            album.setImageResource(imgList[index])
        }
    }

}