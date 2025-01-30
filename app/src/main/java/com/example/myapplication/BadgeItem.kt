package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BadgeItem(badge: Badge, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }
            .background(if (badge.isEarned) Color.Green else Color.Transparent, CircleShape) // Highlight if earned
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(
                    id = getImageResourceForBadge(badge.id) // Use helper function for badge image
                ),
                contentDescription = badge.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = badge.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = if (isExpanded) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down),
                contentDescription = null,
                modifier = Modifier.size(24.dp) // Adjust size as needed
            )
        }
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = badge.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Helper function to get the drawable resource ID for each badge
fun getImageResourceForBadge(id: Int): Int {
    return when (id) {
        1 -> R.drawable.badge01
        2 -> R.drawable.badge02
        3 -> R.drawable.badge03
        4 -> R.drawable.badge04
        5 -> R.drawable.badge05
        6 -> R.drawable.badge06
        7 -> R.drawable.badge07
        8 -> R.drawable.badge08
        9 -> R.drawable.badge09
        10 -> R.drawable.badge10
        11 -> R.drawable.badge11
        12 -> R.drawable.badge12
        13 -> R.drawable.badge13
        14 -> R.drawable.badge14
        15 -> R.drawable.badge15
        16 -> R.drawable.badge16
        17 -> R.drawable.badge17
        18 -> R.drawable.badge18
        19 -> R.drawable.badge19
        20 -> R.drawable.badge20
        21 -> R.drawable.badge21
        22 -> R.drawable.badge22
        23 -> R.drawable.badge23
        24 -> R.drawable.badge24
        25 -> R.drawable.badge25
        26 -> R.drawable.badge26
        27 -> R.drawable.badge27
        28 -> R.drawable.badge28
        29 -> R.drawable.badge29
        30 -> R.drawable.badge30
        else -> R.drawable.default_badge // Default badge in case id is out of range
    }
}
