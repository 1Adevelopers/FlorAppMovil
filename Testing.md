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
```

### Ubicación del Reporte HTML:
Una vez finalizada la ejecución, el reporte HTML generado automáticamente por Gradle se encuentra en:
`app/build/reports/tests/testDebugUnitTest/index.html`

---

## 2. Tests de UI y Accesibilidad con Espresso (Pruebas Instrumentadas)
Estos tests ejecutan la aplicación en un dispositivo real/emulador para validar el flujo crítico de navegación y realizar chequeos automáticos de accesibilidad (AccessibilityChecks).

### Ejecución desde Android Studio:
1. Asegúrate de tener un emulador encendido.
2. Navega a la carpeta `app/src/androidTest/java/`.
3. Haz clic derecho sobre la clase de prueba (ej. `LoginFlowTest`).
4. Selecciona **"Run 'LoginFlowTest'"**.

### Ejecución por Terminal (Gradle):
```bash
./gradlew connectedAndroidTest
```

### Ubicación del Reporte HTML:
El reporte de las pruebas instrumentadas se generará en la siguiente ruta:
`app/build/reports/androidTests/connected/index.html`

---

## 3. Pruebas Manuales y Matriz de Trazabilidad
Los casos de prueba manuales de caja negra, pruebas exploratorias y análisis de seguridad (validación de persistencia de token JWT) se encuentran documentados en la **Matriz de Trazabilidad**.

* Puedes consultar la planilla completa con las columnas de Capa, Ambiente, Pasos y Resultados en la [Matriz de Trazabilidad QA - Sprint 2](https://docs.google.com/spreadsheets/d/149s7t0FSNicdBmSMZw66QXke1f7svicE/edit?usp=sharing&ouid=101996099339104488006&rtpof=true&sd=true).
