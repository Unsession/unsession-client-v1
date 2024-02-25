package ui.screen.tabs.teacherstab

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import api.models.ReviewDto
import api.models.TeacherDto
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import dev.icerock.moko.resources.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.theme.appBarElevation
import ui.uikit.RatingStars
import ui.uikit.TeacherInfoList
import utils.AppBarScreen
import utils.OptScreen
import utils.ScreenOptions

class AddReviewScreen(val teacher: TeacherDto) : AppBarScreen(), OptScreen {
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = MR.strings.review_on.format(teacher.name).toString(LocalContext.current)
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val AppBar: @Composable () -> Unit
        @Composable get() = {
            val nav = LocalNavigator.current!!
            TopAppBar(
                title = {
                    Text(screenOptions.title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.pop()
                    }, content = {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    })
                }
            )
        }

    @Composable
    override fun Content() {
        val nav = LocalNavigator.current!!
        Scaffold(
            topBar = {
                Surface(shadowElevation = appBarElevation) {
                    AppBar()
                }
            },
        ) { paddingValues ->
            var textState by remember { mutableStateOf(TextFieldValue("")) }

            var globalRating by remember { mutableIntStateOf(0) }
            var difficultyRating by remember { mutableIntStateOf(0) }
            var boredomRating by remember { mutableIntStateOf(0) }
            var toxicityRating by remember { mutableIntStateOf(0) }
            var educationalValueRating by remember { mutableIntStateOf(0) }
            var personalQualitiesRating by remember { mutableIntStateOf(0) }

            var sendButtonEnabled by remember { mutableStateOf(true) }

            val errorAlert = remember { mutableStateOf(false) }
            val error = remember { mutableStateOf("") }

            if (errorAlert.value) {
                AlertDialog(
                    onDismissRequest = { errorAlert.value = false },
                    title = {
                        Text("Oops...")
                    },
                    text = {
                        Text(
                            MR.strings.review_error.format(error.value)
                                .toString(LocalContext.current)
                        )
                    },
                    confirmButton = {
                        Button(onClick = { errorAlert.value = false }) {
                            Text(MR.strings.ok.getString(LocalContext.current))
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                )
            }

            LazyColumn(Modifier.padding(paddingValues).padding(horizontal = 8.dp)) {
                item {

                    TeacherInfoList(teacher)

                    OutlinedTextField(
                        value = textState,
                        onValueChange = { text ->
                            textState = text
                        },
                        label = { Text(text = MR.strings.comment.getString(LocalContext.current)) },
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth().requiredHeight(180.dp)
                    )
                    Spacer(Modifier.size(16.dp))
                    RatingStars(
                        title = MR.strings.global_rating.getString(LocalContext.current),
                        onStarsValueChanged = { stars ->
                            globalRating = stars
                        }
                    )
                    RatingStars(
                        title = MR.strings.difficult_rating.getString(LocalContext.current),
                        onStarsValueChanged = { stars ->
                            difficultyRating = stars
                        }
                    )
                    RatingStars(
                        title = MR.strings.boredom_rating.getString(LocalContext.current),
                        onStarsValueChanged = { stars ->
                            boredomRating = stars
                        }
                    )
                    RatingStars(
                        title = MR.strings.toxicity_rating.getString(LocalContext.current),
                        onStarsValueChanged = { stars ->
                            toxicityRating = stars
                        }
                    )
                    RatingStars(
                        title = MR.strings.educational_value_rating.getString(LocalContext.current),
                        onStarsValueChanged = { stars ->
                            educationalValueRating = stars
                        }
                    )
                    RatingStars(
                        title = MR.strings.personal_rating.getString(LocalContext.current),
                        onStarsValueChanged = { stars ->
                            personalQualitiesRating = stars
                        }
                    )
                    Spacer(Modifier.size(16.dp))
                    Button(
                        onClick = {
                            sendButtonEnabled = false
                            CoroutineScope(Dispatchers.IO).launch {
                                api.ApiClient.Reviews.create(
                                    review = ReviewDto(
                                        teacherId = teacher.id,
                                        globalRating = globalRating,
                                        difficultyRating = difficultyRating,
                                        boredomRating = boredomRating,
                                        toxicityRating = toxicityRating,
                                        educationalValueRating = educationalValueRating,
                                        personalQualitiesRating = personalQualitiesRating,
                                        createdTimestamp = -1,
                                        userId = -1,
                                        comment = textState.text
                                    ),
                                    onSuccess = {
                                        nav.pop()
                                    },
                                    onFailure = {
                                        sendButtonEnabled = true
                                        error.value = it
                                        errorAlert.value = true
                                    }
                                )
                            }
                        },
                        modifier = Modifier.height(64.dp).fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Text(MR.strings.send_review.getString(LocalContext.current))
                    }
                    Spacer(Modifier.size(16.dp))
                }
            }
        }
    }
}
