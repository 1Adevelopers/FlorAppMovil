#  FlorApp Móvil - Concientización sobre Flora Autóctona de Córdoba

> **Extensión Android Nativa** del ecosistema **FlorApp** para la consulta pública, gestión de docentes y concientización sobre la flora nativa de la provincia de Córdoba.

---

##  Visión General del Proyecto

**FlorApp Móvil** forma parte de un proyecto integrador desarrollado para el **Instituto Superior Politécnico Córdoba (ISPC)** en la *Tecnicatura Superior en Desarrollo Web y Aplicaciones Digitales*. 

Mientras la Plataforma Web administra integralmente los contenidos y el catálogo (ABM de especies), la **Aplicación Móvil** se enfoca en brindar una **experiencia ágil e interactiva en dispositivos Android**, con soporte para:
* **Modo Invitado:** Acceso directo para niños y estudiantes para explorar el catálogo y fichas informativas sin requerir registro.
* **Modo Docente / Administrador:** Inicio de sesión seguro mediante tokens JWT para gestionar perfil y usuarios.

---

##  Arquitectura y Tecnologías

El proyecto sigue un esquema desacoplado Cliente-Servidor:

* **Cliente Móvil (Frontend):** Android Nativo desarrollado en **Java 17 (JDK 17)** sobre **Android Studio**.
* **Servidor (Backend):** API REST desarrollada en **Django REST Framework** con base de datos **MySQL** y autenticación **JWT** (JSON Web Tokens).
* **Protocolo de Comunicación:** HTTP/HTTPS mediante consumo de endpoints JSON.
* **Diseño UI/UX:** XML con paleta cromática personalizada (`verde1`, `verde2`, `fondo`, `verde_pastel`).

---

##  Flujo de Navegación y Pantallas Principales

1. **Bienvenida / Ingreso (`SplashActivity` / `IngresoActivity`):** Verificación automática de sesión guardada localmente u opciones de acceso libre.
2. **Galería (`GaleriaActivity`):** Catálogo interactivo consumido desde la API REST.
3. **Perfil (`ProfileActivity`):** Edición de información personal y cierre de sesión seguro.
4. **Contacto (`ContactoActivity`):** Formulario directo de consultas generales.
5. **Autenticación (`LoginActivity` / `RegisterActivity`):** Formularios de ingreso y registro docente con validaciones en el cliente.

---

##  Estrategia de Ramas y Control de Versiones (GitFlow)

El desarrollo del repositorio se organiza estrictamente mediante **GitFlow**:

* `main`: Contiene únicamente versiones estables y entregables del software.
* `develop`: Integración de las funcionalidades terminadas y validadas.
* `Feature/Nombre-InicialApellido`: Ramas de trabajo individual donde cada integrante desarrolla sus funcionalidades antes de enviar un **Pull Request** hacia `develop`.

---

##  Equipo de Desarrollo (1A> DEVELOPERS)

| Integrante | Rol en el Proyecto |
| :--- | :--- |
| **Carlos Eduardo Balbastro** | Product Owner / Dev / Tester / Analista de Ciberseguridad |
| **Ignacio Martín Bentivoglio** | Scrum Master / Dev / Tester / Analista de Ciberseguridad |
| **Ruben Dario Bosque** | Dev / Tester / Analista de Ciberseguridad |
| **Kiara Fernandez** | Dev / Tester / Analista de Ciberseguridad |
| **Eric Victor Hugo Heredia** | Dev / Tester / Analista de Ciberseguridad |
| **María Florencia Lorenzati** | Scrum Master / Dev / Tester / Analista de Ciberseguridad |

---

##  Requisitos para la Compilación

* **Android Studio:** Jellyfish / Koala o superior.
* **JDK:** Java 17.
* **Sistema Operativo Objetivo:** Android 8.0 (API Level 26 - Oreo) o superior.
* **RAM Mínima:** 2 GB.


---

##  Configuración de la Conexión con el Backend

La app consume la API REST de Django del repositorio [moduloweb](https://github.com/1Adevelopers/moduloweb). La IP del servidor **se configura en un único lugar**: el archivo `Gradle Scripts/local.properties`, que es propio de cada computadora.

### 1. Levantar el backend Django

Seguir el README de *moduloweb* para instalar dependencias, configurar el `.env` y cargar los fixtures (`categorias.json`, `usuarios.json` y `especies.json`, en ese orden).

En el `.env` del backend, agregar la IP de tu PC en `ALLOWED_HOSTS`:

    ALLOWED_HOSTS=127.0.0.1,localhost,TU_IP,10.0.2.2,0.0.0.0

Iniciar el servidor **escuchando en todas las interfaces** (si se usa solo `runserver`, el celular no puede conectarse):

    python manage.py runserver 0.0.0.0:8000

### 2. Obtener la IP de tu PC

En una terminal de Windows ejecutar `ipconfig` y copiar la **Dirección IPv4** del adaptador **Wi-Fi** (por ejemplo `192.168.0.74`).

> ⚠️ No usar IPs de adaptadores virtuales como VirtualBox (`192.168.56.x`): el celular no puede alcanzarlas.

### 3. Configurar la IP en la app

Abrir `local.properties` (en Android Studio: *Gradle Scripts → local.properties*) y agregar al final:

    florapp.ip=TU_IP

Luego hacer **Sync Project** y **Run**. Si la línea no existe, la app usa `10.0.2.2`, que sirve para el emulador.

### ¿Cómo funciona?

* `app/build.gradle` lee `florapp.ip` y genera `BuildConfig.BASE_URL` (`http://TU_IP:8000/`).
* `RetrofitClient`, `ContactoActivity` y `RegisterActivity` toman la URL desde ahí, sin IPs escritas en el código.
* `app/src/debug/res/xml/network_security_config.xml` permite HTTP **solo en la versión de desarrollo** (botón Run). La versión *release* usa el archivo de `src/main`, que exige HTTPS.


### Solución de problemas

| Síntoma | Causa probable |
| :--- | :--- |
| "Error de red" / "No se pudo conectar con el servidor" | El servidor no se inició con `0.0.0.0:8000`, la IP es incorrecta o el celular está en otra red Wi-Fi. |
| Desde el celular no carga `http://TU_IP:8000/api/flora/categorias/` | El **firewall de Windows o el antivirus** (por ejemplo, Avast) bloquea el puerto 8000. Permitir el puerto o marcar la red como privada. |
| Funcionaba y dejó de funcionar | El router cambió la IP de la PC. Volver a revisar `ipconfig` y actualizar `local.properties` y el `.env`. |
| Error 400 "DisallowedHost" en la consola de Django | Falta la IP en `ALLOWED_HOSTS` del `.env`. |