package com.ojocientifico.app.domain.model

/**
 * Categorías de características morfológicas que un observador puede registrar.
 * Cada categoría agrupa las opciones concretas del [CatalogoRasgos].
 */
enum class CategoriaRasgo(
    val etiqueta: String,
    val pregunta: String,
    val simbolo: String
) {
    FORMA("Forma", "¿Qué forma general tiene?", "◆"),
    COLOR("Color", "¿Qué colores observas?", "●"),
    TEXTURA("Textura", "¿Cómo se vería o se sentiría su superficie?", "≈"),
    SIMETRIA("Simetría", "¿Cómo se reparten sus partes?", "⋈"),
    TAMANO("Tamaño", "¿Qué tamaño tiene comparado con tu mano?", "↔"),
    PARTES("Partes visibles", "¿Qué partes puedes distinguir?", "⚙"),
    ESTRUCTURA("Estructura", "¿Cómo está construida por dentro o por fuera?", "▤"),
    NUMERO("Número", "¿Cuántos elementos iguales cuentas?", "#"),
    PARTICULAR("Rasgo particular", "¿Qué tiene de especial esta muestra?", "★");

    companion object {
        /** Categorías disponibles según el rango del explorador. */
        fun paraNivel(nivel: Int): List<CategoriaRasgo> = when {
            nivel <= 1 -> listOf(FORMA, COLOR, TAMANO, PARTES)
            nivel == 2 -> listOf(FORMA, COLOR, TEXTURA, SIMETRIA, TAMANO, PARTES)
            else -> entries.toList()
        }
    }
}

/**
 * Una característica observable concreta. El identificador es estable y se usa
 * tanto en la base de datos como en la evaluación.
 */
data class OpcionRasgo(
    val id: String,
    val categoria: CategoriaRasgo,
    val etiqueta: String,
    val pista: String
)

/**
 * Catálogo único de características morfológicas de Ojo Científico.
 *
 * Es la fuente de verdad del contenido: la base de datos se siembra a partir de
 * aquí y toda la lógica de dominio lo utiliza sin depender de Android.
 */
object CatalogoRasgos {

    private fun r(id: String, cat: CategoriaRasgo, etiqueta: String, pista: String) =
        OpcionRasgo(id, cat, etiqueta, pista)

    val todas: List<OpcionRasgo> = listOf(
        // ---------------------------- FORMA ----------------------------
        r("forma_alargada", CategoriaRasgo.FORMA, "Alargada", "Es mucho más larga que ancha."),
        r("forma_redondeada", CategoriaRasgo.FORMA, "Redondeada", "Su contorno se parece a un círculo."),
        r("forma_ovalada", CategoriaRasgo.FORMA, "Ovalada", "Como un huevo: redonda pero estirada."),
        r("forma_ramificada", CategoriaRasgo.FORMA, "Ramificada", "Se divide en brazos o ramas."),
        r("forma_espiral", CategoriaRasgo.FORMA, "Espiral", "Gira sobre sí misma hacia el centro."),
        r("forma_estrellada", CategoriaRasgo.FORMA, "Estrellada", "Tiene puntas que salen del centro."),
        r("forma_aplanada", CategoriaRasgo.FORMA, "Aplanada", "Es delgada, casi como una lámina."),
        r("forma_acorazonada", CategoriaRasgo.FORMA, "Acorazonada", "La base tiene una entrada, como un corazón."),
        r("forma_triangular", CategoriaRasgo.FORMA, "Triangular", "Se estrecha hacia una punta."),
        r("forma_irregular", CategoriaRasgo.FORMA, "Irregular", "No se parece a ninguna figura conocida."),
        r("forma_poligonal", CategoriaRasgo.FORMA, "Poligonal", "Su borde está hecho de lados rectos."),
        r("forma_conica", CategoriaRasgo.FORMA, "Cónica", "Ancha abajo y puntiaguda arriba."),

        // ---------------------------- COLOR ----------------------------
        r("color_verde", CategoriaRasgo.COLOR, "Verde", "El color de las hojas con clorofila."),
        r("color_marron", CategoriaRasgo.COLOR, "Marrón", "Tonos de tierra o de madera."),
        r("color_amarillo", CategoriaRasgo.COLOR, "Amarillo", "Un color claro y brillante."),
        r("color_naranja", CategoriaRasgo.COLOR, "Naranja", "Entre el amarillo y el rojo."),
        r("color_rojo", CategoriaRasgo.COLOR, "Rojo", "Un color intenso, a veces de aviso."),
        r("color_azul", CategoriaRasgo.COLOR, "Azul", "Como el cielo o el agua profunda."),
        r("color_morado", CategoriaRasgo.COLOR, "Morado", "Entre el azul y el rojo."),
        r("color_blanco", CategoriaRasgo.COLOR, "Blanco", "Refleja casi toda la luz."),
        r("color_negro", CategoriaRasgo.COLOR, "Negro", "Absorbe casi toda la luz."),
        r("color_gris", CategoriaRasgo.COLOR, "Gris", "Entre el blanco y el negro."),
        r("color_translucido", CategoriaRasgo.COLOR, "Translúcido", "Deja pasar la luz sin ser transparente del todo."),
        r("color_plateado", CategoriaRasgo.COLOR, "Plateado", "Con brillo metálico."),

        // ---------------------------- TEXTURA ----------------------------
        r("textura_lisa", CategoriaRasgo.TEXTURA, "Lisa", "Sin bultos ni rugosidades."),
        r("textura_rugosa", CategoriaRasgo.TEXTURA, "Rugosa", "Con relieves que se notan al tacto."),
        r("textura_aterciopelada", CategoriaRasgo.TEXTURA, "Aterciopelada", "Cubierta de pelillos muy finos."),
        r("textura_brillante", CategoriaRasgo.TEXTURA, "Brillante", "Refleja la luz como un espejo."),
        r("textura_mate", CategoriaRasgo.TEXTURA, "Mate", "No refleja la luz."),
        r("textura_espinosa", CategoriaRasgo.TEXTURA, "Espinosa", "Con puntas o pinchos."),
        r("textura_humeda", CategoriaRasgo.TEXTURA, "Húmeda", "Su superficie parece mojada."),
        r("textura_dura", CategoriaRasgo.TEXTURA, "Dura", "Resiste la presión, no se dobla."),
        r("textura_blanda", CategoriaRasgo.TEXTURA, "Blanda", "Cede fácilmente al tocarla."),
        r("textura_escamosa", CategoriaRasgo.TEXTURA, "Escamosa", "Cubierta de piezas superpuestas."),
        r("textura_sedosa", CategoriaRasgo.TEXTURA, "Sedosa", "Suave y fina como un hilo de seda."),
        r("textura_granulada", CategoriaRasgo.TEXTURA, "Granulada", "Formada por granos pequeños."),

        // ---------------------------- SIMETRÍA ----------------------------
        r("simetria_bilateral", CategoriaRasgo.SIMETRIA, "Bilateral", "Si la doblas por la mitad, los dos lados coinciden."),
        r("simetria_radial", CategoriaRasgo.SIMETRIA, "Radial", "Sus partes salen del centro como los rayos de una rueda."),
        r("simetria_espiral", CategoriaRasgo.SIMETRIA, "Espiral", "Crece girando alrededor de un punto."),
        r("simetria_ausente", CategoriaRasgo.SIMETRIA, "Sin simetría", "No hay forma de dividirla en partes iguales."),

        // ---------------------------- TAMAÑO ----------------------------
        r("tamano_microscopico", CategoriaRasgo.TAMANO, "Microscópico", "Solo se ve con microscopio."),
        r("tamano_muy_pequeno", CategoriaRasgo.TAMANO, "Muy pequeño", "Más pequeño que una uña."),
        r("tamano_pequeno", CategoriaRasgo.TAMANO, "Pequeño", "Cabe en la palma de la mano."),
        r("tamano_mediano", CategoriaRasgo.TAMANO, "Mediano", "Del tamaño de tu mano abierta."),
        r("tamano_grande", CategoriaRasgo.TAMANO, "Grande", "Más grande que tus dos manos juntas."),

        // ---------------------------- PARTES ----------------------------
        r("parte_patas", CategoriaRasgo.PARTES, "Patas", "Sirven para caminar o sujetarse."),
        r("parte_antenas", CategoriaRasgo.PARTES, "Antenas", "Órganos alargados que detectan el entorno."),
        r("parte_alas", CategoriaRasgo.PARTES, "Alas", "Superficies que permiten volar."),
        r("parte_ojos", CategoriaRasgo.PARTES, "Ojos", "Captan la luz y las imágenes."),
        r("parte_tallo", CategoriaRasgo.PARTES, "Tallo", "Sostiene la planta y transporta el agua."),
        r("parte_hojas", CategoriaRasgo.PARTES, "Hojas", "Donde la planta fabrica su alimento."),
        r("parte_petalos", CategoriaRasgo.PARTES, "Pétalos", "Hojas de colores que rodean la flor."),
        r("parte_raices", CategoriaRasgo.PARTES, "Raíces", "Fijan la planta y absorben agua."),
        r("parte_nervaduras", CategoriaRasgo.PARTES, "Nervaduras", "Líneas que reparten el agua por la hoja o el ala."),
        r("parte_caparazon", CategoriaRasgo.PARTES, "Caparazón", "Cubierta dura que protege el cuerpo."),
        r("parte_concha", CategoriaRasgo.PARTES, "Concha", "Refugio duro construido por el propio animal."),
        r("parte_aletas", CategoriaRasgo.PARTES, "Aletas", "Permiten nadar y mantener el equilibrio."),
        r("parte_escamas", CategoriaRasgo.PARTES, "Escamas", "Placas pequeñas que cubren la piel."),
        r("parte_plumas", CategoriaRasgo.PARTES, "Plumas", "Estructuras ligeras que aíslan y permiten volar."),
        r("parte_tentaculos", CategoriaRasgo.PARTES, "Tentáculos", "Prolongaciones flexibles que exploran o tocan."),
        r("parte_semillas", CategoriaRasgo.PARTES, "Semillas", "Contienen una planta futura."),
        r("parte_cabeza", CategoriaRasgo.PARTES, "Cabeza diferenciada", "Se distingue claramente del resto del cuerpo."),
        r("parte_cola", CategoriaRasgo.PARTES, "Cola", "Prolongación en la parte trasera."),
        r("parte_sombrero", CategoriaRasgo.PARTES, "Sombrero", "Parte superior y ancha de un hongo."),
        r("parte_laminillas", CategoriaRasgo.PARTES, "Laminillas", "Láminas bajo el sombrero donde se forman las esporas."),
        r("parte_nucleo", CategoriaRasgo.PARTES, "Núcleo", "Centro de la célula que guarda la información."),
        r("parte_membrana", CategoriaRasgo.PARTES, "Membrana", "Capa que envuelve y protege la célula."),
        r("parte_pinzas", CategoriaRasgo.PARTES, "Pinzas o mandíbulas", "Sirven para sujetar o cortar."),

        // ---------------------------- ESTRUCTURA ----------------------------
        r("estructura_segmentada", CategoriaRasgo.ESTRUCTURA, "Cuerpo segmentado", "Está dividido en secciones que se repiten."),
        r("estructura_capas", CategoriaRasgo.ESTRUCTURA, "Capas superpuestas", "Crece añadiendo una capa sobre otra."),
        r("estructura_red", CategoriaRasgo.ESTRUCTURA, "Red o malla", "Hilos cruzados que forman una trama."),
        r("estructura_filamentos", CategoriaRasgo.ESTRUCTURA, "Filamentos", "Está formada por hilos muy finos."),
        r("estructura_cristalina", CategoriaRasgo.ESTRUCTURA, "Cristalina", "Caras planas y ángulos que se repiten."),
        r("estructura_hueca", CategoriaRasgo.ESTRUCTURA, "Hueca", "Tiene espacio vacío en su interior."),
        r("estructura_maciza", CategoriaRasgo.ESTRUCTURA, "Maciza", "Llena por dentro, sin huecos."),
        r("estructura_ramificada", CategoriaRasgo.ESTRUCTURA, "Ramificada", "Se divide una y otra vez."),
        r("estructura_celular", CategoriaRasgo.ESTRUCTURA, "Celular", "Formada por celdas o compartimentos."),

        // ---------------------------- NÚMERO ----------------------------
        r("numero_2", CategoriaRasgo.NUMERO, "2 elementos iguales", "Cuenta las partes que se repiten."),
        r("numero_4", CategoriaRasgo.NUMERO, "4 elementos iguales", "Cuenta las partes que se repiten."),
        r("numero_5", CategoriaRasgo.NUMERO, "5 elementos iguales", "Cuenta las partes que se repiten."),
        r("numero_6", CategoriaRasgo.NUMERO, "6 elementos iguales", "Cuenta las partes que se repiten."),
        r("numero_8", CategoriaRasgo.NUMERO, "8 elementos iguales", "Cuenta las partes que se repiten."),
        r("numero_muchos", CategoriaRasgo.NUMERO, "Muchos elementos iguales", "Tantos que cuesta contarlos."),

        // ---------------------------- PARTICULAR ----------------------------
        r("rasgo_colores_aviso", CategoriaRasgo.PARTICULAR, "Colores de aviso", "Colores llamativos que advierten a otros animales."),
        r("rasgo_camuflaje", CategoriaRasgo.PARTICULAR, "Camuflaje", "Se confunde con el entorno."),
        r("rasgo_vuela", CategoriaRasgo.PARTICULAR, "Puede volar", "Se desplaza por el aire."),
        r("rasgo_vive_en_agua", CategoriaRasgo.PARTICULAR, "Vive en el agua", "Su cuerpo está adaptado al medio acuático."),
        r("rasgo_se_dispersa_con_viento", CategoriaRasgo.PARTICULAR, "Se dispersa con el viento", "El aire la transporta lejos."),
        r("rasgo_crece_por_capas", CategoriaRasgo.PARTICULAR, "Crece por capas", "Se hace más grande añadiendo material por fuera."),
        r("rasgo_no_es_ser_vivo", CategoriaRasgo.PARTICULAR, "No es un ser vivo", "No nace, no crece por sí mismo ni se reproduce."),
        r("rasgo_construido_por_animal", CategoriaRasgo.PARTICULAR, "Construido por un animal", "Es obra de un ser vivo, no el ser vivo en sí.")
    )

    private val indice: Map<String, OpcionRasgo> = todas.associateBy { it.id }

    fun porId(id: String): OpcionRasgo? = indice[id]

    fun requerir(id: String): OpcionRasgo =
        indice[id] ?: error("Opción de rasgo desconocida: $id")

    fun deCategoria(categoria: CategoriaRasgo): List<OpcionRasgo> =
        todas.filter { it.categoria == categoria }

    fun categoriaDe(id: String): CategoriaRasgo? = indice[id]?.categoria

    fun etiqueta(id: String): String = indice[id]?.etiqueta ?: id

    fun pista(id: String): String = indice[id]?.pista.orEmpty()

    /** Agrupa un conjunto de identificadores por su categoría, en orden del enum. */
    fun agrupar(ids: Collection<String>): Map<CategoriaRasgo, List<OpcionRasgo>> =
        ids.mapNotNull { indice[it] }
            .groupBy { it.categoria }
            .toSortedMap(compareBy { it.ordinal })
}
