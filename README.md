# Practica para capacitacion de Sofka
### API REST

 ![Static Badge](https://img.shields.io/badge/0.0.1-version-%2300bab4) ![Static Badge](https://img.shields.io/badge/0.0.1-release-%2300bab4)

## Crear un API REST que tenga las siguientes caracteristicas:

1. Endpoint que permita generar una lista variable de los productos más
vendidos.

2. Endpoint que permita solicitar el reporte de todos los productos con sus
unidades vendidas.

3. Endpoint que permita solicitar todas las facturas que hay almacenadas
de forma paginada de 100 en 100.

4. Endpoint para registrar facturas.

# Configuraciones
## Configuración de propertie y de ambiente

#### Base de datos

Application.yml (configuración de base de datos)

```
spring:
   data:
      mongodb:
         host: localhost
         port: 27017
         database: sofka
         username: ${USERMONGO}
         password: ${PASSMONGO}
         authentication-database: admin 
```

Antes de comenzar y arrancar nuestro proyecto debemos configurar dos variables de entorno en nuestro sistema operativo, las cuales deberán tener el **usuario y contraseña** de la base de datos mongo.

Adicionalmente tenemos al inicio debemos tener una **base de datos mongo llamada sofka**, la cual  debe tener la **colección sale**.

Datos de conexión  | Valores
------------- | -------------
host  | localhost
puerto | 27017
base de datos  | sofka
nombre de variable de entorno para usuario  | USERMONGO
nombre de variable de entorno para contraseña  | PASSMONGO
base de datos de autenticación | admin

## API Docs

http://localhost:8080/webjars/swagger-ui/index.html
