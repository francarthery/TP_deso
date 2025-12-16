"# TP_deso" 

##Como ejecutar
1) Tener instalado docker y abierto. Correr(en la raiz del proyecto): docker-compose up -d
2) Correr Spring Boot con .\mvnw spring-boot:run

##Ver tablas BD por consola.
Abrir consola MySQL: docker exec -it tp_hotel_mysql mysql -u root -proot
Seleccionar DB: USE tp_hotel_db;
Mostrar tablas: SHOW TABLES;
Mostrar una tabla: DESCRIBE nombre;

En la carpeta ./scripts hay datos de prueba para insertar en la BD.
El orden de insercion de las tablas para respetar las FK es:
-usuarios
-huesped
-habitacion
-reservas
-estadias
-estadia_huespedes_invitados
-consumos

##
Detener container (-v hace que ademas se borre toda la info de la BD):
docker-compose down [-v] 

-------------Aclaraciones entrega TP--------------------
Aclaracion frontend
Con nuestro grupo mantuvimos el frontend y backend separados en dos repositorios desde un inicio. Para cumplir con la entrega, la cual pide un repositorio, juntamos todo en el repositorio del backend. Por eso aparecera como que el frontend se hizo en un solo commit. El repositorio real es (rama deso-final):
https://github.com/francarthery/front_tp_deso


Patrones utilizados:
--Factory: Para abstraer la creacion de Pagos sin exponer la logica de creacion al cliente. Strategy Factory devuelve la estrategia de validacion adecuada segun el tipo de pago.
--Strategy: Permite definir una familia de algoritmos para la validacion de pago. Si el sistema fuera real, utilizando esta arquitectura se podria agregar un metodo "realizarPago" que dependiendo del tipo de pago cumpla las funciones correspondientes. Por ejemplo, en pago con tarjeta conectar con el banco.
--Singleton: El strategyFactory tiene un bloque estatico que inicializa las estrategias, creando y manteniendo una unica entidad de cada una. Ademas, al utilizar utilizar Spring Boot con inyeccion de dependencias, este maneja beans de las clases en forma de singleton.
--DTO: se utilizan objetos simples para transferir datos entre las capas de aplicacion y hacia el cliente, evitando exponer las entidades de la BD directamente.


Factura:
Dado que por un tema de complejidad el sistema no se conecta a la API real de ARCA para generar la factura, generamos un PDF que simula la factura en el gestorFacturacion. Lo hicimos siguiendo un mock que hicimos para diseño de sistemas ./Factura_hotel.pdf
