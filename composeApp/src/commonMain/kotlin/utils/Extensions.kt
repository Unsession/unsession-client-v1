package utils

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import lol.unsession.db.models.PAGE_SIZE_DEFAULT
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toDateText(): String {
    val dateTime =
        LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return dateTime.format(formatter)
}

public data class ScreenOptions(
    val title: String,
)

interface OptScreen : Screen {
    val screenOptions: ScreenOptions @Composable get
}

interface AppBarScreen : Screen, OptScreen {
    val AppBar: @Composable () -> Unit
        @Composable get
}

@Composable
fun clipboard(): ClipboardManager {
    return LocalClipboardManager.current
}

fun ClipboardManager.setText(text: String) {
    setText(AnnotatedString(text))
}

data class Email(
    val to: String,
    val subject: String,
    val body: String,
)

fun sendEmail(ctx: Context, email: Email) {
    val i = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email.to))
        putExtra(Intent.EXTRA_SUBJECT, email.subject)
        putExtra(Intent.EXTRA_TEXT, email.body)
        type = "message/rfc822"
    }
    ctx.startActivity(Intent.createChooser(i, "Choose an Email client : "))
}

open class SearchPagingSource<T : Any>(
    private val query: String,
    private val validator: (String) -> Boolean,
    private val request: suspend (page: Int, query: String, pageSize: Int) -> Result<List<T>>,
    private val defaultPage: Int = 1,
    private val useDefaultQuery: Boolean = true,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: defaultPage
        val pageSize = params.loadSize
        val response = Result.success(emptyList<T>())
        if (validator(query)) {
            request(page, query, pageSize)
        } else if (useDefaultQuery) {
            request(page, "", pageSize)
        }
        if (response.isFailure) {
            return PagingSourceLoadResultError(response.exceptionOrNull()!!)
        }
        return PagingSourceLoadResultPage(
            data = response.getOrElse { emptyList() },
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (response.getOrElse { emptyList() }.isEmpty()) null else page + 1
        )
    }

}

class SearchViewModel<T : Any>(
    request: suspend (page: Int, query: String, pageSize: Int) -> Result<List<T>>,
    debounceTime: Long = 300,
    pageSize: Int = PAGE_SIZE_DEFAULT,
    validator: (String) -> Boolean,
) {
    var searchQuery = MutableStateFlow("")

    val source: SearchPagingSource<T> = SearchPagingSource(
        query = searchQuery.value,
        validator = validator,
        request = request
    )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pager = searchQuery.debounce(debounceTime).flatMapLatest { newQuery ->
        Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = { source }
        ).flow.cachedIn(CoroutineScope(Dispatchers.IO))
    }
}
