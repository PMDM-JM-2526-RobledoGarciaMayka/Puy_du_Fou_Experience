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

    // Lista de espectáculos que se mostrará en el RecyclerView
    private var espectaculosLista: List<Espectaculo> = emptyList()

    //ViewHolder que contiene las referencias a las vistas de cada item de espectáculo.
    class EspectaculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenEspectaculo: ImageView = itemView.findViewById(R.id.imgEspectaculo)
        val tituloEspectaculo: TextView = itemView.findViewById(R.id.tvNombreEspectaculo)
        val zonaEspectaculo: TextView = itemView.findViewById(R.id.tvZona)
        val duracionEspectaculo: TextView = itemView.findViewById(R.id.tvDuracion)
        val horariosEspectaculo: TextView = itemView.findViewById(R.id.tvHorarios)
    }

    //Crea un nuevo ViewHolder inflando el layout del item.
    //Se llama cuando el RecyclerView necesita un nuevo ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EspectaculoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_espectaculo, parent, false)
        return EspectaculoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EspectaculoViewHolder, position: Int) {
        val espectaculo = espectaculosLista[position]

        // Asigna los datos del espectáculo a las vistas correspondientes
        holder.imagenEspectaculo.setImageResource(espectaculo.imagen)
        holder.tituloEspectaculo.text = espectaculo.titulo
        holder.zonaEspectaculo.text = espectaculo.zona
        holder.duracionEspectaculo.text = espectaculo.duracion
        holder.horariosEspectaculo.text = espectaculo.horarios

        // Configura el click listener para navegar a los detalles del espectáculo
        holder.itemView.setOnClickListener {
            onItemClick(espectaculo)
        }
    }

    //Devuelve el número total de items en la lista.
    override fun getItemCount(): Int {
        return espectaculosLista.size
    }

    //Actualiza la lista de espectáculos y notifica al RecyclerView para que se redibuje.
    fun submitList(nuevaLista: List<Espectaculo>) {
        espectaculosLista = nuevaLista
        notifyDataSetChanged()
    }
}