param(
    [string]$JavaHome = $env:JAVA_HOME,
    [string]$Jar = '',
    [string]$Destino = '',
    [switch]$IncluirCredenciales,
    [switch]$OmitirCargaDatos
)

$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path -Parent $PSScriptRoot
if (-not $Jar) { $Jar = Join-Path $raizProyecto 'target\TiendaElectronica-1.0-SNAPSHOT-completo.jar' }
if (-not $Destino) { $Destino = Join-Path $raizProyecto 'dist\TiendaElectronica' }
if (-not $JavaHome) { throw 'Indique -JavaHome con la carpeta de un JDK 21 o posterior.' }
$javaEmpaque = Join-Path $JavaHome 'bin\java.exe'
$jlinkEmpaque = Join-Path $JavaHome 'bin\jlink.exe'
if (-not (Test-Path -LiteralPath $Jar -PathType Leaf)) { throw 'Primero ejecute mvn package para generar el JAR completo.' }
if (-not (Test-Path -LiteralPath $jlinkEmpaque -PathType Leaf)) { throw 'El JDK indicado no contiene jlink.' }

$destinoCompleto = [IO.Path]::GetFullPath($Destino)
New-Item -ItemType Directory -Force -Path $destinoCompleto | Out-Null
Copy-Item -LiteralPath $Jar -Destination (Join-Path $destinoCompleto 'TiendaElectronica.jar') -Force
foreach ($nombre in @('LICENSE', 'NOTICE', 'tienda.sql')) {
    Copy-Item -LiteralPath (Join-Path $raizProyecto $nombre) -Destination $destinoCompleto -Force
}

$runtimeEmpaque = Join-Path $destinoCompleto 'runtime'
if (-not (Test-Path -LiteralPath (Join-Path $runtimeEmpaque 'bin\java.exe'))) {
    & $jlinkEmpaque --add-modules java.se,jdk.unsupported,jdk.crypto.ec,jdk.localedata --bind-services --strip-debug --no-header-files --no-man-pages --compress=zip-6 --output $runtimeEmpaque
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo generar el runtime de Java.' }
}

$archivoConfig = Join-Path $destinoCompleto 'config.local.properties'
$config = @'
# Datos locales portables. No necesita instalar MySQL ni Java.
db.mode=embedded
db.embedded.path=data/tienda
# Clave externa al JAR; nunca publicar esta carpeta si contiene credenciales.
firebase.credentials=firebase-adminsdk.json
'@
[IO.File]::WriteAllText($archivoConfig, $config, [Text.UTF8Encoding]::new($false))

if ($IncluirCredenciales) {
    $clavesEmpaque = @(Get-ChildItem -LiteralPath $raizProyecto -Filter '*-firebase-adminsdk-*.json' -File)
    if ($clavesEmpaque.Count -ne 1) { throw 'Se requiere una única clave Firebase Admin SDK en la raíz.' }
    Copy-Item -LiteralPath $clavesEmpaque[0].FullName -Destination (Join-Path $destinoCompleto 'firebase-adminsdk.json') -Force
}

$iniciar = @'
@echo off
setlocal
cd /d "%~dp0"
if not exist "runtime\bin\javaw.exe" (
  echo No se encontro el runtime. Extraiga el ZIP completo antes de ejecutar.
  pause
  exit /b 1
)
start "Tienda Electronica" "runtime\bin\javaw.exe" -jar "TiendaElectronica.jar"
endlocal
'@
[IO.File]::WriteAllText((Join-Path $destinoCompleto 'Iniciar.cmd'), $iniciar.Replace("`r`n", "`n").Replace("`n", "`r`n"), [Text.ASCIIEncoding]::new())

if (-not $OmitirCargaDatos) {
Push-Location $destinoCompleto
try {
    & $javaEmpaque '-Ddb.mode=embedded' "-Ddb.embedded.path=$(Join-Path $destinoCompleto 'data\tienda')" -cp (Join-Path $destinoCompleto 'TiendaElectronica.jar') utilidades.CargarDatosDemo --solo-local
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo preparar la base local con los 100 clientes de demostración.' }
    & $javaEmpaque '-Ddb.mode=embedded' "-Ddb.embedded.path=$(Join-Path $destinoCompleto 'data\tienda')" -cp (Join-Path $destinoCompleto 'TiendaElectronica.jar') utilidades.CargarProductosVentasDemo --solo-local
    if ($LASTEXITCODE -ne 0) { throw 'No se pudieron preparar los productos y las ventas de demostración.' }
} finally {
    Pop-Location
}
}

$instrucciones = @'
TIENDA ELECTRÓNICA - DISTRIBUCIÓN PARA WINDOWS X64

Extraiga todo el ZIP en una carpeta con permisos de escritura y abra Iniciar.cmd.
Incluye Java y una base local H2 con 100 clientes, 50 productos y 50 ventas
ficticias, con sus categorías y detalles. No necesita instalar
MySQL ni Java. No mueva el JAR fuera de esta carpeta si quiere conservar la
configuración, los datos locales y la conexión a Firebase.

Administrador de demostración: usuario Admin, contraseña 12345.
Cliente de demostración: usuario ana, contraseña ana. El módulo Cliente requiere
que Firebase esté disponible. También se conservan las credenciales originales.

Los datos locales se guardan en data/tienda.mv.db. Mantenga la carpeta data al
actualizar el programa. Las operaciones pendientes se sincronizan con Firebase
cuando el servicio está disponible. La API Cloud Firestore debe estar habilitada
y debe existir la base de datos (default) en el proyecto configurado.

El JAR también se puede ejecutar con Java 21 o posterior:
java -jar TiendaElectronica.jar

Para usar MySQL, cambie db.mode a mysql en config.local.properties y agregue
db.url, db.user y db.password. Cree el esquema con tienda.sql. El modo auto prueba
MySQL al iniciar y usa H2 si no consigue conectarse; mantiene ese almacén durante
toda la sesión para conservar el orden de sincronización.

PRIVACIDAD DE LA CLAVE
Si esta carpeta contiene firebase-adminsdk.json, la clave concede acceso
administrativo al proyecto Firebase. Comparta el paquete únicamente con personas
de confianza. No lo publique en repositorios, páginas web o enlaces públicos.
La clave permanece fuera del JAR y se puede reemplazar sin volver a compilar.
'@
[IO.File]::WriteAllText((Join-Path $destinoCompleto 'LEEME.txt'), $instrucciones, [Text.UTF8Encoding]::new($false))

$archivoZip = Join-Path (Split-Path -Parent $destinoCompleto) 'TiendaElectronica-Windows-x64.zip'
Compress-Archive -LiteralPath $destinoCompleto -DestinationPath $archivoZip -Force
Write-Output "Distribución creada: $destinoCompleto"
Write-Output "Paquete: $archivoZip"
