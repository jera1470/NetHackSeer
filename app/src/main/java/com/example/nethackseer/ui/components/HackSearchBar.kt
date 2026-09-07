package com.example.nethackseer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nethackseer.ui.theme.Black
import com.example.nethackseer.ui.theme.Typography
import com.example.nethackseer.ui.theme.White
import com.example.nethackseer.ui.utils.cleanNetHackName
import com.example.nethackseer.ui.utils.getDisplayChar
import com.example.nethackseer.ui.utils.getNetHackColor

data class SearchResultItem(
    val name: String,
    val category: String, // "monster", "item", or "property"
    val symbol: String,
    val color: String
)

/**
 * Reusable NetHack search bar for multiple entities.
 *
 * @param query Text query inputted to search field
 * @param onQueryChange Callback invoked when the search query text changes
 * @param onSearch Callback invoked when the user submits a search action
 * @param expanded Controls whether search bar is collapsed or not
 * @param onExpandedChange Callback invoked when expanded state changes
 * @param placeholderText Placeholder text shown when query is empty.
 * @param modifier Normal modifier for all composables
 * @param content Slot for rendering live search results inside the expanded overlay
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HackSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    placeholderText: String = "Search",
    content: @Composable ColumnScope.() -> Unit = {}
) {
    SearchBar(
        modifier = modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                placeholder = { Text(text = placeholderText) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        content = content
    )
}

/**
 * Reusable component for displaying an individual search result item within a search list or dropdown.
 *
 * @param item The SearchResultItem to render
 * @param onClick Action to do when clicked
 * @param modifier Normal modifier for all composables
 */
@Composable
fun SearchResultRow(
    item: SearchResultItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = cleanNetHackName(item.name),
                    textAlign = TextAlign.Start,
                    style = Typography.bodyLarge
                )
                Text(
                    text = item.category.replaceFirstChar { it.uppercase() },
                    style = Typography.bodySmall,
                    color = White.copy(alpha = 0.7f)
                )
            }
            Text(
                text = getDisplayChar(item.symbol),
                color = getNetHackColor(item.color),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .background(Black)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
