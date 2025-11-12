StudyCash Spring Boot project (generated)

How to run:
1. Update src/main/resources/application.properties with your MySQL username/password if different.
2. Ensure the database 'studyCash' exists and contains the tables (you already imported studyCash.sql).
3. In terminal at project root:
   ./mvnw clean spring-boot:run
   (or 'mvnw clean spring-boot:run' on Windows)

API endpoints:
- GET  /api/utilizadores
- GET  /api/contas
- GET  /api/transacoes
- GET  /api/metas
- etc.

Notes:
- Java 17 recommended.
- spring.jpa.hibernate.ddl-auto=none (since you already created tables).
