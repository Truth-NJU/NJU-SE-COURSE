package com.example.second_exercise

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class FragmentThree : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_three, container, false)
    }

    override fun onResume() {
        super.onResume()
        val textView= view?.findViewById<TextView>(R.id.textView3)
        //对textView3 淡入淡出
        val animator1= ObjectAnimator.ofFloat(textView, "alpha", 1f,0.3f,0.8f,1f)
        //翻转
        val animator2= ObjectAnimator.ofFloat(textView, "scaleX", 0.0f,1.0f)
        //将多个动画同时运行
        val set=AnimatorSet()
        set.playTogether(animator1,animator2)
        set.duration = 8000
        set.start()
    }
}