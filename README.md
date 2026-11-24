# TacticalDEV - Sistema de Gestión y Mantención de Equipos de Refrigeración

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** bajo la arquitectura **MVVM (Model-View-ViewModel)**, conectada a una base de datos local **Room (SQLite)** y con sincronización híbrida vía **Retrofit** hacia un backend en **Spring Boot 3.3.5** desplegado en **AWS EC2**.

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
- **Backend**: Spring Boot 3.3.5, Spring Data JPA, H2 Database (persistencia en archivo local `./data/tacticaldev_db`), Swagger / OpenAPI 3.
- **Infraestructura Cloud**: AWS EC2 (Amazon Linux 2023), IP Elástica (`100.27.237.93`), puerto `8080` y arranque automático gestionado mediante servicio `systemd` (`tacticaldev.service`).
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

### 1. Endpoints Backend Spring Boot (`http://100.27.237.93:8080/api/`)
- `/api/usuarios`: Gestión de cuentas de usuario y autenticación.
- `/api/inventario`: Inventario de repuestos e insumos.
- `/api/equipos`: Registro y estado de equipos de refrigeración.
- `/api/ventas`: Operaciones de venta, arriendo y cotización.
- `/api/visitas`: Programación y seguimiento de visitas técnicas en terreno.
- `/api/asistencias`: Sesiones de asistencia remota.
- `/api/minimarkets`: Registro de clientes y minimarkets asociados.
- `/api/ordenes-compra`: Órdenes de compra de stock a proveedores.

**Documentación Swagger UI**: `http://100.27.237.93:8080/swagger-ui/index.html`  
**Consola Web H2 Database**: `http://100.27.237.93:8080/h2-console`  
- **JDBC URL**: `jdbc:h2:file:./data/tacticaldev_db`
- **Usuario**: `sa`

### 2. API Externa
- **Servicio**: `https://mindicador.cl/api`
- **Uso**: Consulta de indicadores económicos oficiales (UF, UTM, Dólar) para cálculo de precios y cotizaciones.

---

## 🔑 Credenciales de Prueba por Defecto

- **Administrador**: privada

---

## 🛠️ Pasos de Ejecución

### 1. Backend en Producción (AWS EC2)
1. Inicia la instancia EC2 en AWS y verifica que la IP Elástica `100.27.237.93` esté asociada.
2. El servicio `tacticaldev.service` en `systemd` inicia automáticamente el servidor Spring Boot (`backend-0.0.1-SNAPSHOT.jar`) en el puerto `8080` con acceso remoto a H2 habilitado.
3. Verifica la disponibilidad del servicio en `http://100.27.237.93:8080/api/usuarios` o desde `http://100.27.237.93:8080/h2-console`.



### 2. Compilar y Ejecutar la Aplicación Android
1. Abre el proyecto en **Android Studio**.
2. Verifica en `RetrofitClient.kt` que la URL base apunte a `http://100.27.237.93:8080/`.
3. Sincroniza las dependencias Gradle.
4. Ejecuta la aplicación en un Emulador o Dispositivo Físico con acceso a internet (`Shift + F10`).

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

##  Firma Release y Keystore
