package com.example.second_exercise

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class FragmentTwo : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two, container, false)
    }

    override fun onResume() {
        super.onResume()
        //旋转
        val textView=view?.findViewById<TextView>(R.id.textView2)
        val animator=ObjectAnimator.ofFloat(textView,"rotation",0f,360f)
        animator.duration = 10000
        animator.start()
    }

}