package ui.screen.tabs.inner.teacherstab

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import api.models.Review
import api.models.TeacherDto
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
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import dev.icerock.moko.resources.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import lol.unsession.db.models.PAGE_SIZE_DEFAULT
import ui.theme.appBarElevation
import ui.uikit.ReviewCard
import ui.uikit.TeacherInfoList
import utils.AppBarScreen
import utils.ScreenOptions

class FullReviewPagingSource(private val teacherId: Int) : PagingSource<Int, Review>() {
    override suspend fun load(params: LoadParams<Int>): PagingSourceLoadResult<Int, Review> {
        val page = params.key ?: 1
        val pageSize = params.loadSize
        var error = ""
        val response = api.Api.Reviews.getByTeacher(page, teacherId, pageSize, onFailure = {
            error = it
        })
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

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(20)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(20)
        }
    }
}

class FullReviewScreenViewModel(id: Int = -1) : ViewModel() {
    val pager = Pager(PagingConfig(pageSize = PAGE_SIZE_DEFAULT)) {
        FullReviewPagingSource(id)
    }.flow.cachedIn(CoroutineScope(Dispatchers.IO))
}

class FullReviewScreen(
    private val teacher: TeacherDto
) : AppBarScreen {
    private val vm = FullReviewScreenViewModel(teacher.id)
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = MR.strings.reviews_on.format(teacher.name).toString(LocalContext.current)
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
                    IconButton(onClick = { nav.pop() }, content = {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    })
                }
            )
        }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val nav = LocalNavigator.current!!
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        nav.push(AddReviewScreen(teacher))
                    },
                    text = { Text(MR.strings.review.getString(LocalContext.current)) },
                    icon = { Icon(imageVector = (Icons.Default.Add), "add review") }
                )
            },
            topBar = {
                Surface(shadowElevation = appBarElevation) {
                    AppBar()
                }
            }
        ) {
            Column(Modifier.padding(top = 50.dp).padding(horizontal = 8.dp)) {
                val lazyReviewItems = vm.pager.collectAsLazyPagingItems()
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(Modifier) {
                    item {
                        TeacherInfoList(teacher)
                    }
                    items(
                        lazyReviewItems.itemCount
                    ) { index ->
                        when (lazyReviewItems.loadState.refresh) {
                            is LoadStateLoading -> {
                                Spacer(Modifier.size(40.dp))
                            }

                            is LoadStateError -> {
                                Text("Error")
                            }

                            else -> {
                                val review = lazyReviewItems[index]
                                if (review != null) {
                                    ReviewCard(review)
                                } else {
                                    Text("Error review null [$index]")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
