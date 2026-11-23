# TacticalDEV - Sistema de Gestión y Mantención de Equipos de Refrigeración

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** bajo la arquitectura **MVVM (Model-View-ViewModel)**, conectada a una base de datos local **Room (SQLite)** y con sincronización híbrida vía **Retrofit** hacia un backend en **Spring Boot 3.3.5**.

---

## 👨‍💻 Información del Proyecto e Integrantes

- **Integrantes**:
  - Maximiliano Provoste
  - Bastian Vernal
  - Sebastian Castillo
- **Docente**: Dusty Tessini
- **Asignatura**: DSY1105 - Desarrollo de Aplicaciones Móviles
- **Sección**: 002V
- **Repositorio GitHub**: [https://github.com/sebacasti07/CASO_DSY1105-002V_TACTICALDEV](https://github.com/sebacasti07/CASO_DSY1105-002V_TACTICALDEV)

---

## ⚙️ Arquitectura y Tecnologías
- **Frontend / Móvil**: Android SDK (Kotlin, Jetpack Compose, Material 3, Coroutines & Flow, Room Database, Retrofit 2, OkHttp3, ViewModelFactory, Navigation Compose).
- **Backend**: Spring Boot 3.3.5, Spring Data JPA, H2 Database (persistencia en archivo local), Swagger / OpenAPI 3.
- **Firma Release**: Keystore JKS (`app/tacticaldev.jks`) y `keystore.properties`.

---

## 👥 Funcionalidades por Rol

1. **Administrador**
   - Gestión integral de usuarios (CRUD de clientes, técnicos, vendedores, transportistas).
   - Administración del inventario de repuestos y equipos de refrigeración.
   - Registro y control de compras de stock de insumos.
   - Supervisión global de cotizaciones, ventas, visitas presenciales y asistencia remota.

2. **Cliente**
   - Visualización del catálogo de equipos e insumos con búsqueda y filtros.
   - Solicitud de cotizaciones de compra y arriendo.
   - Programación de visitas técnicas en terreno y visitas de asistencia remota.
   - Gestión de mis equipos asociados por Minimarket.
   - Consulta y seguimiento del estado de solicitudes (reagendar o cancelar).

3. **Vendedor**
   - Atención y respuesta a cotizaciones solicitadas por clientes.
   - Registro directo de ventas y arriendos de equipos/repuestos.
   - Consulta de stock de inventario en tiempo real.
   - Control de arriendos activos y procesamiento de devoluciones/bajas.

4. **Técnico**
   - Agenda de visitas técnicas asignadas en terreno.
   - Actualización del estado operativo de visitas y registro de observaciones.
   - Sala de asistencia técnica remota para atención a clientes.
   - Consulta rápida de repuestos y componentes disponibles.

5. **Transportista**
   - Listado de despachos y entregas de equipos/repuestos asignados.
   - Actualización del estado del despacho (En Ruta, Entregado, Incidencia).
   - Geolocalización e información del Minimarket de destino.

---

## 🌐 Endpoints y Servicios Web

### 1. Endpoints Backend Spring Boot (`http://10.0.2.2:8080/api/` o `http://localhost:8080/api/`)
- `/api/usuarios`: Gestión de cuentas de usuario y autenticación.
- `/api/inventario`: Inventario de repuestos e insumos.
- `/api/equipos`: Registro y estado de equipos de refrigeración.
- `/api/ventas`: Operaciones de venta, arriendo y cotización.
- `/api/visitas`: Programación y seguimiento de visitas técnicas en terreno.
- `/api/asistencias`: Sesiones de asistencia remota.
- `/api/minimarkets`: Registro de clientes y minimarkets asociados.
- `/api/ordenes-compra`: Órdenes de compra de stock a proveedores.

**Documentación Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

### 2. API Externa
- **Servicio**: `https://mindicador.cl/api`
- **Uso**: Consulta de indicadores económicos oficiales (UF, UTM, Dólar) para cálculo de precios y cotizaciones.

---

## 🔑 Credenciales de Prueba por Defecto

- **Administrador**: `admin@admindev.cl` / `Admin1234@`

---

## 🛠️ Pasos de Ejecución

### 1. Ejecutar Backend (Spring Boot)
1. Navega a la carpeta `backend/` desde la terminal.
2. Ejecuta el servidor con Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(En Windows: `mvnw.cmd spring-boot:run`)*
3. Verifica la disponibilidad del backend en `http://localhost:8080/api/usuarios`.

### 2. Compilar y Ejecutar la Aplicación Android
1. Abre el proyecto en **Android Studio**.
2. Sincroniza las dependencias Gradle.
3. Ejecuta la aplicación en un Emulador o Dispositivo Físico (`Shift + F10`).

---

## 🔏 Firma Release y Generación de APK

La aplicación cuenta con firma digital autónoma configurada para compilación en entorno Release.

### Archivos de Firma
- **Keystore**: `app/tacticaldev.jks`
- **Configuración de Firma**: `keystore.properties` (en la raíz del proyecto)

### Comando de Compilación Release
```bash
./gradlew assembleRelease
```

### Ubicación del Artifact Generado
- **APK Firmado**: `app/build/outputs/apk/release/app-release.apk`
- **APK en Raíz para Entrega**: `app-release.apk`

---

## 📸 Evidencia de Firma Release y Keystore

![Evidencia de Firma Release y Keystore](privado)
