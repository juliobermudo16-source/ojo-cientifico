package com.ojocientifico.app.data.seed

import com.ojocientifico.app.domain.model.ClaveIlustracion
import com.ojocientifico.app.domain.model.Habitat
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.domain.model.ReinoMuestra

/**
 * Las 16 muestras del laboratorio de Ojo Científico.
 *
 * Cada muestra guarda sus características morfológicas verdaderas ([Muestra.rasgos])
 * y un conjunto de distractores plausibles. La evaluación de toda la aplicación
 * se apoya en estos datos: no hay respuestas escritas dentro de las actividades.
 */
object MuestrasSemilla {

    val todas: List<Muestra> = listOf(

        Muestra(
            id = "hoja_helecho",
            nombre = "Hoja de helecho",
            nombreCientifico = "Dryopteris filix-mas",
            reino = ReinoMuestra.PLANTA,
            habitat = Habitat.BOSQUE,
            ilustracion = ClaveIlustracion.HOJA_HELECHO,
            descripcion = "Una hoja dividida en muchas hojitas pequeñas colocadas a lo largo de un tallo central.",
            datoCurioso = "Los helechos ya crecían en la Tierra mucho antes que los dinosaurios, y no tienen flores ni semillas: se reproducen con esporas.",
            nivelRequerido = 1,
            rasgos = setOf(
                "forma_ramificada", "forma_alargada",
                "color_verde",
                "textura_lisa", "textura_mate",
                "simetria_bilateral",
                "tamano_mediano",
                "parte_tallo", "parte_hojas", "parte_nervaduras",
                "estructura_ramificada",
                "numero_muchos"
            ),
            distractores = setOf(
                "forma_espiral", "forma_poligonal",
                "color_rojo", "color_plateado",
                "textura_espinosa", "textura_humeda",
                "simetria_radial",
                "tamano_microscopico", "tamano_grande",
                "parte_patas", "parte_petalos", "parte_concha",
                "estructura_cristalina", "estructura_red",
                "numero_2", "numero_6"
            )
        ),

        Muestra(
            id = "mariposa",
            nombre = "Mariposa monarca",
            nombreCientifico = "Danaus plexippus",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.CIELO,
            ilustracion = ClaveIlustracion.MARIPOSA,
            descripcion = "Insecto de alas anchas y planas, cubiertas de escamas diminutas de color naranja con venas negras.",
            datoCurioso = "Las monarcas viajan miles de kilómetros cada año. Ningún individuo hace el viaje entero: lo terminan sus nietos.",
            nivelRequerido = 1,
            rasgos = setOf(
                "forma_aplanada", "forma_triangular",
                "color_naranja", "color_negro", "color_blanco",
                "textura_aterciopelada", "textura_mate",
                "simetria_bilateral",
                "tamano_pequeno",
                "parte_alas", "parte_antenas", "parte_patas", "parte_ojos", "parte_nervaduras",
                "estructura_segmentada",
                "numero_6",
                "rasgo_vuela", "rasgo_colores_aviso"
            ),
            distractores = setOf(
                "forma_espiral", "forma_redondeada",
                "color_azul", "color_verde",
                "textura_escamosa", "textura_dura",
                "simetria_radial",
                "tamano_grande", "tamano_microscopico",
                "parte_concha", "parte_aletas", "parte_raices",
                "estructura_cristalina", "estructura_hueca",
                "numero_8", "numero_4",
                "rasgo_vive_en_agua", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "caracol",
            nombre = "Caracol de jardín",
            nombreCientifico = "Cornu aspersum",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.JARDIN,
            ilustracion = ClaveIlustracion.CARACOL,
            descripcion = "Animal de cuerpo blando y húmedo que lleva encima una concha enrollada en espiral.",
            datoCurioso = "La concha crece con el caracol: cada vuelta nueva es más ancha que la anterior, como los anillos de un árbol.",
            nivelRequerido = 1,
            rasgos = setOf(
                "forma_espiral", "forma_redondeada",
                "color_marron", "color_gris",
                "textura_humeda", "textura_dura",
                "simetria_espiral",
                "tamano_pequeno",
                "parte_concha", "parte_tentaculos", "parte_ojos",
                "estructura_capas",
                "numero_2",
                "rasgo_crece_por_capas"
            ),
            distractores = setOf(
                "forma_estrellada", "forma_triangular",
                "color_verde", "color_amarillo",
                "textura_espinosa", "textura_sedosa",
                "simetria_radial",
                "tamano_grande", "tamano_microscopico",
                "parte_alas", "parte_patas", "parte_plumas",
                "estructura_cristalina", "estructura_filamentos",
                "numero_6", "numero_muchos",
                "rasgo_vuela", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "hormiga",
            nombre = "Hormiga obrera",
            nombreCientifico = "Formica rufa",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.SUELO,
            ilustracion = ClaveIlustracion.HORMIGA,
            descripcion = "Insecto muy pequeño con el cuerpo dividido en tres partes bien visibles y seis patas.",
            datoCurioso = "Una hormiga puede cargar objetos que pesan hasta cincuenta veces su propio peso.",
            nivelRequerido = 1,
            rasgos = setOf(
                "forma_alargada", "forma_ovalada",
                "color_marron", "color_negro",
                "textura_brillante", "textura_dura",
                "simetria_bilateral",
                "tamano_muy_pequeno",
                "parte_patas", "parte_antenas", "parte_cabeza", "parte_ojos", "parte_pinzas",
                "estructura_segmentada",
                "numero_6"
            ),
            distractores = setOf(
                "forma_espiral", "forma_estrellada",
                "color_azul", "color_blanco",
                "textura_humeda", "textura_sedosa",
                "simetria_radial",
                "tamano_mediano", "tamano_grande",
                "parte_alas", "parte_concha", "parte_aletas",
                "estructura_cristalina", "estructura_red",
                "numero_8", "numero_4",
                "rasgo_vive_en_agua"
            )
        ),

        Muestra(
            id = "pez_payaso",
            nombre = "Pez payaso",
            nombreCientifico = "Amphiprion ocellaris",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.OCEANO,
            ilustracion = ClaveIlustracion.PEZ,
            descripcion = "Pez pequeño de cuerpo ovalado, con franjas blancas sobre un fondo naranja intenso.",
            datoCurioso = "Vive entre los tentáculos venenosos de las anémonas: una capa de moco especial lo protege de las picaduras.",
            nivelRequerido = 1,
            rasgos = setOf(
                "forma_ovalada", "forma_aplanada",
                "color_naranja", "color_blanco", "color_negro",
                "textura_escamosa", "textura_lisa",
                "simetria_bilateral",
                "tamano_pequeno",
                "parte_aletas", "parte_escamas", "parte_ojos", "parte_cola", "parte_cabeza",
                "estructura_maciza",
                "rasgo_vive_en_agua", "rasgo_colores_aviso"
            ),
            distractores = setOf(
                "forma_espiral", "forma_estrellada",
                "color_verde", "color_morado",
                "textura_espinosa", "textura_aterciopelada",
                "simetria_radial",
                "tamano_microscopico", "tamano_grande",
                "parte_patas", "parte_antenas", "parte_raices",
                "estructura_cristalina", "estructura_filamentos",
                "numero_6", "numero_muchos",
                "rasgo_vuela"
            )
        ),

        Muestra(
            id = "girasol",
            nombre = "Flor de girasol",
            nombreCientifico = "Helianthus annuus",
            reino = ReinoMuestra.PLANTA,
            habitat = Habitat.JARDIN,
            ilustracion = ClaveIlustracion.GIRASOL,
            descripcion = "Flor grande y redonda, con pétalos amarillos alrededor de un centro oscuro lleno de semillas.",
            datoCurioso = "El centro no es una sola flor: son cientos de flores diminutas ordenadas en espirales perfectas.",
            nivelRequerido = 1,
            rasgos = setOf(
                "forma_redondeada", "forma_estrellada",
                "color_amarillo", "color_marron", "color_verde",
                "textura_aterciopelada", "textura_mate",
                "simetria_radial",
                "tamano_grande",
                "parte_petalos", "parte_tallo", "parte_hojas", "parte_semillas",
                "estructura_maciza",
                "numero_muchos"
            ),
            distractores = setOf(
                "forma_espiral", "forma_alargada",
                "color_azul", "color_negro",
                "textura_escamosa", "textura_dura",
                "simetria_bilateral",
                "tamano_microscopico", "tamano_muy_pequeno",
                "parte_patas", "parte_alas", "parte_concha",
                "estructura_cristalina", "estructura_red",
                "numero_2", "numero_5",
                "rasgo_vuela", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "semilla_viento",
            nombre = "Semilla de diente de león",
            nombreCientifico = "Taraxacum officinale",
            reino = ReinoMuestra.PLANTA,
            habitat = Habitat.CIELO,
            ilustracion = ClaveIlustracion.SEMILLA_VIENTO,
            descripcion = "Semilla diminuta unida a una corona de pelos finos que funciona como un paracaídas.",
            datoCurioso = "Ese paracaídas puede mantenerla en el aire más de un kilómetro antes de tocar el suelo.",
            nivelRequerido = 2,
            rasgos = setOf(
                "forma_redondeada", "forma_alargada",
                "color_blanco", "color_marron",
                "textura_sedosa",
                "simetria_radial",
                "tamano_muy_pequeno",
                "parte_semillas",
                "estructura_filamentos",
                "numero_muchos",
                "rasgo_se_dispersa_con_viento"
            ),
            distractores = setOf(
                "forma_espiral", "forma_poligonal",
                "color_verde", "color_rojo",
                "textura_dura", "textura_rugosa",
                "simetria_bilateral",
                "tamano_grande", "tamano_mediano",
                "parte_patas", "parte_concha", "parte_ojos",
                "estructura_cristalina", "estructura_maciza",
                "numero_4", "numero_2",
                "rasgo_vive_en_agua", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "pluma",
            nombre = "Pluma de ave",
            nombreCientifico = "Pluma pennácea",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.CIELO,
            ilustracion = ClaveIlustracion.PLUMA,
            descripcion = "Estructura ligera formada por un eje central del que salen cientos de barbas finas a los dos lados.",
            datoCurioso = "Las barbas se enganchan entre sí con ganchos microscópicos: por eso una pluma despeinada se vuelve a alisar.",
            nivelRequerido = 2,
            rasgos = setOf(
                "forma_alargada", "forma_aplanada",
                "color_marron", "color_blanco", "color_gris",
                "textura_sedosa", "textura_lisa",
                "simetria_bilateral",
                "tamano_pequeno",
                "parte_nervaduras",
                "estructura_filamentos", "estructura_ramificada",
                "numero_muchos",
                "rasgo_vuela"
            ),
            distractores = setOf(
                "forma_espiral", "forma_redondeada",
                "color_azul", "color_naranja",
                "textura_dura", "textura_humeda",
                "simetria_radial",
                "tamano_grande", "tamano_microscopico",
                "parte_patas", "parte_ojos", "parte_concha",
                "estructura_cristalina", "estructura_capas",
                "numero_5", "numero_6",
                "rasgo_vive_en_agua"
            )
        ),

        Muestra(
            id = "estrella_mar",
            nombre = "Estrella de mar",
            nombreCientifico = "Asterias rubens",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.OCEANO,
            ilustracion = ClaveIlustracion.ESTRELLA_MAR,
            descripcion = "Animal marino con cinco brazos iguales que salen de un disco central.",
            datoCurioso = "No tiene cerebro y camina con cientos de pies diminutos llenos de agua. Si pierde un brazo, le crece otro.",
            nivelRequerido = 2,
            rasgos = setOf(
                "forma_estrellada", "forma_aplanada",
                "color_naranja", "color_rojo",
                "textura_rugosa", "textura_granulada",
                "simetria_radial",
                "tamano_mediano",
                "parte_tentaculos",
                "estructura_maciza",
                "numero_5",
                "rasgo_vive_en_agua"
            ),
            distractores = setOf(
                "forma_espiral", "forma_ovalada",
                "color_azul", "color_blanco",
                "textura_sedosa", "textura_brillante",
                "simetria_bilateral",
                "tamano_microscopico", "tamano_grande",
                "parte_alas", "parte_antenas", "parte_escamas",
                "estructura_red", "estructura_filamentos",
                "numero_6", "numero_8",
                "rasgo_vuela", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "escarabajo",
            nombre = "Escarabajo joya",
            nombreCientifico = "Chrysolina fastuosa",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.BOSQUE,
            ilustracion = ClaveIlustracion.ESCARABAJO,
            descripcion = "Insecto de cuerpo ovalado cubierto por un caparazón duro y brillante con reflejos metálicos.",
            datoCurioso = "Su color no viene de un pigmento: son capas microscópicas que rompen la luz, como una pompa de jabón.",
            nivelRequerido = 2,
            rasgos = setOf(
                "forma_ovalada", "forma_redondeada",
                "color_verde", "color_plateado", "color_morado",
                "textura_brillante", "textura_dura",
                "simetria_bilateral",
                "tamano_muy_pequeno",
                "parte_caparazon", "parte_patas", "parte_antenas", "parte_ojos", "parte_cabeza",
                "estructura_segmentada",
                "numero_6"
            ),
            distractores = setOf(
                "forma_espiral", "forma_estrellada",
                "color_blanco", "color_amarillo",
                "textura_sedosa", "textura_humeda",
                "simetria_radial",
                "tamano_grande", "tamano_mediano",
                "parte_aletas", "parte_concha", "parte_petalos",
                "estructura_filamentos", "estructura_cristalina",
                "numero_8", "numero_5",
                "rasgo_vive_en_agua", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "seta",
            nombre = "Seta del bosque",
            nombreCientifico = "Amanita muscaria",
            reino = ReinoMuestra.HONGO,
            habitat = Habitat.BOSQUE,
            ilustracion = ClaveIlustracion.SETA,
            descripcion = "Hongo con un sombrero rojo salpicado de motas blancas sostenido por un pie blanco.",
            datoCurioso = "Lo que ves es solo la parte que fabrica esporas: bajo tierra hay una red de hilos que puede ocupar metros.",
            nivelRequerido = 2,
            rasgos = setOf(
                "forma_redondeada", "forma_conica",
                "color_rojo", "color_blanco",
                "textura_lisa", "textura_humeda",
                "simetria_radial",
                "tamano_pequeno",
                "parte_sombrero", "parte_laminillas", "parte_tallo",
                "estructura_maciza",
                "numero_muchos",
                "rasgo_colores_aviso"
            ),
            distractores = setOf(
                "forma_espiral", "forma_alargada",
                "color_azul", "color_plateado",
                "textura_escamosa", "textura_dura",
                "simetria_bilateral",
                "tamano_microscopico", "tamano_grande",
                "parte_patas", "parte_alas", "parte_raices",
                "estructura_cristalina", "estructura_red",
                "numero_2", "numero_6",
                "rasgo_vuela", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "cristal_cuarzo",
            nombre = "Cristal de cuarzo",
            nombreCientifico = "Dióxido de silicio (SiO₂)",
            reino = ReinoMuestra.MINERAL,
            habitat = Habitat.SUELO,
            ilustracion = ClaveIlustracion.CRISTAL,
            descripcion = "Prisma de seis caras planas rematado en punta, transparente o lechoso.",
            datoCurioso = "Sus átomos se ordenan siempre igual: por eso todos los cristales de cuarzo tienen seis caras, midan lo que midan.",
            nivelRequerido = 2,
            rasgos = setOf(
                "forma_poligonal", "forma_conica", "forma_alargada",
                "color_translucido", "color_blanco",
                "textura_lisa", "textura_dura", "textura_brillante",
                "simetria_radial",
                "tamano_pequeno",
                "estructura_cristalina", "estructura_maciza",
                "numero_6",
                "rasgo_no_es_ser_vivo"
            ),
            distractores = setOf(
                "forma_espiral", "forma_acorazonada",
                "color_verde", "color_naranja",
                "textura_humeda", "textura_aterciopelada",
                "simetria_bilateral",
                "tamano_microscopico", "tamano_grande",
                "parte_patas", "parte_hojas", "parte_ojos",
                "estructura_filamentos", "estructura_segmentada",
                "numero_muchos", "numero_2",
                "rasgo_vuela", "rasgo_vive_en_agua"
            )
        ),

        Muestra(
            id = "telarana",
            nombre = "Telaraña orbicular",
            nombreCientifico = "Araneus diadematus",
            reino = ReinoMuestra.CONSTRUCCION,
            habitat = Habitat.JARDIN,
            ilustracion = ClaveIlustracion.TELARANA,
            descripcion = "Trampa de hilos: radios que salen del centro y una espiral pegajosa que los cruza.",
            datoCurioso = "El hilo de seda es más resistente que un hilo de acero del mismo grosor, y la araña rehace la tela casi cada día.",
            nivelRequerido = 3,
            rasgos = setOf(
                "forma_redondeada", "forma_espiral",
                "color_blanco", "color_plateado",
                "textura_sedosa",
                "simetria_radial",
                "tamano_mediano",
                "estructura_red", "estructura_filamentos",
                "numero_muchos",
                "rasgo_construido_por_animal"
            ),
            distractores = setOf(
                "forma_acorazonada", "forma_poligonal",
                "color_verde", "color_marron",
                "textura_dura", "textura_rugosa",
                "simetria_bilateral",
                "tamano_microscopico", "tamano_grande",
                "parte_patas", "parte_ojos", "parte_alas",
                "estructura_cristalina", "estructura_maciza",
                "numero_5", "numero_8",
                "rasgo_vuela", "rasgo_vive_en_agua"
            )
        ),

        Muestra(
            id = "copo_nieve",
            nombre = "Copo de nieve",
            nombreCientifico = "Cristal de hielo hexagonal",
            reino = ReinoMuestra.MINERAL,
            habitat = Habitat.CIELO,
            ilustracion = ClaveIlustracion.COPO_NIEVE,
            descripcion = "Cristal de hielo con seis brazos que se ramifican de forma idéntica alrededor del centro.",
            datoCurioso = "Siempre tiene seis brazos porque así se unen las moléculas de agua al congelarse, y cada copo se ramifica distinto.",
            nivelRequerido = 3,
            rasgos = setOf(
                "forma_estrellada", "forma_ramificada",
                "color_blanco", "color_translucido",
                "textura_lisa", "textura_brillante",
                "simetria_radial",
                "tamano_muy_pequeno",
                "estructura_cristalina", "estructura_ramificada",
                "numero_6",
                "rasgo_no_es_ser_vivo"
            ),
            distractores = setOf(
                "forma_espiral", "forma_ovalada",
                "color_marron", "color_amarillo",
                "textura_rugosa", "textura_aterciopelada",
                "simetria_bilateral",
                "tamano_grande", "tamano_mediano",
                "parte_patas", "parte_hojas", "parte_tentaculos",
                "estructura_red", "estructura_segmentada",
                "numero_5", "numero_muchos",
                "rasgo_vive_en_agua", "rasgo_camuflaje"
            )
        ),

        Muestra(
            id = "celula_vegetal",
            nombre = "Célula vegetal",
            nombreCientifico = "Elodea canadensis (célula foliar)",
            reino = ReinoMuestra.MICROSCOPICO,
            habitat = Habitat.LABORATORIO,
            ilustracion = ClaveIlustracion.CELULA_VEGETAL,
            descripcion = "Vista al microscopio: celdas de paredes rectas con puntos verdes moviéndose dentro.",
            datoCurioso = "Los puntos verdes son cloroplastos y giran dentro de la célula siguiendo una corriente interna.",
            nivelRequerido = 3,
            rasgos = setOf(
                "forma_poligonal", "forma_alargada",
                "color_verde", "color_translucido",
                "textura_lisa",
                "simetria_ausente",
                "tamano_microscopico",
                "parte_nucleo", "parte_membrana",
                "estructura_celular",
                "numero_muchos"
            ),
            distractores = setOf(
                "forma_espiral", "forma_estrellada",
                "color_naranja", "color_negro",
                "textura_espinosa", "textura_rugosa",
                "simetria_radial", "simetria_bilateral",
                "tamano_grande", "tamano_pequeno",
                "parte_patas", "parte_alas", "parte_concha",
                "estructura_cristalina", "estructura_red",
                "numero_2", "numero_6",
                "rasgo_vuela", "rasgo_no_es_ser_vivo"
            )
        ),

        Muestra(
            id = "rana_arboricola",
            nombre = "Rana arborícola",
            nombreCientifico = "Agalychnis callidryas",
            reino = ReinoMuestra.ANIMAL,
            habitat = Habitat.RIO,
            ilustracion = ClaveIlustracion.RANA,
            descripcion = "Anfibio de piel verde y lisa, con ojos rojos muy grandes y costados azules.",
            datoCurioso = "Duerme tapando sus colores vivos. Si la molestan, los enseña de golpe para asustar al depredador y escapar.",
            nivelRequerido = 3,
            rasgos = setOf(
                "forma_ovalada", "forma_redondeada",
                "color_verde", "color_rojo", "color_azul",
                "textura_lisa", "textura_humeda",
                "simetria_bilateral",
                "tamano_pequeno",
                "parte_patas", "parte_ojos", "parte_cabeza",
                "estructura_maciza",
                "numero_4",
                "rasgo_camuflaje", "rasgo_colores_aviso"
            ),
            distractores = setOf(
                "forma_espiral", "forma_poligonal",
                "color_plateado", "color_marron",
                "textura_escamosa", "textura_dura",
                "simetria_radial",
                "tamano_grande", "tamano_microscopico",
                "parte_alas", "parte_concha", "parte_plumas",
                "estructura_cristalina", "estructura_filamentos",
                "numero_6", "numero_8",
                "rasgo_no_es_ser_vivo", "rasgo_vuela"
            )
        )
    )

    private val indice = todas.associateBy { it.id }

    fun porId(id: String): Muestra? = indice[id]

    fun requerir(id: String): Muestra = indice[id] ?: error("Muestra desconocida: $id")

    fun deNivel(nivel: Int): List<Muestra> = todas.filter { it.nivelRequerido <= nivel }
}
