package com.example.puy_du_fou_experience.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.puy_du_fou_experience.R
import com.example.puy_du_fou_experience.model.Espectaculo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Espectaculo::class], version = 1, exportSchema = false)
abstract class EspectaculosDataBase : RoomDatabase() {
    abstract fun EspectaculosDAO(): EspectaculosDAO

    companion object {
        @Volatile
        private var INSTANCE: EspectaculosDataBase? = null

        fun getDatabase(context: Context): EspectaculosDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EspectaculosDataBase::class.java,
                    "espectaculos_database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.EspectaculosDAO()
                                ?.insert(datosIniciales())
                        }
                    }
                }).build()

                INSTANCE = instance
                instance
            }
        }
        private fun datosIniciales(): List<Espectaculo> {
            return listOf(
                Espectaculo(
                titulo = "El Tambor de la Libertad",
                imagen = R.drawable.eltambor,
                descripcion = "Épica de la resistencia de Toledo en 1812 contra las tropas de Napoleón.",
                horarios = "21:34",
                duracion = "30 minutos aprox.",
                zona = "Al aire libre (Próximo a La Venta de Isidro)",
                precio = 10.00,
                resticcionEdad = "18 años"
                ),
                Espectaculo(
                    titulo = "El Último Cantar",
                    imagen = R.drawable.elultimocantar,
                    descripcion = "Las hazañas del Cid Campeador en un teatro con una espectacular grada giratoria.",
                    horarios = "20:00",
                    duracion = "30 minutos",
                    zona = "Interior (La Puebla Real)",
                    precio = 5.00,
                    resticcionEdad = "16 años"
                ),
                Espectaculo(
                    titulo = "A Pluma y Espada",
                    imagen = R.drawable.aplumayespada,
                    descripcion = "Aventura del Siglo de Oro con Lope de Vega, duelos y coreografías sobre agua.",
                    horarios = "18:00",
                    duracion = "30 minutos",
                    zona = "Interior (Corral de Comedias)",
                    precio = 6.00,
                    resticcionEdad = "15 años"
                ),
                Espectaculo(
                    titulo = "Cetrería de Reyes",
                    imagen = R.drawable.cetreria,
                    descripcion = "Gran exhibición aérea de aves rapaces que narra el pacto entre un califa y un conde.",
                    horarios = "19:30",
                    duracion = "30 minutos",
                    zona = "Aire libre (Cerca de El Askar Andalusí)",
                    precio = 7.50,
                    resticcionEdad = "20 años"
                ),
                Espectaculo(
                    titulo = "El Sueño de Toledo",
                    imagen = R.drawable.elsueno,
                    descripcion = "Recorrido por 1.500 años de historia sobre un escenario de 5 hectáreas.",
                    horarios = "22:00",
                    duracion = "70 - 80 minutos",
                    zona = "Escenario Nocturno (Acceso desde El Arrabal)",
                    precio = 9.50,
                    resticcionEdad = "18 años"
                ),
                Espectaculo(
                    titulo = "De Tal Palo...",
                    imagen = R.drawable.detalpalo,
                    descripcion = "Narración generacional de la historia de España a través de una familia toledana.",
                    horarios = "18:45",
                    duracion = "20 minutos",
                    zona = "Aire libre (Villanueva del Corral)",
                    precio = 5.0,
                    resticcionEdad = "15 años"
                ),
                Espectaculo(
                    titulo = "El Misterio de Sorbaces",
                    imagen = R.drawable.elmisterio,
                    descripcion = "La historia de los reyes visigodos y los tesoros de Guarrazar con caballos y efectos.",
                    horarios = "19:45",
                    duracion = "25 - 30 minutos",
                    zona = "Aire libre (Gradas de Sorbaces)",
                    precio = 6.50,
                    resticcionEdad = "16 años"
                ),
                Espectaculo(
                    titulo = "Allende la Mar Océana",
                    imagen = R.drawable.allende,
                    descripcion = "Viaje inmersivo a pie dentro de la nao Santa María junto a Cristóbal Colón.",
                    horarios = "20:45",
                    duracion = "25 minutos",
                    zona = "Inmersivo (Pasaje continuo)",
                    precio = 8.0,
                    resticcionEdad = "15 años"
                )
            )

        }

    }
}