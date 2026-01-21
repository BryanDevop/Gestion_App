# HuellaPP - GestionCanina

EL unico problema que ella eh ajena

Aplicación móvil Android para la gestión integral de mascotas, con funcionalidades de adopción, historial médico veterinario, citas, facturación y panel de administración.

## Tabla de Contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Dependencias](#dependencias)
- [Módulos Principales](#módulos-principales)
- [Base de Datos](#base-de-datos)
- [API y Servicios](#api-y-servicios)
- [Pantallas Principales](#pantallas-principales)
- [Contribución](#contribución)
- [Licencia](#licencia)

## Descripción

HuellaPP es una aplicación Android nativa desarrollada en Kotlin que permite a los usuarios gestionar sus mascotas de manera integral. La aplicación incluye funcionalidades para el registro de mascotas, seguimiento de historial médico, gestión de citas veterinarias, proceso de adopción y administración de usuarios y contenidos.

## Características

### Para Usuarios
- **Autenticación**: Registro e inicio de sesión con email/password
- **Gestión de Mascotas**: Registro, edición y eliminación de mascotas
- **Historial Médico**: Registro de consultas, vacunas, tratamientos y diagnósticos
- **Citas Veterinarias**: Programación y gestión de citas
- **Adopciones**: Visualización y solicitud de adopción de mascotas
- **Perfil de Usuario**: Gestión de información personal y configuración
- **Facturación**: Visualización de facturas y pagos

### Para Administradores
- **Panel de Administración**: Gestión completa de usuarios y contenido
- **Gestión de Mascotas en Adopción**: Alta, baja y modificación de mascotas disponibles
- **Estadísticas**: Vista general de la plataforma

## Arquitectura

El proyecto sigue una arquitectura limpia (Clean Architecture) con separación de responsabilidades:

```
app/src/main/java/com/boxing/gestioncanina/
├── data/              # Capa de datos
│   ├── local/         # Room Database y DAOs
│   ├── model/         # Modelos de datos
│   ├── network/       # Cliente Supabase y servicios
│   └── repository/    # Implementación de repositorios
├── domain/            # Lógica de negocio
│   ├── mapper/        # Mapeadores entre capas
│   └── usecase/       # Casos de uso
├── ui/                # Capa de presentación
│   ├── adoption/      # Pantallas de adopción
│   ├── appointments/  # Gestión de citas
│   ├── auth/          # Autenticación
│   ├── billing/       # Facturación y pagos
│   ├── dashboard/     # Panel principal
│   ├── medical/       # Historial médico
│   ├── pets/          # Gestión de mascotas
│   ├── settings/      # Configuración
│   └── components/    # Componentes reutilizables
├── Admin/             # Panel de administración
├── veterinaria/       # Módulo de veterinaria
└── utils/             # Utilidades
```

## Tecnologías

### Lenguaje y Framework
- **Kotlin**: Lenguaje de programación principal
- **Android SDK**: API 24 (Android 7.0) a API 36
- **AndroidX**: Bibliotecas de compatibilidad

### Arquitectura y Patrones
- **MVVM**: Pattern Model-View-ViewModel
- **Clean Architecture**: Separación en capas
- **Repository Pattern**: Abstracción de acceso a datos
- **View Binding**: Vinculación de vistas tipeada

### Base de Datos
- **Room**: Base de datos local SQLite
- **Supabase**: Base de datos remota (PostgreSQL)

### Autenticación y Backend
- **Supabase Auth**: Autenticación de usuarios
- **Supabase Database**: Base de datos en la nube
- **Firebase**: Analytics y AppCheck

### Networking
- **Ktor**: Cliente HTTP para Supabase
- **Coroutines**: Programación asíncrona

### Imágenes
- **Glide**: Carga y caché de imágenes

### UI/UX
- **Material Design 3**: Componentes de diseño
- **View Binding**: Generación de código para vistas
- **Navigation Component**: Navegación entre fragmentos

## Estructura del Proyecto

```
GestionCanina/
├── app/
│   ├── build.gradle.kts          # Configuración de Gradle
│   └── src/main/
│       ├── AndroidManifest.xml   # Manifiesto de la app
│       ├── java/com/boxing/gestioncanina/
│       │   ├── MyApplication.kt  # Clase Application
│       │   ├── Admin/            # Panel Admin
│       │   ├── data/             # Capa de datos
│       │   ├── domain/           # Lógica de negocio
│       │   ├── ui/               # Interfaz de usuario
│       │   ├── veterinaria/      # Módulo veterinario
│       │   └── utils/            # Utilidades
│       └── res/
│           ├── layout/           # Layouts XML
│           ├── values/           # Recursos (strings, colors, themes)
│           ├── drawable/         # Imágenes y vectores
│           ├── navigation/       # Gráfico de navegación
│           └── menu/             # Menús
├── build.gradle.kts              # Build raíz
├── settings.gradle.kts           # Configuración de settings
├── gradle.properties             # Propiedades de Gradle
└── local.properties              # Configuración local
```

## Configuración

### Requisitos Previos
- Android Studio Hedgehog o superior
- JDK 17
- Gradle 8.14.3
- Android SDK 36

### Instalación

1. Clonar el repositorio:
```bash
git clone https://github.com/BryanDevop/Gestion_App.git
```

2. Abrir el proyecto en Android Studio

3. Sincronizar el proyecto con Gradle:
```bash
./gradlew clean build
```3

4. Configurar Firebase:
   - Crear un proyecto en Firebase Console
   - Descargar el archivo `google-services.json`
   - Colocarlo en `app/google-services.json`

5. Configurar Supabase:
   - Crear un proyecto en Supabase
   - Configurar las credenciales en `gradle.properties`:
   ```properties
   SUPABASE_URL=tu_url_supabase
   SUPABASE_KEY=tu_anon_key
   ```

### Generación de APK

```bash
./gradlew assembleRelease
```

El APK se generará en `app/build/outputs/apk/release/`

## Dependencias

### AndroidX Core
- `androidx.core:core-ktx:1.13.1`
- `androidx.appcompat:appcompat:1.7.0`
- `androidx.activity:activity-ktx:1.9.3`
- `androidx.fragment:fragment-ktx:1.8.5`

### Material Design
- `com.google.android.material:material:1.12.0`

### Room Database
- `androidx.room:room-runtime:2.6.1`
- `androidx.room:room-ktx:2.6.1`
- `kapt("androidx.room:room-compiler:2.6.1")`

### Navigation
- `androidx.navigation:navigation-fragment-ktx:2.8.5`
- `androidx.navigation:navigation-ui-ktx:2.8.5`

### Coroutines
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0`
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0`

### Supabase
- `io.github.jan-tennert.supabase:bom:2.6.0`
- `io.github.jan-tennert.supabase:postgrest-kt`
- `io.github.jan-tennert.supabase:gotrue-kt`
- `io.github.jan-tennert.supabase:realtime-kt`
- `io.github.jan-tennert.supabase:storage-kt`

### Firebase
- `com.google.firebase:firebase-bom:33.7.0`
- `com.google.firebase:firebase-auth-ktx`
- `com.google.firebase:firebase-firestore-ktx`
- `com.google.firebase:firebase-analytics-ktx`

### Lifecycle
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7`
- `androidx.lifecycle:lifecycle-livedata-ktx:2.8.7`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.8.7`

### Glide (Imágenes)
- `com.github.bumptech.glide:glide:4.16.0`
- `kapt("com.github.bumptech.glide:compiler:4.16.0")`

### Serialización
- `org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3`

## Módulos Principales

### Módulo de Autenticación (`ui.auth`)
Gestiona el registro e inicio de sesión de usuarios.

**Archivos principales:**
- `LoginActivity.kt`: Pantalla de inicio de sesión
- `RegisterActivity.kt`: Pantalla de registro
- `ForgotPasswordActivity.kt`: Recuperación de contraseña
- `AuthRepository.kt`: Repositorio de autenticación

**Funcionalidades:**
- Login con email/password
- Registro de nuevos usuarios
- Recuperación de contraseña
- Validación de formularios

### Módulo de Mascotas (`ui.pets` y `veterinaria`)
Gestión integral de mascotas y su información.

**Archivos principales:**
- `PetsMe.kt`: Lista de mascotas del usuario
- `PetListFragment.kt`: Fragmento de lista de mascotas
- `PetDetailFragment.kt`: Detalle de mascota
- `Agenda.kt`: Agenda de mascotas
- `Mascota.kt`: Modelo de datos

**Funcionalidades:**
- Registro de nuevas mascotas
- Visualización de información detallada
- Edición de datos de mascotas
- Eliminación de mascotas

### Módulo Veterinario (`veterinaria`)
Sistema completo de gestión médica para mascotas.

**Archivos principales:**
- `Mascota.kt`: Modelo de mascota
- `ConsultaVeterinaria.kt`: Modelo de consulta
- `MascotaListFragment.kt`: Lista de mascotas
- `AddConsultaFragment.kt`: Agregar consulta
- `HistorialMedicoFragment.kt`: Historial médico
- `MascotaDao.kt`, `ConsultaVeterinariaDao.kt`: DAOs de Room

**Funcionalidades:**
- Registro de consultas veterinarias
- Historial médico completo
- Seguimiento de tratamientos
- Gestión de medicamentos
- Registro de vacunas
- Observaciones y diagnósticos

### Módulo de Adopción (`ui.adoption`)
Sistema de adopción de mascotas.

**Archivos principales:**
- `AdoptionPetsActivity.kt`: Lista de mascotas en adopción
- `AdoptionPetsFragment.kt`: Fragmento de adopción
- `AdoptionPetsViewModel.kt`: ViewModel de adopción
- `AdoptionAdapter.kt`: Adapter para lista
- `AdoptionPet.kt`: Modelo de mascota en adopción

**Funcionalidades:**
- Visualización de mascotas disponibles
- Filtros de búsqueda
- Formulario de solicitud de adopción
- Detalle de mascotas

### Módulo de Citas (`ui.appointments` y `ui.citas`)
Gestión de citas veterinarias.

**Archivos principales:**
- `AppointmentListFragment.kt`: Lista de citas
- `AppointmentDetailFragment.kt`: Detalle de cita
- `AppointmentViewModel.kt`: ViewModel
- `RegistrarCitaActivity.kt`: Registro de citas
- `Appointment.kt`: Modelo de cita

**Funcionalidades:**
- Creación de citas
- Visualización de agenda
- Detalle de citas
- Cancelación de citas

### Módulo de Facturación (`ui.billing`)
Gestión de pagos y facturas.

**Archivos principales:**
- `InvoiceFragment.kt`: Lista de facturas
- `PaymentFragment.kt`: Pagos
- `Invoice.kt`: Modelo de factura

**Funcionalidades:**
- Visualización de facturas
- Historial de pagos
- Generación de comprobantes

### Módulo Médico (`ui.medical`)
Gestión de aspectos médicos de las mascotas.

**Archivos principales:**
- `MedicalHistoryFragment.kt`: Historial médico
- `VaccineFragment.kt`: Vacunas
- `Medicamento.kt`: Medicamentos
- `Tratamiento.kt`: Tratamientos
- `Diagnostico.kt`: Diagnósticos
- `Observaciones.kt`: Observaciones

### Panel de Administración (`Admin`)
Área exclusiva para administradores.

**Archivos principales:**
- `MainAdmin.kt`: Activity principal
- `AdminActivity.kt`: Panel de administración

**Funcionalidades:**
- Gestión de usuarios
- Administración de contenido
- Estadísticas generales

## Base de Datos

### Room Database
La aplicación utiliza Room para almacenamiento local:

**Entidades:**
- `MascotaEntity`: Información de mascotas
- `ConsultaVeterinariaEntity`: Consultas médicas
- `PetEntity`: Mascotas generales
- `UserEntity`: Usuarios

**DAOs:**
- `MascotaDao`: Operaciones de mascotas
- `ConsultaVeterinariaDao`: Operaciones de consultas
- `PetDao`: Operaciones de mascotas
- `UserDao`: Operaciones de usuarios

### Supabase (Base de Datos Remota)
Supabase proporciona almacenamiento en la nube:

**Tablas principales:**
- `users`: Perfiles de usuarios
- `mascotas`: Mascotas registradas
- `citas`: Citas veterinarias
- `consultas`: Historial médico
- `adoption_pets`: Mascotas en adopción
- `facturas`: Registros de facturación

## API y Servicios

### Supabase Client
Cliente principal para comunicación con el backend:
- `SupabaseClient.kt`: Configuración del cliente
- `ApiService.kt`: Servicios API

### Firebase Services
- `FirebaseService.kt`: Servicios de Firebase
- Autenticación
- Firestore
- Analytics
- AppCheck

### Network Response
- `NetworkResponse.kt`: Wrapper para respuestas de red

## Pantallas Principales

| Pantalla | Activity/Fragment | Descripción |
|----------|-------------------|-------------|
| Pantalla de Carga | `Load_screen` | Splash screen de la app |
| Login | `LoginActivity` | Inicio de sesión |
| Registro | `RegisterActivity` | Crear cuenta |
| Dashboard | `DashboardActivity` | Panel principal |
| Mis Mascotas | `PetsMe` | Lista de mascotas |
| Detalle Mascota | `PetDetailFragment` | Información detallada |
| Agenda | `Agenda` | Calendario de citas |
| Adopción | `AdoptionPetsFragment` | Mascotas en adopción |
| Historial Médico | `MedicalHistoryFragment` | Registro médico |
| Perfil | `ProfileFragment` | Perfil de usuario |
| Facturas | `InvoiceFragment` | Historial de pagos |
| Panel Admin | `AdminActivity` | Administración |


---

## Versiones del Proyecto

- **Version**: 1.0
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 36

## Notas de Build

- Build Types: Debug y Release
- Minificación habilitada en Release
- ProGuard configurado para optimización
- Recursos comprimidos en Release
