package ui.screen.tabs.searchteachers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import api.Api
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import lol.unsession.db.models.Teacher
import ui.HomeAppBar
import ui.TeacherCard
import ui.screen.tabs.inner.FullReviewScreen
import utils.OptScreen
import utils.ScreenOptions

class TeachersSearchPagingSource(private val query: String) : PagingSource<Int, Teacher>() {
    override suspend fun load(params: LoadParams<Int>): PagingSourceLoadResult<Int, Teacher> {
        val page = params.key ?: 1
        val pageSize = params.loadSize
        var error = ""
        val response = if (query.length >= 3) {
            Api.Teachers.searchTeachers(page, query, pageSize, onFailure = {
                error = it
            })
        } else {
            Api.Teachers.getTeachers(page, pageSize, onFailure = {
                error = it
            })
        }
        if (error.isNotEmpty()) {
            println(error)
            return PagingSourceLoadResultError(IllegalArgumentException(error))
        }
        return PagingSourceLoadResultPage(
            data = response,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (response.isEmpty()) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Teacher>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(20)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(20)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchTeachersScreenViewModel : ViewModel() {
    var query = MutableStateFlow("")

    val pager =
        query.debounce(300).flatMapLatest { uQuery ->
            Pager(PagingConfig(pageSize = 15)) {
                TeachersSearchPagingSource(uQuery)
            }.flow.cachedIn(CoroutineScope(Dispatchers.IO))
        }

}

class SearchTeachersScreen(
    private
    val vm: SearchTeachersScreenViewModel = SearchTeachersScreenViewModel()
) : OptScreen {

    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = MR.strings.reviews.getString(LocalContext.current)
        )

    @Composable
    override fun Content() {
        val nav = LocalNavigator.current!!
        val lazyReviewItems: LazyPagingItems<Teacher> =
            vm.pager.collectAsLazyPagingItems()

        Column {
            HomeAppBar(
                title = screenOptions.title,
                onSearchTextChanged = {
                    vm.query.value = it
                }
            )
            Box(Modifier.padding(horizontal = 8.dp)) {
                when (lazyReviewItems.loadState.refresh) {
                    is LoadStateLoading -> {
                        Spacer(Modifier.size(40.dp))
                    }

                    is LoadStateError -> {
                        Text("Error")
                    }

                    else -> {
                        LazyColumn(Modifier) {
                            items(
                                lazyReviewItems.itemCount
                            ) { index ->
                                val teacher = lazyReviewItems[index]
                                TeacherCard(teacher!!) {
                                    nav.push(FullReviewScreen(teacher))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
