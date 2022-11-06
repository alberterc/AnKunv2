package com.radx.ankunv2

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.apache.commons.lang3.time.DateUtils
import java.util.*

object Utils {
    const val animeBaseUrl = "https://animension.to/"

    fun getTimeFromEpoch(epochTime: String): Long {
        val currDate = DateUtils.round(Date(System.currentTimeMillis()), Calendar.MINUTE)
        val releasedDate = DateUtils.round(Date(epochTime.toLong() * 1000), Calendar.MINUTE)

        return currDate.time - releasedDate.time
    }

    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @Suppress("UNCHECKED_CAST")
    fun readFavoriteIds(context: Context): MutableList<String> {
        val animeIDs = mutableListOf<String>()
        val firebaseAuth = Firebase.auth
        val firebaseDatabase = Firebase.firestore

        // read all favorite anime ids
        firebaseDatabase.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    try {
                        val ids: List<String> = document.data!!.values.toMutableList()[0] as List<String>
                        // add all existing anime id
                        ids.forEach {
                            animeIDs.add(it)
                        }
                    } catch (_: IndexOutOfBoundsException) {}
                }
                else {
                    toast(context, "No Docs")
                }
            }
            .addOnFailureListener {
                toast(context, "Failed to retrieve data.")
            }

        return animeIDs
    }
}

sealed class AnimeDetailsScreenNav(var route: String) {
    object AnimeDetails: AnimeDetailsScreenNav("anime_details")
    object AnimeEpisodeStream: AnimeDetailsScreenNav("anime_episode_stream")
}

@Composable
private fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Float,
    spaceBetween: Dp = 0.dp
) {

    val image = ImageBitmap.imageResource(id = R.drawable.ic_baseline_star_outline_24)
    val imageFull = ImageBitmap.imageResource(id = R.drawable.ic_baseline_star_24)

    val totalCount = 5

    val height = LocalDensity.current.run { image.height.toDp() }
    val width = LocalDensity.current.run { image.width.toDp() }
    val space = LocalDensity.current.run { spaceBetween.toPx() }
    val totalWidth = width * totalCount + spaceBetween * (totalCount - 1)


    Box(
        modifier
            .width(totalWidth)
            .height(height)
            .drawBehind {
                drawRating(rating, image, imageFull, space)
            })
}

private fun DrawScope.drawRating(
    rating: Float,
    image: ImageBitmap,
    imageFull: ImageBitmap,
    space: Float
) {

    val totalCount = 5

    val imageWidth = image.width.toFloat()
    val imageHeight = size.height

    val reminder = rating - rating.toInt()
    val ratingInt = (rating - reminder).toInt()

    for (i in 0 until totalCount) {

        val start = imageWidth * i + space * i

        drawImage(
            image = image,
            topLeft = Offset(start, 0f)
        )
    }

    drawWithLayer {
        for (i in 0 until totalCount) {
            val start = imageWidth * i + space * i
            // Destination
            drawImage(
                image = imageFull,
                topLeft = Offset(start, 0f)
            )
        }

        val end = imageWidth * totalCount + space * (totalCount - 1)
        val start = rating * imageWidth + ratingInt * space
        val size = end - start

        // Source
        drawRect(
            Color.Transparent,
            topLeft = Offset(start, 0f),
            size = Size(size, height = imageHeight),
            blendMode = BlendMode.SrcIn
        )
    }
}

private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}