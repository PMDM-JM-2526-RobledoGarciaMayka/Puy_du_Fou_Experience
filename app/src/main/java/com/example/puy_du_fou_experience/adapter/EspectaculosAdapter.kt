package com.example.puy_du_fou_experience.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.model.Espectaculo

class EspectaculosAdapter(
    private val onItemClick: (Espectaculo) -> Unit
): RecyclerView.Adapter<EspectaculosAdapter.EspectaculoViewHolder>() {

    private var espectaculosLista: List<Espectaculo> = emptyList()

    class EspectaculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenEspectaculo: ImageView = itemView.findViewById(R.id.imgEspectaculo)
        val tituloEspectaculo: TextView = itemView.findViewById(R.id.tvNombreEspectaculo)
        val zonaEspectaculo: TextView = itemView.findViewById(R.id.tvZona)
        val duracionEspectaculo: TextView = itemView.findViewById(R.id.tvDuracion)
        val horariosEspectaculo: TextView = itemView.findViewById(R.id.tvHorarios)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EspectaculoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_espectaculo, parent, false)
        return EspectaculoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EspectaculoViewHolder, position: Int) {
        val espectaculo = espectaculosLista[position]

        holder.imagenEspectaculo.setImageResource(espectaculo.imagen)
        holder.tituloEspectaculo.text = espectaculo.titulo
        holder.zonaEspectaculo.text = espectaculo.zona
        holder.duracionEspectaculo.text = espectaculo.duracion
        holder.horariosEspectaculo.text = espectaculo.horarios

        holder.itemView.setOnClickListener {
            onItemClick(espectaculo)
        }
    }

    override fun getItemCount(): Int {
        return espectaculosLista.size
    }

    fun submitList(nuevaLista: List<Espectaculo>) {
        espectaculosLista = nuevaLista
        notifyDataSetChanged()
    }
}