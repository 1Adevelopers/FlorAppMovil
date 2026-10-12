# Guía de Ejecución de Pruebas - FlorApp Móvil

Este documento detalla las instrucciones necesarias para ejecutar la suite de pruebas automatizadas y consultar los reportes de calidad del proyecto FlorApp.

## Requisitos Previos
* Android Studio (versión Iguana o superior recomendada).
* JDK 17 configurado en el proyecto.
* Para las pruebas de UI (Espresso), se requiere un emulador o dispositivo físico conectado con Android 8.0 (API 26) o superior.

---

## 1. Tests Unitarios y MockWebServer (Pruebas Locales)
Estos tests evalúan la lógica interna (Validadores, ViewModels) y simulan las respuestas de la API de Django usando MockWebServer sin necesidad de un emulador.

### Ejecución desde Android Studio:
1. En la vista de proyecto (Project View), navega a la carpeta `app/src/test/java/`.
2. Haz clic derecho sobre el paquete raíz de las pruebas (`com.ispc.florappmovil`).
3. Selecciona **"Run 'Tests in florappmovil'"**.

### Ejecución por Terminal (Gradle):
Abre la terminal integrada en Android Studio y ejecuta:
```bash
./gradlew testDebugUnitTest
