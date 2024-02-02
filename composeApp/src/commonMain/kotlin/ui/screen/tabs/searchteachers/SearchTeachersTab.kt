package ui.screen.tabs.searchteachers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import api.Api.Teachers.searchTeachers
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
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import lol.unsession.db.models.Teacher
import ui.ClickableCard
import ui.FabTab
import ui.screen.tabs.inner.FullReviewScreen

class TeachersSearchPagingSource(val query: String) : PagingSource<Int, Teacher>() {
    override suspend fun load(params: LoadParams<Int>): PagingSourceLoadResult<Int, Teacher> {
        val page = params.key ?: 1
        val pageSize = params.loadSize
        var error = ""
        val response = searchTeachers(page, query, pageSize, onFailure = {
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

    override fun getRefreshKey(state: PagingState<Int, Teacher>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(20)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(20)
        }
    }
}

class SearchTeachersTabViewModel : ViewModel() {
    var query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pager = query.debounce(200).flatMapLatest { uQuery ->
        if (uQuery.length >= 3) {
            Pager(
                PagingConfig(pageSize = 15)
            ) {
                TeachersSearchPagingSource(uQuery)
            }.flow.cachedIn(CoroutineScope(IO))
        } else {
            flowOf()
        }
    }
}

class SearchTeachersTab(
    val vm: SearchTeachersTabViewModel = SearchTeachersTabViewModel()
) : FabTab {

    @Composable
    override fun Fab() {
        FloatingActionButton(onClick = {
            println("Hello")
        }) {
            Text("Hello")
        }
    }

    override val options: TabOptions
        @Composable get() = TabOptions(
            index = 0U,
            title = "Reviews",
            icon = rememberVectorPainter(Icons.Default.Reviews),
        )

    @Composable
    override fun Content() {
        val nestedNav =
        val lazyReviewItems: LazyPagingItems<Teacher> = vm.pager.collectAsLazyPagingItems()
        val query by vm.query.collectAsState()

        Column {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                value = query,
                onValueChange = { newValue ->
                    vm.query.value = newValue
                },
                label = { Text(text = "Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                keyboardActions = KeyboardActions(onSearch = {
                    vm.query.value = query
                })
            )

            when (lazyReviewItems.loadState.refresh) {
                is LoadStateLoading -> {
                    Spacer(Modifier.size(40.dp))
                }

                is LoadStateError -> {
                    Text("Error")
                }

                else -> {
                    LazyColumn(Modifier) {
//                        item {
//                            ClickableCard(
//                                modifier = Modifier.fillMaxWidth().height(120.dp),
//                                onClick = {
//
//                                },
//                                content = {
//                                    Text(
//                                        "Ratings",
//                                        textAlign = TextAlign.Center,
//                                        modifier = Modifier.fillMaxWidth(),
//                                        fontWeight = FontWeight.Bold,
//                                    )
//                                }
//                            )
//                        }
                        items(
                            lazyReviewItems.itemCount
                        ) { index ->
                            val teacher = lazyReviewItems[index]
                            TeacherCard(teacher!!) {
                                // navigate to FullReviewScreen
                                localNav.push(FullReviewScreen(teacher))
                            }
                        }
                    }
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
                teacher.rating?.let {
                    println("RATING: $it")
                    val stars = teacher.rating.toFloat()
                    Stars(stars)
                }
            }
        )
    }

    @Composable
    fun Stars(rating: Float) {
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
            repeat(5) {
                if (rating >= it + 1) {
                    Star(true)
                } else {
                    Star(false)
                }
            }
            Spacer(Modifier.width(4.dp))
            Text(text = "(${if (rating > 0) rating else "Нет рейтинга"})", style = MaterialTheme.typography.labelSmall)
        }
    }
}
