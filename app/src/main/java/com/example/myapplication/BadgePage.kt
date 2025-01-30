package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BadgePage(badges: List<Badge>) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredBadges = badges.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(searchQuery) { searchQuery = it }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(filteredBadges) { badge ->
                BadgeItem(badge)
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            .padding(16.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        decorationBox = { innerTextField ->
            if (query.isEmpty()) {
                Text(text = "Search badges...", color = MaterialTheme.colorScheme.onSurface)
            }
            innerTextField()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BadgePagePreview() {
    val badges = (1..30).map { id ->
        Badge(
            id = id,
            name = "Checkpoint $id",
            description = "Description for checkpoint $id",
            isEarned = id % 2 == 0
        )
    }
    BadgePage(badges)
}
