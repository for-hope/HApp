import android.graphics.Bitmap
import android.net.Uri

data class HVideo(
    val uri: Uri,
    val name: String,
    val duration: Long,
    val dateAdded: Long,
    var thumbnail: Bitmap
)