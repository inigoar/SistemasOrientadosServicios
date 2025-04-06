# Proyecto SOS WSDL 2020

##Autores
- Iñigo Aranguren Redondo
- Ignacio de las Alas-Pumariño Martínez

##Comentarios
En la carpeta clientes hay varios archivos:
- Client.java:
  ```
  Implementa la clase UPMBankWSStub.java
  Realiza una serie de tests que se imprimen en consola
  Muestran el resultado esperado vs el obtenido
  ```
- jUnitClient:
  ```
  Un cliente que implementa la clase UPMBankWSStub.java
  Contiene todas las operaciones que ofrece el servicio
  Es el cliente empleado para realizar los tests unitarios
  ```
- jUnitClientTest:
  ```
  Realiza una serie de tests con valores generados aleatoriamente
  para comprobar la funcionalidad del servicio
  Necesita la librería javaFaker que se importa a través de maven
  ```
- MultipleSessionsTest:
  ```
  Realiza una serie de tests con datos hardcodeados
  Comprueba el correcto funcionamiento de las sesiones de usuarios
  la persistencia de los datos entre sesiones y que los datos de un
  usuario no se mezclan con los de otro
  ```

