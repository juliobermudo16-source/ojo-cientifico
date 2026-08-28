# Ojo Cientifico — reglas ProGuard
-keepattributes *Annotation*
-keep class com.ojocientifico.app.data.local.entity.** { *; }
-dontwarn org.jetbrains.annotations.**
