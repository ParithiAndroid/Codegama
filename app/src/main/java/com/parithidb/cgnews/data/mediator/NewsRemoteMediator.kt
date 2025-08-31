import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.parithidb.cgnews.data.api.NewsApiService
import com.parithidb.cgnews.data.database.AppDatabase
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import com.parithidb.cgnews.data.database.entities.RemoteKeysEntity

@OptIn(ExperimentalPagingApi::class)
class ArticlesRemoteMediator(
    private val db: AppDatabase,
    private val service: NewsApiService,
    private val country: String,
    private val pageSize: Int
) : RemoteMediator<Int, ArticleEntity>() {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> {
                    val first = state.firstItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    val key = db.remoteKeysDao().remoteKeysUrl(first.url)?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    key
                }

                LoadType.APPEND -> {
                    val last = state.lastItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    val key = db.remoteKeysDao().remoteKeysUrl(last.url)?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    key
                }
            }

            val apiPageSize = state.config.pageSize.takeIf { it > 0 } ?: pageSize
            val resp = service.topHeadlines(country = country, page = page, pageSize = apiPageSize)

            val articles = resp.articles.mapNotNull { it.toEntity() }
            val endReached = articles.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().clearRemoteKeys()
                    db.articleDao().clearAll()
                }

                val keys = articles.mapIndexed { index, entity ->
                    val next = if (endReached) null else page + 1
                    val prev = if (page == 1) null else page - 1
                    RemoteKeysEntity(articleUrl = entity.url, prevKey = prev, nextKey = next)
                }
                db.remoteKeysDao().insertAll(keys)
                db.articleDao().insertAll(articles)
            }

            return MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}