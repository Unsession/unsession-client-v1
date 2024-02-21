package ui.screen.tabs.teacherstab

import android.os.Parcel
import androidx.compose.foundation.layout.Column
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
import api.Api
import api.models.Access
import api.models.TeacherDto
import api.models.User
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import dev.icerock.moko.parcelize.Parcelable
import ui.theme.appBarElevation
import ui.uikit.Forbidden
import ui.uikit.SearchAppBar
import ui.uikit.TeacherCard
import utils.OptScreen
import utils.ScreenOptions
import utils.SearchViewModel

/**
 * По идее, я ничего при сериализации никуда не кладу
 * но оно почему-то всё равно сохраняет состояние.
 * Магия.*/
class SearchTeachersScreen() : OptScreen,
    Parcelable {

    private val vm = SearchViewModel(
        request = { page, query, pageSize ->
            if (query.isEmpty())
                Api.Teachers.getTeachers(page, pageSize)
            else
                Api.Teachers.searchTeachers(page, query, pageSize)
        },
        validator = { it.length >= 3 }
    )
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
            Column {
                Surface(shadowElevation = appBarElevation, modifier = Modifier.padding(it)) {
                    SearchAppBar(onSearchTextChanged = { q ->
                        vm.searchQuery.value = q
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
                        if (User.hasAccess(Access.Teachers)) {
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
                        } else {
                            Forbidden()
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
