package com.example.wallio.ui.credits

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wallio.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Créditos",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header con información del desarrollador
            item {
                DeveloperHeader()
            }

            // Sección de acerca de
            item {
                SectionTitle(
                    icon = Icons.Default.Person,
                    title = "Acerca del Desarrollador"
                )
                AboutSection()
            }

            // Sección de tecnologías
            item {
                SectionTitle(
                    icon = Icons.Default.Code,
                    title = "Tecnologías Utilizadas"
                )
                TechnologiesSection()
            }

            // Sección del proyecto
            item {
                SectionTitle(
                    icon = Icons.Default.Star,
                    title = "Acerca de Wallio"
                )
                ProjectSection()
            }

            // Sección de contacto
            item {
                SectionTitle(
                    icon = Icons.Default.Email,
                    title = "Contacto"
                )
                ContactSection()
            }

            // Footer
            item {
                AppFooter()
            }
        }
    }
}

@Composable
fun SectionTitle(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DeveloperHeader() {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil desde recursos locales
            Image(
                painter = painterResource(id = R.drawable.credit),
                contentDescription = "Foto de perfil de Victor Vasquez",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Victor Vasquez",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Desarrollador Full Stack",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                SkillTag(text = "Android")
                Spacer(modifier = Modifier.width(8.dp))
                SkillTag(text = "Web")
                Spacer(modifier = Modifier.width(8.dp))
                SkillTag(text = "Backend")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Redes sociales
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SocialIconButton(
                    icon = Icons.Default.Code, // Icono de GitHub
                    contentDescription = "GitHub",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/VictorVasquezZT2005"))
                        context.startActivity(intent)
                    },
                    color = MaterialTheme.colorScheme.primary
                )

                SocialIconButton(
                    icon = Icons.Default.Person, // Icono de LinkedIn
                    contentDescription = "LinkedIn",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/victor-vasquez-4555522ba/"))
                        context.startActivity(intent)
                    },
                    color = Color(0xFF0077B5) // Color azul de LinkedIn
                )

                SocialIconButton(
                    icon = Icons.Default.Email,
                    contentDescription = "Email",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:vvasquezdv2016@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Contacto desde Wallio App")
                        }
                        context.startActivity(intent)
                    },
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SocialIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = Modifier.size(48.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = CircleShape,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SkillTag(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun AboutSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Con 5 años de experiencia como desarrollador, me especializo en diversos ámbitos tecnológicos, incluyendo la creación y personalización de custom ROMs, el desarrollo de páginas web utilizando frameworks modernos, y la construcción de aplicaciones robustas.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun TechnologiesSection() {
    val technologies = listOf(
        "Kotlin" to MaterialTheme.colorScheme.primary,
        "Jetpack Compose" to MaterialTheme.colorScheme.primary,
        "Material Design 3" to MaterialTheme.colorScheme.primary,
        "Firebase" to MaterialTheme.colorScheme.error,
        "Android Studio" to MaterialTheme.colorScheme.tertiary,
        "MVVM Architecture" to MaterialTheme.colorScheme.secondary,
        "Coroutines" to MaterialTheme.colorScheme.primary,
        "Room Database" to MaterialTheme.colorScheme.secondary
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        technologies.chunked(2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { (tech, color) ->
                    TechnologyChip(
                        technology = tech,
                        color = color,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Si la fila tiene solo un elemento, agregar un espacio vacío
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TechnologyChip(technology: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = technology,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProjectSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Wallio es una aplicación de gestión financiera personal desarrollada con las últimas tecnologías de Android. Permite a los usuarios trackear sus ingresos y gastos de manera intuitiva, ofreciendo reportes detallados y un control completo sobre sus finanzas.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Características principales:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureItem(text = "Registro de ingresos y gastos")
            FeatureItem(text = "Categorización automática")
            FeatureItem(text = "Reportes y gráficos detallados")
            FeatureItem(text = "Interfaz moderna y intuitiva")
            FeatureItem(text = "Sincronización en la nube")
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "•",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ContactSection() {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            ContactItem(
                icon = Icons.Default.Code,
                platform = "GitHub",
                username = "VictorZT2005",
                description = "Repositorios y proyectos",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/VictorVasquezZT2005"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactItem(
                icon = Icons.Default.Person,
                platform = "LinkedIn",
                username = "Victor Vasquez",
                description = "Perfil profesional",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/victor-vasquez-4555522ba/"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactItem(
                icon = Icons.Default.Email,
                platform = "Email",
                username = "vvasquezdv2016@gmail.com",
                description = "Contacto directo",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:vvasquezdv2016@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Contacto desde Wallio App")
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    platform: String,
    username: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = platform,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Abrir enlace",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AppFooter() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wallio v0.9",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "© 2025 Victor Vasquez. Todos los derechos reservados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}