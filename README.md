# Tienda-Electronica

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)

Una aplicación de escritorio desarrollada en **Java (Swing)** que implementa una arquitectura de capas. El sistema permite la gestión de Personas (clientes/administradores) y sus Productos, garantizando la persistencia de datos mediante una sincronización inteligente en segundo plano entre una base de datos local (MySQL) y la nube (Firebase Firestore).

## 🚀 Características Principales

*   **Arquitectura Tolerante a Fallos:** Capacidad de operar sin conexión a internet (Modo Offline) guardando datos en una cola de trabajo local.
*   **Sincronización Inteligente:** Un hilo monitor (Daemon Thread) detecta automáticamente la recuperación de la red y sincroniza los datos locales pendientes hacia la nube.
*   **Control de Roles:** Autenticación de usuarios con roles específicos (Administrador y Cliente).
*   **Seguridad:** Cifrado de contraseñas mediante algoritmo **SHA-256**.
*   **Borrado Lógico:** Manejo seguro de eliminaciones en modo offline para evitar el "Problema de la Lápida" al sincronizar con la nube.
*   **Interfaz Gráfica (GUI):** Vistas desarrolladas con Java Swing para un manejo intuitivo de las operaciones CRUD.

## 🛠️ Tecnologías y Herramientas

*   **Lenguaje:** Java (JDK X.X)
*   **Bases de Datos:**
    *   **Nube:** Google Firebase (Firestore) a través del Firebase Admin SDK.
    *   **Local:** MySQL (JDBC Driver).
*   **Interfaz Gráfica:** Java Swing / AWT.

## 📁 Estructura del Proyecto

El proyecto está organizado bajo una arquitectura de capas:

*   `/Logica_Negocio`: Modelos de dominio (`Persona`, `Producto`, `Usuario`).
*   `/Logica_Conexion`: Implementación del patrón **DAO** (`PersonaDAO`) para MySQL y el proveedor de Firestore (`PersonaProvider`).
*   `/Helpers`: Clases utilitarias (Cifrado, validaciones, Monitor de Red, Gestor de Sincronización).
*   `/GUI`: Interfaces de usuario separadas por módulo y rol.
*   `/Logica_Cliente`: Punto de entrada de la aplicación (`Main`).

## ⚙️ Instalación y Configuración

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/Physanto/Tienda-Electronica.git](https://github.com/Physanto/Tienda-Electronica.git)
    ```
2.  **Configurar Base de Datos Local (MySQL):**
    *   Crear una base de datos llamada `prueba` (o el nombre configurado en `Logica_Conexion/Conexion.java`).
    *   Ejecutar el script SQL para tener la base de datos local (Este se encuentra script-sql/bd.sql).
3.  **Configurar Firebase:**
    *   Descargar el archivo de credenciales de servicio desde la consola de Firebase, esto es en [firebase](https://firebase.google.com/?_gl=1*wi2h65*_up*MQ..&gclid=Cj0KCQjw2MbPBhCSARIsAP3jP9zGvuFqI_UiOJLIw95ph_k-lh82OFyNOpSQeVhpA5VPBalzE2hxtXoaAlHsEALw_wcB&gclsrc=aw.ds&hl=es-419).
    *   Nombrar el archivo como `tienda-electronica.json` y colocarlo en la raíz del proyecto.

##   Autores
* Manuel David Escobar Figueroa (https://github.com/Physanto)
* Marlon Estiven Vargas Muñoz
* Juan David Erazo Meneses
* Sebastian Mamian Palta
