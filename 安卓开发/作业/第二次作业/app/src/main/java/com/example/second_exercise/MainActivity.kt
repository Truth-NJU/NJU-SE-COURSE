package com.example.second_exercise


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var itemOne: TextView? = null
    private var itemTwo: TextView? = null
    private var itemThere: TextView? = null
    private var viewPager: ViewPager? = null
    private var list: ArrayList<Fragment>? = null
    private var adapter: TabFragmentPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初始化
        itemOne = findViewById<View>(R.id.item_one) as TextView
        itemTwo = findViewById<View>(R.id.item_two) as TextView
        itemThere = findViewById<View>(R.id.item_three) as TextView
        viewPager = findViewById<View>(R.id.viewPager) as ViewPager

        // 设置菜单栏的点击事件
        itemOne!!.setOnClickListener(this)
        itemTwo!!.setOnClickListener(this)
        itemThere!!.setOnClickListener(this)
        viewPager!!.setOnPageChangeListener(MyPagerChangeListener())

        //把Fragment添加到List集合里面
        list= ArrayList()
        list?.add(FragmentOne())
        list?.add(FragmentTwo())
        list?.add(FragmentThree())
        //设置viewPager
        adapter = TabFragmentPagerAdapter(supportFragmentManager, list)
        viewPager!!.adapter = adapter
        viewPager!!.currentItem = 0 //初始化显示第一个页面

        itemOne!!.setBackgroundColor(Color.RED) //被选中就为红色
    }


    /**
     * 点击事件
     */
    override fun onClick(v: View) {
        //判断当前点击哪一个菜单
        when (v.id) {
            R.id.item_one -> {
                viewPager!!.currentItem = 0
                itemOne!!.setBackgroundColor(Color.RED)
                itemTwo!!.setBackgroundColor(Color.WHITE)
                itemThere!!.setBackgroundColor(Color.WHITE)
            }
            R.id.item_two -> {
                viewPager!!.currentItem = 1
                itemOne!!.setBackgroundColor(Color.WHITE)
                itemTwo!!.setBackgroundColor(Color.RED)
                itemThere!!.setBackgroundColor(Color.WHITE)
            }
            R.id.item_three -> {
                viewPager!!.currentItem = 2
                itemOne!!.setBackgroundColor(Color.WHITE)
                itemTwo!!.setBackgroundColor(Color.WHITE)
                itemThere!!.setBackgroundColor(Color.RED)
            }
        }
    }

    /**
     * 设置一个ViewPager的侦听事件，当左右滑动ViewPager时菜单栏被选中状态跟着改变
     */
    inner class MyPagerChangeListener : OnPageChangeListener {
        override fun onPageScrollStateChanged(arg0: Int) {}
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        // 判断当前滑倒了哪一个页面
        override fun onPageSelected(arg0: Int) {
            when (arg0) {
                0 -> {
                    itemOne!!.setBackgroundColor(Color.RED)
                    itemTwo!!.setBackgroundColor(Color.WHITE)
                    itemThere!!.setBackgroundColor(Color.WHITE)
                }
                1 -> {
                    itemOne!!.setBackgroundColor(Color.WHITE)
                    itemTwo!!.setBackgroundColor(Color.RED)
                    itemThere!!.setBackgroundColor(Color.WHITE)
                }
                2 -> {
                    itemOne!!.setBackgroundColor(Color.WHITE)
                    itemTwo!!.setBackgroundColor(Color.WHITE)
                    itemThere!!.setBackgroundColor(Color.RED)
                }
            }
        }
    }
}