# Backend

Este proyecto corresponde al backend de una aplicación web desarrollada como parte del trabajo final del curso de Finanzas e Ingeniería Económica. El sistema permite proyectar el flujo de caja de un bono corporativo utilizando el **método alemán**, con soporte para plazos de gracia, configuraciones financieras y cálculos financieros clave.

## 📌 Funcionalidades principales

- Autenticación de usuarios con usuario y contraseña.
- Registro, edición y eliminación de bonos corporativos.
- Proyección del flujo de caja de un bono con método alemán.
- Soporte para plazos de gracia parciales o totales.
- Cálculo de:
  - Duración
  - Duración modificada
  - Convexidad
  - TCEA (Tasa de Coste Efectivo Anual) desde el punto de vista del emisor.
  - TREA (Tasa de Rendimiento Efectivo Anual) desde el punto de vista del inversor.
  - Precio máximo de mercado que se estaría dispuesto a pagar por el bono.

## ⚙️ Tecnologías usadas
- Java 17
- Spring Boot 3.5
- Spring Data JPA
- Spring Security
- PostgreSQL
- OpenAPI (Swagger)
- Lombok

## 📦 Instalación
1. Clona el repositorio:
   ```bash
   git clone https://github.com/Finanzas-UPC/backend.git
   cd backend
   cd finanzas
   ```
2. Configura la base de datos MySQL:
    - Crea una base de datos llamada `finanzasdb`.
    - Configura las credenciales de la base de datos en el archivo `src/main/resources/application.properties`:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/finanzasdb
      spring.datasource.username=tu_usuario
      spring.datasource.password=tu_contraseña
      ```
3. Ejecuta la aplicación:
    ```bash
    ./mvnw spring-boot:run
    ```

## ✍️ Autores

- Delgado Corrales, Piero Gonzalo (U202210749)
- Gongora Castillejos, Williams Jesus (U20221C186)
- La Torre Soto, Andre Sebastian (U202217772)
- Paredes Puente, Sebastian Roberto (U202217239)
- Salinas Torres, Salvador Antonio (U20221B127)

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Para más detalles, consulta el archivo [LICENSE](LICENSE).
