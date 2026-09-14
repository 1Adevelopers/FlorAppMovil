# 🌿 FlorApp Móvil - Concientización sobre Flora Autóctona de Córdoba

> **Extensión Android Nativa** del ecosistema **FlorApp** para la consulta pública, gestión de docentes y concientización sobre la flora nativa de la provincia de Córdoba.

---

## 📌 Visión General del Proyecto

**FlorApp Móvil** forma parte de un proyecto integrador desarrollado para el **Instituto Superior Politécnico Córdoba (ISPC)** en la *Tecnicatura Superior en Desarrollo Web y Aplicaciones Digitales*. 

Mientras la Plataforma Web administra integralmente los contenidos y el catálogo (ABM de especies), la **Aplicación Móvil** se enfoca en brindar una **experiencia ágil e interactiva en dispositivos Android**, con soporte para:
* **Modo Invitado:** Acceso directo para niños y estudiantes para explorar el catálogo y fichas informativas sin requerir registro.
* **Modo Docente / Administrador:** Inicio de sesión seguro mediante tokens JWT para gestionar perfil y usuarios.

---

## 🛠️ Arquitectura y Tecnologías

El proyecto sigue un esquema desacoplado Cliente-Servidor:

* **Cliente Móvil (Frontend):** Android Nativo desarrollado en **Java 17 (JDK 17)** sobre **Android Studio**.
* **Servidor (Backend):** API REST desarrollada en **Django REST Framework** con base de datos **MySQL** y autenticación **JWT** (JSON Web Tokens).
* **Protocolo de Comunicación:** HTTP/HTTPS mediante consumo de endpoints JSON.
* **Diseño UI/UX:** XML con paleta cromática personalizada (`verde1`, `verde2`, `fondo`, `verde_pastel`).

---

## 🌿 Flujo de Navegación y Pantallas Principales

1. **Bienvenida / Ingreso (`SplashActivity` / `IngresoActivity`):** Verificación automática de sesión guardada localmente u opciones de acceso libre.
2. **Galería (`GaleriaActivity`):** Catálogo interactivo consumido desde la API REST.
3. **Perfil (`ProfileActivity`):** Edición de información personal y cierre de sesión seguro.
4. **Contacto (`ContactoActivity`):** Formulario directo de consultas generales.
5. **Autenticación (`LoginActivity` / `RegisterActivity`):** Formularios de ingreso y registro docente con validaciones en el cliente.

---

## 🌳 Estrategia de Ramas y Control de Versiones (GitFlow)

El desarrollo del repositorio se organiza estrictamente mediante **GitFlow**:

* `main`: Contiene únicamente versiones estables y entregables del software.
* `develop`: Integración de las funcionalidades terminadas y validadas.
* `Feature/Nombre-InicialApellido`: Ramas de trabajo individual donde cada integrante desarrolla sus funcionalidades antes de enviar un **Pull Request** hacia `develop`.

---

## 👥 Equipo de Desarrollo (1A> DEVELOPERS)

| Integrante | Rol en el Proyecto |
| :--- | :--- |
| **Carlos Eduardo Balbastro** | Product Owner / Dev / Tester / Analista de Ciberseguridad |
| **Ignacio Martín Bentivoglio** | Scrum Master / Dev / Tester / Analista de Ciberseguridad |
| **Ruben Dario Bosque** | Dev / Tester / Analista de Ciberseguridad |
| **Kiara Fernandez** | Dev / Tester / Analista de Ciberseguridad |
| **Eric Victor Hugo Heredia** | Dev / Tester / Analista de Ciberseguridad |
| **María Florencia Lorenzati** | Scrum Master / Dev / Tester / Analista de Ciberseguridad |

---

## ⚙️ Requisitos para la Compilación

* **Android Studio:** Jellyfish / Koala o superior.
* **JDK:** Java 17.
* **Sistema Operativo Objetivo:** Android 8.0 (API Level 26 - Oreo) o superior.
* **RAM Mínima:** 2 GB.
