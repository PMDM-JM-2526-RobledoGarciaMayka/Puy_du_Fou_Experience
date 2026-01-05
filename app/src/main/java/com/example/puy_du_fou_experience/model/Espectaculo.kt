package com.example.puy_du_fou_experience.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "espectaculo")
data class Espectaculo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "titulo")
    var titulo: String,

    @ColumnInfo(name = "imagen")
    var imagen: Int,    //Foto de la carpeta Drawable

    @ColumnInfo(name = "descripcion")
    var descripcion: String,

    @ColumnInfo(name = "horarios")
    var horarios: String,

    @ColumnInfo(name = "duracion")
    var duracion: String,

    @ColumnInfo(name = "zona")
    var zona: String,

    @ColumnInfo(name = "precio")
    var precio: Double,

    @ColumnInfo(name = "restriccionEdad")
    var resticcionEdad: String,
)

