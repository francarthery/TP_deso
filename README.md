"# TP_deso" 

##Como ejecutar
1) Tener instalado docker y abierto. Correr(en la raiz del proyecto): docker-compose up -d
2) Correr Spring Boot con .\mvnw spring-boot:run

##Ver tablas BD por consola.
Abrir consola MySQL: docker exec -it tp_hotel_mysql mysql -u root -proot
Seleccionar DB: USE tp_hotel_db;
Mostrar tablas: SHOW TABLES;
Mostrar una tabla: DESCRIBE nombre;

En la carpeta scripts hay datos de prueba para insertar en la BD.

##
Detener container (-v hace que ademas se borre toda la info de la BD):
docker-compose down [-v] 