# Pruebas de Tienda-Electronica con JUnit en IntelliJ IDEA

## Qué se preparó

El proyecto usa Maven, Java 21 y Swing. Se agregó JUnit Jupiter 5.11.4
con alcance `test`, junto con Maven Surefire 3.5.2 para ejecutar las pruebas.
La dependencia de pruebas no se incluye en el aplicativo distribuido.
Se fijó Maven Compiler 3.13.0 y `maven.compiler.release=21` para comprobar
también la compatibilidad de las API utilizadas con Java 21.

La copia adaptada de la clase externa se encuentra en:

```text
src/test/java/Pruebas_Unit/Pruebas_JUnit.java
```

Su paquete sigue siendo `Pruebas_Unit`. Surefire incluye explícitamente
`Pruebas_JUnit.java`, porque ese nombre no coincide con sus patrones habituales
(`Test*`, `*Test`, `*Tests`, `*TestCase`). También conserva esos patrones para
futuras clases. El archivo externo original se conserva sin modificaciones.

## Ejecutar desde IntelliJ IDEA

1. Abre la carpeta **Tienda-Electronica** con **File > Open**. Si IntelliJ ofrece
   importar el proyecto Maven, acepta; también puedes abrir `pom.xml` como proyecto.
2. En **File > Project Structure > Project**, selecciona un **JDK 21** en
   **Project SDK** y nivel de lenguaje 21. Si no aparece, usa **Add SDK > Download JDK**.
   El `pom.xml` exige Java 21; un JDK anterior no puede compilarlo.
3. Abre la ventana **Maven** y pulsa **Reload All Maven Projects**. La primera
   carga necesita internet para obtener JUnit y las dependencias del aplicativo.
4. Verifica que `src/test/java` esté identificado como carpeta de pruebas.
   Maven debe configurarlo automáticamente. Si no sucede, haz clic derecho en
   esa carpeta y selecciona **Mark Directory as > Test Sources Root**.
5. Abre `src/test/java/Pruebas_Unit/Pruebas_JUnit.java` y pulsa el triángulo verde
   junto a la clase; selecciona **Run 'Pruebas_JUnit'**. También puedes usar
   el triángulo junto a un método para ejecutar solo ese caso.
6. La ventana **Run** debe mostrar **8 casos aprobados y 1 omitido**. Selecciona
   un caso para revisar su resultado. Para inspeccionar la ejecución, coloca un
   punto de interrupción y selecciona **Debug** junto al método.

No hace falta ejecutar `InicioSesion` ni abrir las ventanas del aplicativo.
Estas pruebas llaman a métodos estáticos de `HelperValidacion`; no usan
credenciales de Firebase, conexión a MySQL ni datos de clientes.

## Por qué la clase adjunta requería adaptación

Java distingue mayúsculas y minúsculas: el método existente es `validarTodo`,
no `ValidarTodo`. La llamada original impide compilar la clase.

Además, `ValidarTodoLetra` calcula:

```java
ValidarVacio(cadena) + RetornarLetra(cadena) + RetornarCEV2(cadena)
```

Cada componente devuelve 0 o 1. `RetornarLetra` devuelve 1 si encuentra un
carácter que **no** es letra. `RetornarCEV2` devuelve 1 si encuentra al menos
un carácter de su lista: `@`, `/`, `;`, `:`, `"`, `!`, espacio o `~`.
El helper de caracteres especiales aplica `trim()`, por lo que los espacios
en los extremos se eliminan para esa comprobación. No cuenta apariciones.
`+` y `.` no pertenecen a esa lista. Un espacio interior sí pertenece.

| Caso del archivo adjunto | Entrada | Esperado original | Esperado adaptado |
| --- | --- | ---: | ---: |
| Cadena vacía | `""` | 1 | 1 |
| Solo letras | `HolaMundo` | 9 | 0 |
| Carácter especial | `Hola@mundo` | 10 | 2 |
| Varios especiales | `Hola!mundo+.` | 12 | 2 |
| Letras y números | `Hola123` | 4 | 1 |
| Solo especiales | `@+;.` | 4 | 2 |
| Espacios y letras | `Hola mundo` | 9 | 2 |
| Fallo deliberado | `@+;.` | 5 | Se conserva 5, desactivado |
| `validarTodo` con números y especiales | `@Hola123: mundo!` | 7 | 2 |

Ejemplo: `Hola123` no está vacío (0), contiene caracteres que no son letras
(1) y no tiene especiales de la lista (0). Por eso el resultado es 1.
En `validarTodo`, el segundo indicador detecta la presencia de dígitos;
no usa `RetornarLetra`.

Estas expectativas verifican el comportamiento actual. Antes de usar estas
pruebas como criterio de aceptación, confirma las reglas del negocio: por
ejemplo, si un nombre completo debe admitir espacios, habrá que definir ese
requisito y probar el validador utilizado para ese campo. La documentación de
algunos métodos del helper no describe exactamente su implementación: la suma
puede devolver 2 y `RetornarLetra` detecta caracteres que no son letras.

## Observar un fallo de JUnit

El método `testSoloCaracteresEspecialesIncorrecto` conserva `assertEquals(5, ...)`
como ejemplo y tiene `@Disabled`. Para la demostración:

1. Retira temporalmente su anotación `@Disabled(...)`.
2. Ejecuta únicamente ese método con su triángulo verde.
3. IntelliJ mostrará **expected: 5** y **actual: 2**, con la línea de la aserción.
4. Restablece `@Disabled(...)` al terminar.

Una aserción fallida indica que el resultado no coincide con la expectativa;
para determinar si existe un defecto hay que contrastar ambos con el requisito.
En este caso, la expectativa 5 se conserva deliberadamente incorrecta.

## Ejecutar con Maven dentro del IDE

En la ventana **Maven**, abre **Lifecycle** y ejecuta **test**. Este camino
también comprueba que Maven descubra la clase adjunta. Los informes quedan en
`target/surefire-reports`.

Si Maven está instalado y disponible en la terminal:

```powershell
mvn test
mvn '-Dtest=Pruebas_JUnit' test
mvn '-Dtest=Pruebas_JUnit#testSoloLetras' test
```

No es obligatorio instalar Maven por separado: IntelliJ incluye Maven.
Si necesitas elegirlo, revisa **Settings > Build, Execution, Deployment >
Build Tools > Maven > Maven home path** y selecciona la versión integrada.

## Alcance y siguientes pruebas

La suite preparada contiene **pruebas unitarias de validación**. Comprueba
una parte de la lógica que utiliza el aplicativo de escritorio; no cubre
botones, navegación, persistencia ni el flujo completo de compra.

Para ampliar la cobertura:

- **Lógica de negocio:** prueba cálculos del carrito, cantidades límite
  (0, 1, 999, 1000), formatos numéricos y reglas de compra con resultados
  definidos por los requisitos. Los casos pueden agruparse con
  `@ParameterizedTest` de JUnit Jupiter.
- **Integración:** prueba los DAO contra una base de datos exclusiva de pruebas
  y servicios aislados; usa preparación y limpieza de datos. Separa estos
  casos para que la suite unitaria siga siendo independiente de servicios.
- **Interfaz Swing:** JUnit puede organizar las pruebas, pero para automatizar
  clics e inspeccionar componentes necesitarás una herramienta para Swing,
  como AssertJ Swing. Verifica su compatibilidad antes de incorporarla y
  ejecuta las operaciones de Swing en su hilo de eventos (EDT). Esas pruebas
  requieren un entorno gráfico y una preparación controlada del aplicativo.

## Problemas frecuentes

| Mensaje o síntoma | Acción |
| --- | --- |
| `Cannot resolve symbol junit` | Recarga Maven y comprueba la descarga de `junit-jupiter`. |
| No aparece el triángulo de pruebas | Comprueba la importación Maven y `src/test/java` como carpeta de pruebas. |
| `invalid target release: 21` | Usa JDK 21 o superior; revisa también el JDK del importador y runner Maven. |
| `Cannot find symbol: ValidarTodo` | Usa la copia adaptada, que llama a `validarTodo` con minúscula. |
| Maven no encuentra pruebas | Comprueba la sección `includes` de Surefire y la ruta de la clase. |
| `mvn` no se reconoce en la terminal | Ejecuta **Maven > Lifecycle > test** desde el IDE. |

## Verificación realizada

Se comprobó la compilación del aplicativo y de la clase con Maven integrado
en IntelliJ, usando JDK 26 y configuración `release 21`. La suite normal
dio 8 casos aprobados, 1 omitido, 0 fallos y 0 errores. También se ejecutó
por separado el caso deliberadamente incorrecto, habilitándolo solo para esa
ejecución: produjo `expected: <5> but was: <2>`.
La ejecución mediante los botones del IDE queda descrita arriba; la
verificación realizada fue con Maven, no mediante interacción con el IDE.

## Referencias oficiales

- [JUnit 5.11.4: guía de usuario](https://docs.junit.org/5.11.4/user-guide/).
- [JetBrains: comenzar con JUnit](https://www.jetbrains.com/help/idea/junit.html).
- [JetBrains: pruebas con Maven](https://www.jetbrains.com/help/idea/work-with-tests-in-maven.html).
- [Maven Surefire: nombres incluidos y excluidos](https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html).
