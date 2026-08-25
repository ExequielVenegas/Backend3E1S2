# Legacy Batch Migrator

Una aplicación de procesamiento por lotes (Batch) desarrollada con **Spring Boot 4.1.0** que automatiza la migración de datos desde archivos CSV a una base de datos MySQL. Diseñada para migrar información financiera de un banco legacy.

## 📋 Descripción

Legacy Batch Migrator es un procesador de datos por lotes que ejecuta tres trabajos principales para procesar y persistir datos financieros:

1. **Reporte de Transacciones** - Lee transacciones desde CSV y las persiste en la base de datos
2. **Cálculo de Intereses** - Calcula intereses sobre cuentas y los registra
3. **Generación de Estados de Cuenta** - Genera reportes anuales de cuentas

## 🛠️ Tecnologías

- **Java**: 21
- **Spring Boot**: 4.1.0
- **Spring Batch**: Framework para procesamiento por lotes
- **Spring Data JDBC**: Acceso a datos con JDBC
- **MySQL**: Base de datos relacional
- **Lombok**: Simplificación de código boilerplate
- **Maven**: Gestor de dependencias y construcción

## 📦 Estructura del Proyecto

```
legacy-batch-migrator/
├── src/
│   ├── main/
│   │   ├── java/cl/duoc/bancoxyz/legacy_batch_migrator/
│   │   │   ├── LegacyBatchMigratorApplication.java  # Entrada principal
│   │   │   ├── config/                               # Configuración de jobs
│   │   │   │   ├── EstadoCuentaBatchConfig.java
│   │   │   │   ├── InteresBatchConfig.java
│   │   │   │   ├── TransaccionBatchConfig.java
│   │   │   │   └── TaskExecutorConfig.java
│   │   │   ├── listeners/                            # Listeners personalizados
│   │   │   │   ├── BancoJobListener.java
│   │   │   │   ├── BancoSkipListener.java
│   │   │   │   └── BancoStepListener.java
│   │   │   ├── model/                                # Entidades y DTOs
│   │   │   │   ├── CuentaAnualDTO.java
│   │   │   │   ├── CuentaAnualEntity.java
│   │   │   │   ├── InteresDTO.java
│   │   │   │   ├── InteresEntity.java
│   │   │   │   ├── TransaccionDTO.java
│   │   │   │   └── TransaccionEntity.java
│   │   │   ├── policies/                             # Políticas personalizadas
│   │   │   │   ├── BancoChunkCompletionPolicy.java
│   │   │   │   ├── BancoRetryPolicy.java
│   │   │   │   └── BancoSkipPolicy.java
│   │   │   └── processor/                            # Procesadores de items
│   │   │       ├── CuentaAnualProcessor.java
│   │   │       ├── InteresProcessor.java
│   │   │       └── TransaccionProcessor.java
│   │   └── resources/
│   │       ├── application.properties                # Configuración
│   │       ├── input/                                # Archivos CSV
│   │       │   ├── cuentas_anuales.csv
│   │       │   ├── intereses.csv
│   │       │   └── transacciones.csv
│   │       └── script/                               # Scripts SQL
│   │           ├── calculoInteres.sql
│   │           ├── estadocuentaanual.sql
│   │           └── transacciones.sql
│   └── test/
│       └── java/...                                  # Tests unitarios
└── pom.xml                                           # Configuración Maven
```

## 🚀 Inicio Rápido

### Requisitos Previos

- JDK 21 o superior
- MySQL 8.0 o superior
- Maven 3.8.0 o superior

### Instalación

1. **Clonar el repositorio**
```bash
git clone <repositorio-url>
cd legacy-batch-migrator
```

2. **Crear la base de datos MySQL**
```sql
CREATE DATABASE lab_batch;
USE lab_batch;
```

3. **Configurar la conexión a la base de datos**

Edita `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lab_batch
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

4. **Compilar el proyecto**
```bash
mvn clean install
```

5. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

O ejecutar el JAR generado:
```bash
java -jar target/legacy-batch-migrator-0.0.1-SNAPSHOT.jar
```

## ⚙️ Configuración

### application.properties

```properties
# Nombre de la aplicación
spring.application.name=legacy-batch-migrator

# Base de datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/lab_batch
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Spring Batch
spring.batch.jdbc.initialize-schema=always
spring.batch.job.enabled=true

# Seleccionar job a ejecutar (descomentar el deseado)
spring.batch.job.name=reporteTransaccionesJob
# spring.batch.job.name=calculoInteresesJob
# spring.batch.job.name=generacionEstadosCuentaJob

# Configuración de executor de tareas
bancoxyz.task-executor.core-pool-size=3
bancoxyz.task-executor.max-pool-size=3
bancoxyz.task-executor.queue-capacity=10
```

## 📊 Jobs Disponibles

### 1. Reporte de Transacciones (`reporteTransaccionesJob`)

Lee transacciones desde `input/transacciones.csv` y las persiste en la tabla `transacciones_diarias`.

**CSV de entrada:**
```
id,fecha,monto,tipo
1,2024-01-15,1000.00,DEPOSITO
2,2024-01-16,500.00,RETIRO
```

**Configuración:** `src/main/java/config/TransaccionBatchConfig.java`

### 2. Cálculo de Intereses (`calculoInteresesJob`)

Procesa el cálculo de intereses desde `input/intereses.csv` y actualiza la base de datos.

**Configuración:** `src/main/java/config/InteresBatchConfig.java`

### 3. Generación de Estados de Cuenta (`generacionEstadosCuentaJob`)

Genera estados de cuenta anuales desde `input/cuentas_anuales.csv`.

**Configuración:** `src/main/java/config/EstadoCuentaBatchConfig.java`

## 🔧 Características Avanzadas

### Listeners Personalizados

- **BancoJobListener**: Monitorea el ciclo de vida completo del job
- **BancoStepListener**: Supervisa cada paso del procesamiento
- **BancoSkipListener**: Maneja registros que son omitidos

### Políticas Personalizadas

- **BancoChunkCompletionPolicy**: Define el tamaño y condiciones de los chunks
- **BancoRetryPolicy**: Estrategia de reintentos para errores transitorios
- **BancoSkipPolicy**: Define qué registros pueden ser omitidos

### Ejecución Paralela

Configurada con un `TaskExecutor` que permite procesar múltiples registros en paralelo:
- Pool de hilos: 3 (configurable)
- Capacidad de cola: 10

## 🗄️ Base de Datos

La aplicación crea automáticamente las tablas requeridas por Spring Batch y migra los datos a las siguientes tablas:

- `transacciones_diarias` - Transacciones diarias
- `calculo_interes` - Cálculos de intereses
- `estado_cuenta_anual` - Estados de cuenta anuales

### Scripts SQL

Los scripts de inicialización están en `src/main/resources/script/`:
- `transacciones.sql`
- `calculoInteres.sql`
- `estadocuentaanual.sql`

## 📝 Modelos de Datos

### TransaccionDTO / TransaccionEntity
```java
- id: Long
- fecha: LocalDate
- monto: BigDecimal
- tipo: String (DEPOSITO, RETIRO, etc.)
- estado: String
```

### InteresDTO / InteresEntity
```java
- id: Long
- cuenta: String
- tasa: BigDecimal
- monto: BigDecimal
- periodo: String
```

### CuentaAnualDTO / CuentaAnualEntity
```java
- id: Long
- numeroCuenta: String
- saldoInicial: BigDecimal
- saldoFinal: BigDecimal
- anio: Integer
```

## 🧪 Testing

Ejecutar los tests unitarios:
```bash
mvn test
```

## 📈 Monitoreo y Logs

La aplicación registra eventos importantes:
- Inicio y fin de jobs
- Inicio y fin de steps
- Registros procesados exitosamente
- Registros omitidos
- Errores y reintentos

## 🛡️ Manejo de Errores

- **Skip Policy**: Permite omitir registros problemáticos
- **Retry Policy**: Reintentos automáticos para errores transitorios
- **Fault Tolerance**: Configuración de tolerancia a fallos por paso

## 📚 Recursos Útiles

- [Spring Batch Documentation](https://docs.spring.io/spring-batch/docs/current/reference/html/)
- [Spring Boot 4.1.0 Documentation](https://docs.spring.io/spring-boot/4.1.0/reference/html/)
- [Spring Data JDBC Guide](https://github.com/spring-projects/spring-data-examples/tree/main/jdbc/basics)

