package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import api.models.Review
import api.models.Teacher
import cafe.adriel.voyager.navigator.tab.Tab
import com.apu.unsession.MR
import utils.OptScreen
import utils.toDateText
import kotlin.math.floor

public interface OptTab : OptScreen, Tab

@Composable
fun ClickableCard(
    modifier: Modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(modifier = modifier) {
        Box(Modifier.fillMaxSize().clickable { onClick() }) {
            Column(Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun TeacherCard(teacher: Teacher, onClick: () -> Unit = {}) {
    ClickableCard(
        onClick = onClick,
        content = {
            Text("Преподаватель: ${teacher.name} (${teacher.department})")
            Spacer(Modifier.size(8.dp))
            Row {
                if (teacher.rating != null && teacher.rating.toInt() != -1) {
                    val stars = teacher.rating
                    Stars(stars)
                } else {
                    MiniTitle("(Нет рейтинга)")
                }
            }
        }
    )
}

@Composable
fun ClickableReviewCard(review: Review, onClick: () -> Unit = {}) {
    @Composable
    fun Review(name: String, value: Int) {
        Column {
            Text(name, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))
            Stars(value.toDouble())
        }
    }
    ClickableCard(
        onClick = onClick,
        content = {
            Text("Отзыв от: ${review.user.name} (${review.user.id})")
            Text("Опубликовано: ${review.createdTimestamp.toDateText()}")
            review.comment?.let {
                Spacer(Modifier.size(8.dp))
                Text(it)
            }
            Spacer(Modifier.size(8.dp))

            Review("Общая оценка: ", review.globalRating)
            review.difficultyRating?.let { Review("Сложность: ", it) }
            review.boredomRating?.let { Review("Душность: ", it) }
            review.toxicityRating?.let { Review("Токсичность: ", it) }
            review.educationalValueRating?.let { Review("Полезность: ", it) }
            review.personalQualitiesRating?.let { Review("Личные качества: ", it) }
        }
    )
}

@Composable
fun ReviewCard(review: Review, onClick: () -> Unit = {}) {
    @Composable
    fun Review(name: String, value: Int) {
        Column {
            Text(name, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))
            Stars(value.toDouble())
        }
    }
    ClickableCard(
        onClick = onClick,
        content = {
            Text("Отзыв от: ${review.user.name} (${review.user.id})")
            Text("Опубликовано: ${review.createdTimestamp.toDateText()}")
            review.comment?.let {
                Spacer(Modifier.size(8.dp))
                Text(it)
            }
            Spacer(Modifier.size(8.dp))

            Review("Общая оценка: ", review.globalRating)
            review.difficultyRating?.let { Review("Сложность: ", it) }
            review.boredomRating?.let { Review("Душность: ", it) }
            review.toxicityRating?.let { Review("Токсичность: ", it) }
            review.educationalValueRating?.let { Review("Полезность: ", it) }
            review.personalQualitiesRating?.let { Review("Личные качества: ", it) }
        }
    )
}

@Composable
fun Stars(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        @Composable
        fun Star(enabled: Boolean) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "star",
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFC0C0C0),
                modifier = Modifier.size(24.dp)
            )
        }

        @Composable
        fun HalfStar(enabled: Boolean) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = "star",
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFC0C0C0),
                modifier = Modifier.size(24.dp)
            )
        }
        repeat(5) {
            val intR = floor(rating).toInt()
            if (it < intR) {
                Star(enabled = true)
            } else if (it == intR && rating % 1 >= 0.5) {
                HalfStar(enabled = true)
            } else {
                Star(enabled = false)
            }
        }
    }
}

@Composable
fun MiniTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.labelSmall
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(onSearchTextChanged: (String) -> Unit = {}, onSearch: (String) -> Unit = {}, content: @Composable () -> Unit = {}) {
    val searchText = remember { mutableStateOf("") }
    val fm = LocalFocusManager.current
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        query = searchText.value,
        active = false,
        onQueryChange = {
            searchText.value = it
            onSearchTextChanged(it)
        },
        onSearch = {
            onSearch(searchText.value)
            fm.clearFocus()
        },
        onActiveChange = {},
        placeholder = {
            Text(text = MR.strings.search.getString(LocalContext.current))
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "search")
        }
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(title: String, onSearchTextChanged: (String) -> Unit = {}, onSearch: (String) -> Unit = {}) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        actions = {
            SearchAppBar(onSearchTextChanged, onSearch)
        }
    )
}
