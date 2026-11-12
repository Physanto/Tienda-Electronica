# Tienda-Electronica

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)

Una aplicación de escritorio desarrollada en **Java (Swing)** que implementa una arquitectura de capas. El sistema permite la gestión de Personas (clientes/administradores) y sus Productos, garantizando la persistencia de datos mediante una sincronización inteligente en segundo plano entre una base de datos local (MySQL) y la nube (Firebase Firestore).

## Características Principales

*   **Arquitectura Tolerante a Fallos:** Capacidad de operar sin conexión a internet (Modo Offline) guardando datos en una cola de trabajo local.
*   **Sincronización Inteligente:** Un hilo monitor (Daemon Thread) detecta automáticamente la recuperación de la red y sincroniza los datos locales pendientes hacia la nube.
*   **Control de Roles:** Autenticación de usuarios con roles específicos (Administrador y Cliente).
*   **Seguridad:** Cifrado de contraseñas mediante algoritmo **SHA-256**.
*   **Borrado Lógico:** Manejo seguro de eliminaciones en modo offline para evitar el "Problema de la Lápida" al sincronizar con la nube.
*   **Interfaz Gráfica (GUI):** Vistas desarrolladas con Java Swing para un manejo intuitivo de las operaciones CRUD.

## Tecnologías y Herramientas

*   **Lenguaje:** Java
*   **Bases de Datos:**
    *   **Nube:** Google Firebase (Firestore) a través del Firebase Admin SDK.
    *   **Local:** MySQL (JDBC Driver).
*   **Interfaz Gráfica:** Java Swing / AWT.

## Estructura del Proyecto

El proyecto está organizado bajo una arquitectura de capas:

*   `/Logica_Negocio`: Modelos de dominio (`Persona`, `Producto`, `Usuario`).
*   `/Logica_Conexion`: Implementación del patrón **DAO** (`PersonaDAO`) para MySQL y el proveedor de Firestore (`PersonaProvider`).
*   `/Helpers`: Clases utilitarias (Cifrado, validaciones, Monitor de Red, Gestor de Sincronización).
*   `/GUI`: Interfaces de email separadas por módulo y rol.
*   `/Logica_Cliente`: Punto de entrada de la aplicación (`Main`).

## Instalación y configuración

El código requiere un JDK 21 o posterior y Maven. Para ejecutar la distribución
para Windows, extrae el ZIP completo y abre `Iniciar.cmd`: incluye Java y una
base H2 con los 100 clientes ficticios. Conserva juntas las carpetas y el JAR.

### Base local

La configuración predeterminada (`db.mode=auto`) intenta conectar a MySQL y, si
no está disponible al abrir la primera conexión, utiliza H2 durante esa sesión.
No cambia de almacén a mitad de la ejecución. En la distribución portable se
utiliza `db.mode=embedded` para que cada equipo conserve sus propios datos.

Para usar MySQL, ejecuta `tienda.sql`. Crea `Tienda_Electronica` y sus tablas sin
borrar datos existentes. Copia `config.example.properties` como
`config.local.properties` y ajusta `db.url`, `db.user` y `db.password`.
`db.mode=mysql` exige MySQL y permite detectar errores de configuración sin
activar el respaldo. También puedes configurar `DB_URL`, `DB_USER`,
`DB_PASSWORD` y `DB_MODE`; las propiedades JVM tienen prioridad sobre el entorno,
y el entorno sobre el archivo local.

### Firebase

La aplicación reconoce un único `*-firebase-adminsdk-*.json` en la raíz. También
admite `firebase.credentials` en la configuración o la variable
`GOOGLE_APPLICATION_CREDENTIALS`. Las rutas relativas se resuelven junto al
proyecto o al JAR. En un entorno Google Cloud puede usar las credenciales
predeterminadas de la plataforma si no se indica un archivo.

Habilita la API Cloud Firestore y crea la base `(default)` en el proyecto de la
clave. Inicializar el SDK no confirma que la base exista o que permita consultas.
Consulta la [configuración oficial del Admin SDK](https://firebase.google.com/docs/admin/setup).

Las claves permanecen fuera del JAR y de Git. El paquete privado puede llevar
`firebase-adminsdk.json` como archivo externo: concede acceso administrativo y
solo debe compartirse con personas de confianza.

### Compilar y cargar los datos de prueba

```bash
mvn test
mvn package
java -cp target/TiendaElectronica-1.0-SNAPSHOT-completo.jar utilidades.CargarDatosDemo
```

El cargador crea 100 clientes ficticios con UUID deterministas y comprueba los
seis campos en MySQL y Firestore. Repetirlo no duplica registros; si encuentra
uno de esos identificadores con otros datos, cancela la carga y lo conserva.
Con `--solo-local` opera únicamente sobre el almacén local configurado, y con
`--verificar` solo consulta los datos.

Para cargar además 50 productos y 50 ventas ficticias, ejecuta
`java -cp target/TiendaElectronica-1.0-SNAPSHOT-completo.jar utilidades.CargarProductosVentasDemo`.
Incluye 5 categorías y 100 detalles de venta; verifica los importes, las relaciones
y la igualdad de todos los campos entre ambas bases. Admite `--solo-local` y
`--verificar` y conserva los registros existentes si detecta un conflicto.

El administrador de demostración mantiene el usuario `Admin` y la contraseña
`12345`. Las operaciones del administrador guardan el dato y el pendiente de
sincronización en una misma transacción local. La cola se reproduce en orden
hacia Firebase y se detiene al fallar una operación para reintentar después.
La sincronización automática es de local a nube; no importa cambios remotos a
MySQL ni a H2. El módulo de clientes mantiene su funcionamiento contra la nube.
Los clientes de prueba admiten su nombre en minúsculas como usuario y contraseña;
por ejemplo, `ana` / `ana`. Este acceso es de demostración y requiere Firestore.

El JAR completo se genera en `target/TiendaElectronica-1.0-SNAPSHOT-completo.jar`.
Para preparar el paquete Windows con Java incluido:

```powershell
./scripts/CrearDistribucion.ps1 -JavaHome 'C:/ruta/al/jdk' -IncluirCredenciales
```

El resultado queda en `dist/TiendaElectronica-Windows-x64.zip`. El JAR aislado
requiere Java 21 o posterior; el ZIP incluye su propio runtime para Windows x64.

Para generar el ZIP de Linux sin incluir Java, ejecuta
`python scripts/CrearDistribucionLinux.py` después de preparar la distribución.
El resultado queda en `dist/TiendaElectronica-Linux.zip`. El compañero debe tener
Java 21 o posterior instalado; después de extraer todo el ZIP, puede iniciar
la aplicación con `bash Iniciar.sh`. Este empaquetado copia los datos existentes
y no ejecuta la aplicación ni pruebas.

El módulo Promociones separa productos, clientes y promociones vigentes o
programadas en tres pestañas. Carga las sugerencias automáticamente, conserva
el precio base y guarda cada promoción con su vínculo y sus eventos de
sincronización en una sola transacción. No permite promociones con fechas
superpuestas para el mismo destino. Las promociones finalizadas se excluyen
al consultar, sin modificar sus fechas históricas.

## Pruebas con JUnit en IntelliJ IDEA

La configuración Maven incluye JUnit 5 y la clase adaptada
`src/test/java/Pruebas_Unit/Pruebas_JUnit.java`.
Consulta la [guía de ejecución y explicación de los casos](docs/PRUEBAS_JUNIT_INTELLIJ.md).

## Autores
* Manuel David Escobar Figueroa (https://github.com/Physanto)
* Marlon Estiven Vargas Muñoz
* Juan David Erazo Meneses
* Sebastian Mamian Palta

## License

This project is licensed under the Apache License 2.0.

## Attribution

If you redistribute this project or create derivative works, please preserve the LICENSE and NOTICE files.

Copyright (c) 2026 The Tienda-Electronica Contributors.
