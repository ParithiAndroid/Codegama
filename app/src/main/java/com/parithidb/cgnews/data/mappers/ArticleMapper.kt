import android.os.Build
import androidx.annotation.RequiresApi
import com.parithidb.cgnews.data.api.model.ArticleDto
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import java.time.Instant


@RequiresApi(Build.VERSION_CODES.O)
fun ArticleDto.toEntity(): ArticleEntity? {
    val safeUrl = url ?: return null
    val published = publishedAt?.let {
        runCatching { Instant.parse(it).toEpochMilli() }.getOrDefault(System.currentTimeMillis())
    } ?: System.currentTimeMillis()

    return ArticleEntity(
        url = safeUrl,
        title = title,
        description = description,
        imageUrl = urlToImage,
        sourceName = source?.name,
        publishedAt = published,
        content = content
    )
}
