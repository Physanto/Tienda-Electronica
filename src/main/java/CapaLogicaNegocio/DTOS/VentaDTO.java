package CapaLogicaNegocio.DTOS;

import CapaLogicaNegocio.Logica_Negocio.Venta;

/**
 * record encargado de modelar un objeto venta generico para la vista.
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public record VentaDTO(String id, String fechaVenta, String totalVenta, Venta.MetodoPago metodoPago, String idCliente) {}

sudo apt update
sudo apt install apache2 php libapache2-mod-php -y

Si usas MySQL:

sudo apt install mariadb-server php-mysql -y
2. Verificar que funciona

Abre en el navegador:

http://TU_IP_DEL_SERVER

Debería aparecer la página de Apache.

        3. Subir tu proyecto

La carpeta web de Apache es:

        /var/www/html

Entonces:

cd /var/www/html

Borra el index por defecto:

sudo rm index.html

Copia tu app ahí.

Por ejemplo:

sudo cp -r ~/mi_app/* /var/www/html/
4. Dar permisos
sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html
5. Reiniciar Apache
sudo systemctl restart apache2
6. Entrar desde el navegador
http://IP_DEL_SERVER

Y ya debería funcionar.

Si quieres usar un dominio

Ejemplo:

midominio.com

Debes apuntar el DNS del dominio a la IP del servidor.

Luego puedes poner SSL gratis con:

Certbot (Let's Encrypt)

Instalación rápida:

sudo apt install certbot python3-certbot-apache -y
sudo certbot --apache
La forma MÁS rápida de todas

Si solo quieres probar rápido:

Usar el servidor interno de PHP

En la carpeta del proyecto:

php -S 0.0.0.0:8000

Luego entras:

http://IP:8000

Pero esto NO es recomendado para producción.