package com.example.chamasegura

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class allslides : RecyclerView.Adapter<allslides.IntroViewHolder>(){

    private val slideLayouts = listOf(
        R.layout.activity_slide1,
        R.layout.activity_slide2,
        R.layout.activity_slide3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return IntroViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        if (position == 2) { // O terceiro slide (índice 2)
            holder.itemView.findViewById<Button>(R.id.button1).setOnClickListener {
                val context = it.context
                val intent = Intent(context, Login::class.java)
                context.startActivity(intent)
            }
            holder.itemView.findViewById<Button>(R.id.button2).setOnClickListener {
                val context = it.context
                val intent = Intent(context, Register::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = slideLayouts.size

    override fun getItemViewType(position: Int): Int {
        return slideLayouts[position]
    }

    inner class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Configure as views dos slides se necessário
    }
}
