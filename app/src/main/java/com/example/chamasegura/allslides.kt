package com.example.chamasegura

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        // Aqui você pode configurar o conteúdo dos slides se necessário
    }

    override fun getItemCount() = slideLayouts.size

    override fun getItemViewType(position: Int): Int {
        return slideLayouts[position]
    }

    inner class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Configure as views dos slides se necessário
    }

}