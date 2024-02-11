package ui.uikit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import api.models.Review
import api.models.TeacherDto
import cafe.adriel.voyager.navigator.tab.Tab
import com.apu.unsession.MR
import dev.icerock.moko.resources.format
import utils.Email
import utils.OptScreen
import utils.clipboard
import utils.sendEmail
import utils.setText
import utils.toDateText
import kotlin.math.floor

public interface OptTab : OptScreen, Tab

@Composable
fun ClickableCard(
    elevation: CardElevation = CardDefaults.cardElevation(4.dp, 8.dp, 4.dp, 8.dp),
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
        Box(Modifier.fillMaxSize().clickable { onClick() }) {
            Column(Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ThinClickableCard(
    modifier: Modifier = Modifier,
    elevation: CardElevation = CardDefaults.cardElevation(4.dp, 8.dp, 4.dp, 8.dp),
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(Modifier.padding(2.dp).fillMaxWidth()) {
        Box(Modifier.fillMaxSize().clickable { onClick() }) {
            Column(Modifier.padding(2.dp)) {
                content()
            }
        }
    }
}

@Composable
fun TeacherCard(teacher: TeacherDto, onClick: () -> Unit = {}) {
    ClickableCard(onClick = onClick, content = {
        Text("${teacher.name} (${teacher.department})")
        Spacer(Modifier.size(8.dp))
        Row {
            if (teacher.rating != null && teacher.rating.toInt() != -1) {
                val stars = teacher.rating
                Stars(stars)
            } else {
                MiniTitle("(Нет рейтинга)") // fixme: string resources
            }
        }
    })
}

@Composable
fun ReviewCard(review: Review, onClick: () -> Unit = {}) {
    val ctx = LocalContext.current

    @Composable
    fun Review(name: String, value: Int) {
        Column {
            Text(name, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))
            Stars(value.toDouble())
        }
    }
    ClickableCard(onClick = onClick, content = {
        Text("${MR.strings.reviewed_by.getString(ctx)}: ${review.user!!.name} (${review.user.id})")
        review.createdTimestamp?.toLong()?.toDateText()?.let { Text(it) }
        review.comment?.let { Spacer(Modifier.size(8.dp)); Text(it) }
        Spacer(Modifier.size(16.dp))
        Review(MR.strings.global_rating.getString(ctx), review.globalRating)
        Spacer(Modifier.size(8.dp))
        review.difficultyRating?.let {
            Review(
                "${MR.strings.difficult_rating.getString(ctx)}: ", it
            )
        }
        review.boredomRating?.let {
            Review(
                "${MR.strings.boredom_rating.getString(ctx)}: ", it
            )
        }
        review.toxicityRating?.let {
            Review(
                "${MR.strings.toxicity_rating.getString(ctx)}: ", it
            )
        }
        review.educationalValueRating?.let {
            Review(
                "${
                    MR.strings.educational_value_rating.getString(
                        ctx
                    )
                }: ", it
            )
        }
        review.personalQualitiesRating?.let {
            Review(
                "${MR.strings.personal_rating.getString(ctx)}: ", it
            )
        }
    })
}

@Composable
fun Stars(rating: Double) {
    val colorSelected = MaterialTheme.colorScheme.primary
    val colorUnselected = MaterialTheme.colorScheme.onPrimary
    Row(verticalAlignment = Alignment.CenterVertically) {
        @Composable
        fun Star(enabled: Boolean) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "star",
                tint = if (enabled) colorSelected else colorUnselected,
                modifier = Modifier.size(24.dp)
            )
        }

        @Composable
        fun HalfStar(enabled: Boolean) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = "star",
                tint = if (enabled) colorSelected else colorUnselected,
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
        modifier = modifier, text = title, style = MaterialTheme.typography.labelSmall
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    onSearchTextChanged: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val searchText = remember { mutableStateOf("") }
    val fm = LocalFocusManager.current
    SearchBar(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(bottom = 8.dp),
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
        }) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    title: String, onSearchTextChanged: (String) -> Unit = {}, onSearch: (String) -> Unit = {}
) {
    TopAppBar(title = {
        Text(text = title)
    }, actions = {
        SearchAppBar(onSearchTextChanged, onSearch)
    })
}

@Composable
fun RatingStars(title: String, onStarsValueChanged: (Int) -> Unit = {}) {
    val colorSelected = MaterialTheme.colorScheme.primary
    val colorUnselected = MaterialTheme.colorScheme.onPrimary
    Text(title, style = MaterialTheme.typography.bodyLarge)
    Row {
        val stars = remember { mutableIntStateOf(3) }
        repeat(5) {
            IconButton(onClick = {
                stars.intValue = it + 1
                onStarsValueChanged(stars.intValue)
            }, content = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "star",
                    tint = if (it < stars.intValue) colorSelected else
                        colorUnselected,
                    modifier = Modifier.size(32.dp)
                )
            })
        }
    }
    Spacer(Modifier.size(16.dp))
}

@Composable
fun TeacherInfo(icon: ImageVector, iconTint: Color, contentText: String, onClick: () -> Unit) {
    ThinClickableCard(
        Modifier.padding(vertical = 4.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp, 8.dp, 4.dp, 8.dp),
        onClick = onClick,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    tint = iconTint,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(contentText, style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
fun TeacherInfoList(teacher: TeacherDto) {
    val clipboard = clipboard()
    Column {
        TeacherInfo(
            Icons.Default.Fingerprint,
            iconTint = MaterialTheme.colorScheme.onBackground,
            teacher.id.toString()
        ) {
            clipboard.setText(teacher.id.toString())
        }
        TeacherInfo(
            Icons.Default.Badge,
            iconTint = MaterialTheme.colorScheme.onBackground,
            teacher.name
        ) {
            clipboard.setText(teacher.name)
        }
        TeacherInfo(
            Icons.Default.Apartment,
            iconTint = MaterialTheme.colorScheme.onBackground,
            teacher.department
        ) {
            clipboard.setText(teacher.department) // feature: link to department website
        }
        val ctx = LocalContext.current
        teacher.email?.let { email ->
            TeacherInfo(
                Icons.Default.Email,
                iconTint = MaterialTheme.colorScheme.onBackground,
                email
            ) {
                sendEmail(ctx, Email(
                    to = email,
                    subject = "",
                    body = MR.strings.teacher_email_greetings.format(teacher.name).toString(ctx)
                ))
            }
        }
    }
}

@Composable
fun Forbidden(error: String = MR.strings.default_not_enough_permissions.getString(LocalContext.current)) {
    Box(Modifier.size(128.dp).padding(16.dp), contentAlignment = Alignment.Center) {
        Icon(imageVector = Icons.Default.Block, contentDescription = "forbidden")
        Text(error, modifier = Modifier.padding(8.dp))
    }
}
