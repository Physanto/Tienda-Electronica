"""Empaqueta el JAR y los datos existentes para Linux, sin incluir Java ni ejecutar pruebas."""
from pathlib import Path
import argparse
import stat
import zipfile


def empaquetar(fuente: Path, destino: Path) -> None:
    requeridos = ["TiendaElectronica.jar", "config.local.properties", "data/tienda.mv.db", "LICENSE", "NOTICE", "tienda.sql"]
    for nombre in requeridos:
        if not (fuente / nombre).is_file():
            raise FileNotFoundError(f"Falta {fuente / nombre}. Prepare primero la distribución del proyecto.")
    archivos = requeridos + (["firebase-adminsdk.json"] if (fuente / "firebase-adminsdk.json").is_file() else [])
    iniciar = r'''#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"
if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" ]]; then
  JAVA_BIN="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
  JAVA_BIN="$(command -v java)"
else
  printf '%s\n' 'No se encontró Java. Instala Java 21 o posterior y vuelve a iniciar.' >&2
  exit 1
fi
exec "$JAVA_BIN" -jar "TiendaElectronica.jar" "$@"
'''
    instrucciones = '''TIENDA ELECTRÓNICA PARA LINUX — JAVA NO INCLUIDO

Requiere Java 21 o posterior instalado y una sesión gráfica de Linux.
Extrae el ZIP completo en una carpeta con permisos de escritura y ejecuta:

    bash Iniciar.sh

También puedes usar ./Iniciar.sh si el archivo conserva el permiso de ejecución.
El lanzador usa JAVA_HOME cuando está definido; de lo contrario usa java del PATH.

Administrador: Admin / 12345.
Cliente de demostración: ana / ana. El módulo Cliente necesita acceso a Firebase.

Incluye 100 clientes, 50 productos, 50 ventas, 5 categorías y 100 detalles de venta
en la base local H2. No hace falta instalar MySQL para usar esta configuración.
La configuración de MySQL se puede habilitar en config.local.properties.

Promociones se organiza en Productos, Clientes y Vigentes y programadas.
Las sugerencias se cargan al abrir el módulo. El precio base del producto no se
modifica al guardar un descuento. La promoción, su vínculo y los pendientes de
sincronización se guardan juntos. Desactivar sin internet conserva el cambio
para sincronizarlo cuando vuelva la conexión.

Mantén juntos el JAR, config.local.properties y la carpeta data. Conserva data al
actualizar para no perder cambios locales. No abras dos copias de la aplicación
que usen el mismo archivo de datos.

Si se incluye firebase-adminsdk.json, contiene una clave administrativa de
Firebase. Comparte este paquete solo con personas de confianza y no lo publiques
en repositorios, páginas web ni enlaces públicos. La clave está fuera del JAR.
'''
    destino.parent.mkdir(parents=True, exist_ok=True)
    raiz = "TiendaElectronica-Linux/"
    with zipfile.ZipFile(destino, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=6) as paquete:
        for nombre in archivos:
            entrada = zipfile.ZipInfo(raiz + nombre)
            entrada.create_system = 3
            permisos = 0o600 if nombre in ("firebase-adminsdk.json", "data/tienda.mv.db") else 0o644
            entrada.external_attr = (stat.S_IFREG | permisos) << 16
            entrada.compress_type = zipfile.ZIP_DEFLATED
            paquete.writestr(entrada, (fuente / nombre).read_bytes())
        for nombre, contenido, permisos in [("Iniciar.sh", iniciar, 0o755), ("LEEME.txt", instrucciones, 0o644)]:
            entrada = zipfile.ZipInfo(raiz + nombre)
            entrada.create_system = 3
            entrada.external_attr = (stat.S_IFREG | permisos) << 16
            entrada.compress_type = zipfile.ZIP_DEFLATED
            paquete.writestr(entrada, contenido.encode("utf-8"))
    print(f"ZIP Linux creado sin Java y sin ejecutar pruebas: {destino.resolve()} ({destino.stat().st_size} bytes)")


if __name__ == "__main__":
    proyecto = Path(__file__).resolve().parent.parent
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fuente", type=Path, default=proyecto / "dist" / "TiendaElectronica")
    parser.add_argument("--destino", type=Path, default=proyecto / "dist" / "TiendaElectronica-Linux.zip")
    argumentos = parser.parse_args()
    empaquetar(argumentos.fuente, argumentos.destino)
