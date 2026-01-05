package com.realtorsuccessiq.ui.admin.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun AdminBrandingScreen(
    brokerageId: String,
    viewModel: AdminBrandingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(brokerageId) {
        viewModel.loadBrokerage(brokerageId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Branding",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Logo Upload
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Logo",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                uiState.logoUrl?.let { url ->
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = "Logo",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                } ?: Box(
                    modifier = Modifier
                        .size(200.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No logo uploaded")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = { viewModel.uploadLogo(brokerageId) }) {
                    Text("Upload Logo")
                }
            }
        }
        
        // Color Customization
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Brand Colors",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                ColorPicker(
                    label = "Primary Color",
                    color = Color(uiState.primaryColor),
                    onColorChange = { viewModel.updatePrimaryColor(brokerageId, it.value.toLong()) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ColorPicker(
                    label = "Secondary Color",
                    color = Color(uiState.secondaryColor),
                    onColorChange = { viewModel.updateSecondaryColor(brokerageId, it.value.toLong()) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ColorPicker(
                    label = "Accent Color",
                    color = Color(uiState.accentColor),
                    onColorChange = { viewModel.updateAccentColor(brokerageId, it.value.toLong()) }
                )
            }
        }
        
        // Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(uiState.primaryColor)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Brand Preview",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "This is how your branding will appear to agents",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ColorPicker(
    label: String,
    color: Color,
    onColorChange: (Color) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Box(
            modifier = Modifier
                .size(48.dp)
                .fillMaxWidth(0.3f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = color,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {}
        }
        // In a real implementation, clicking would open a color picker dialog
        TextButton(onClick = { /* Open color picker */ }) {
            Text("Change")
        }
    }
}

