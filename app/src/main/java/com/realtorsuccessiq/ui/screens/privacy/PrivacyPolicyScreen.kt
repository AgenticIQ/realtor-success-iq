package com.realtorsuccessiq.ui.screens.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyPolicyScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Privacy Policy (Draft)", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Text(
                "We are privacy-first. This app is designed to help agents execute daily actions. " +
                    "We do not sell personal contact data.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            Text("What we may collect (opt-out):", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text("• Feature usage counts (e.g., screens visited, buttons tapped)")
                Text("• Aggregated activity metrics (e.g., calls logged/day, streaks)")
                Text("• App version + device OS version")
            }
        }
        item {
            Text("What we do NOT collect:", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text("• Your clients’ names, emails, phone numbers for marketing")
                Text("• Message content or call recordings")
                Text("• Precise location data")
            }
        }
        item {
            Text(
                "You can disable analytics in Settings at any time.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


