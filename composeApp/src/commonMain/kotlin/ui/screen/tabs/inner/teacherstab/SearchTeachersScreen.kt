package ui.screen.tabs.inner.teacherstab

import android.os.Parcel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
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
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import dev.icerock.moko.parcelize.Parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import ui.theme.appBarElevation
import ui.uikit.SearchAppBar
import ui.uikit.TeacherCard
import utils.OptScreen
import utils.ScreenOptions

class TeachersSearchPagingSource(private val query: String) : PagingSource<Int, TeacherDto>() {
    override suspend fun load(params: LoadParams<Int>): PagingSourceLoadResult<Int, TeacherDto> {
        val page = params.key ?: 1
        val pageSize = params.loadSize
        var error = ""
        val response = if (query.length >= 3) {
            api.Api.Teachers.searchTeachers(page, query, pageSize, onFailure = {
                error = it
            })
        } else {
            api.Api.Teachers.getTeachers(page, pageSize, onFailure = {
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

    override fun getRefreshKey(state: PagingState<Int, TeacherDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(20)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(20)
        }
    }
}


class SearchTeachersScreenViewModel() : ViewModel() {
    var query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pager = query.debounce(300).flatMapLatest { uQuery ->
            Pager(PagingConfig(pageSize = 15)) {
                TeachersSearchPagingSource(uQuery)
            }.flow.cachedIn(CoroutineScope(Dispatchers.IO))

        }

}

/**
 * По идее, я ничего при сериализации никуда не кладу
 * но оно почему-то всё равно сохраняет состояние.
 * Магия.*/
class SearchTeachersScreen() : OptScreen,
    Parcelable { // WARN: IOS, DESKTOP check it is not platform-specific api

    private val vm = SearchTeachersScreenViewModel()

    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = MR.strings.reviews.getString(LocalContext.current)
        )

    constructor(parcel: Parcel) : this() {
    }

    @Composable
    override fun Content() {
        val nav = LocalNavigator.current!!
        val lazyReviewItems: LazyPagingItems<TeacherDto> = vm.pager.collectAsLazyPagingItems()

        Scaffold(
            floatingActionButton = {

            }
        ) {
            Surface(shadowElevation = appBarElevation, modifier = Modifier.padding(it)) {
                SearchAppBar(onSearchTextChanged = { q ->
                    vm.query.value = q
                }, content = {
                    Spacer(Modifier.height(8.dp))
                })
            }
            when (lazyReviewItems.loadState.refresh) {
                is LoadStateLoading -> {
                    Spacer(Modifier.size(40.dp))
                }

                is LoadStateError -> {
                    Text("Error")
                }

                else -> {
                    LazyColumn(Modifier.padding(horizontal = 8.dp)) {
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

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {

    }

    companion object CREATOR : android.os.Parcelable.Creator<SearchTeachersScreen> {
        override fun createFromParcel(parcel: Parcel): SearchTeachersScreen {
            return SearchTeachersScreen(parcel)
        }

        override fun newArray(size: Int): Array<SearchTeachersScreen?> {
            return arrayOfNulls(size)
        }
    }
}
