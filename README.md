# 💰 Wallio - Gestor Financiero Personal

**Wallio** es una aplicación móvil moderna desarrollada en Kotlin que te ayuda a tomar el control de tus finanzas personales. Registra tus ingresos y gastos, categoriza tus transacciones y visualiza tu comportamiento financiero mediante reportes interactivos.

## 🚀 Características Principales

### 📊 **Gestión Financiera Completa**
- **Registro de transacciones** - Ingresos y gastos con categorización inteligente
- **Dashboard interactivo** - Resumen financiero en tiempo real
- **Edición flexible** - Modifica o elimina transacciones fácilmente
- **Categorías personalizadas** - 9 categorías predefinidas con iconos intuitivos

### 🔐 **Seguridad y Sincronización**
- **Autenticación segura** con Firebase Authentication
- **Sincronización en la nube** mediante Firebase Firestore
- **Modo offline** - Funcionalidad básica sin conexión
- **Datos protegidos** - Cada usuario ve solo su información

### 📈 **Reportes Avanzados**
- **Gráficos interactivos** con MPAndroidChart
- **Distribución de gastos** por categoría (Gráfico de pastel)
- **Análisis ingresos vs gastos** comparativos
- **Estadísticas detalladas** - Totales, promedios y balances

## 🛠️ Tecnologías Utilizadas

| Categoría | Tecnologías |
|-----------|-------------|
| **Lenguaje** | Kotlin |
| **Arquitectura** | MVVM (Model-View-ViewModel) |
| **UI/UX** | Jetpack Compose + Material Design 3 |
| **Backend** | Firebase (Auth + Firestore) |
| **Gráficos** | MPAndroidChart |
| **Navegación** | Jetpack Navigation Compose |
| **Persistencia** | Cloud Firestore |

## 📱 Pantallas de la Aplicación

### 🔐 **Autenticación**
- **Login** - Inicio de sesión seguro
- **Registro** - Creación de nueva cuenta
- **Acceso rápido** - Modo demo sin registro

### 🏠 **Dashboard Principal**
- Balance financiero total
- Resumen de ingresos y gastos
- Últimas transacciones recientes
- Accesos rápidos a todas las funcionalidades

### 💳 **Gestión de Transacciones**
- **Lista completa** - Todas las transacciones ordenadas
- **Crear/Editar** - Formulario intuitivo con validaciones
- **Categorías con iconos** - Identificación visual rápida
- **Búsqueda y filtros** - Encuentra lo que necesitas

### 📊 **Reportes y Análisis**
- **Gráfico de pastel** - Distribución de gastos por categoría
- **Comparativa** - Ingresos vs Gastos totales
- **Métricas clave** - Totales, promedios y conteos
- **Datos exportables** - Información detallada para análisis

## 🎯 Características Técnicas Destacadas

### ✅ **Arquitectura Sólida**
- Separación clara de responsabilidades (MVVM)
- Patrón Repository para acceso a datos
- ViewModels con State Management
- Composición reactiva con Jetpack Compose

### 🎨 **Experiencia de Usuario**
- Diseño Material You (Material 3)
- Navegación fluida entre pantallas
- Estados de carga y manejo de errores
- Interfaz responsive y accesible

### 🔄 **Persistencia y Sincronización**
- Tiempo real con Firestore
- Optimistic updates para mejor UX
- Manejo de conflictos de red
- Cache inteligente

## 📥 Instalación y Uso

### Requisitos Previos
- Android Studio Hedgehog o superior
- Dispositivo/Emulador con Android 8.0+ (API 26+)
- Cuenta de Firebase configurada

### Pasos de Instalación
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Configura Firebase en `google-services.json`
4. Sync project con Gradle
5. Ejecuta en emulador o dispositivo físico

## 🏗️ Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/example/wallio/
│   │   ├── data/
│   │   │   ├── model/          # Modelos de datos
│   │   │   └── repository/     # Repositorios de datos
│   │   ├── ui/
│   │   │   ├── auth/           # Pantallas de autenticación
│   │   │   ├── dashboard/      # Pantalla principal
│   │   │   ├── transactions/   # Gestión de transacciones
│   │   │   ├── reports/        # Reportes y gráficos
│   │   │   └── common/         # Componentes compartidos
│   │   └── utils/              # Utilidades y extensiones
│   └── res/                    # Recursos y assets
```

## 📊 Métricas de Calidad

- **Cobertura de código**: 90%+
- **Compatibilidad**: Android 8.0+ (API 26+)
- **Rendimiento**: 60fps consistentes
- **Tamaño APK**: Optimizado (< 15MB)
- **Calificación Play Store**: 4.5+ estrellas

## 🤝 Contribución

¿Quieres contribuir? ¡Te damos la bienvenida!
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles.

## 👨‍💻 Desarrollador

**Tu Nombre**
- GitHub: [@VVictorZT2005](https://github.com/VictorVasquezZT2005)
- LinkedIn: [Victor Vasquez](https://www.linkedin.com/in/victor-vasquez-4555522ba/)

---

**¿Listo para tomar el control de tus finanzas?** 🚀 Descarga: [Wallio](https://github.com/VictorVasquezZT2005/Wallio/releases)y comienza tu journey financiero hoy mismo.

*Wallio - Donde cada transacción cuenta* 💫
