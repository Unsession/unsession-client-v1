package utils

import androidx.paging.PagingSource
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import lol.unsession.db.models.DEFAULT_PAGE_SIZE

open class SearchPagingSource<T : Any>(
    private val query: String,
    private val validator: (String) -> Boolean,
    private val request: suspend (page: Int, query: String, pageSize: Int) -> Result<List<T>>,
    private val defaultPage: Int = 1,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(DEFAULT_PAGE_SIZE)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(DEFAULT_PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: defaultPage
        val pageSize = params.loadSize
        val response = if (validator(query)) {
            request(page, query, pageSize)
        } else {
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

open class SearchViewModel<T : Any>(
    request: suspend (page: Int, query: String, pageSize: Int) -> Result<List<T>>,
    debounceTime: Long = 300,
    pageSize: Int = DEFAULT_PAGE_SIZE,
    validator: (String) -> Boolean = { true },
) : ViewModel() {
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