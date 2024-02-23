package de.hive.gamefinder.core.utils

const val IGDB_IMAGE_ENDPOINT = "https://images.igdb.com/igdb/image/upload/"
const val IMAGE_SIZE_COVER_BIG_2X = "t_cover_big_2x/"
const val IMAGE_SIZE_COVER_SMALL = "t_cover_small/"

fun getImageEndpoint(coverImageId: String, imageSize: ImageSize): String {
    val size = when (imageSize) {
        ImageSize.COVER_BIG -> IMAGE_SIZE_COVER_BIG_2X
        ImageSize.COVER_SMALL -> IMAGE_SIZE_COVER_SMALL
    }
    val imageEndpoint = "$IGDB_IMAGE_ENDPOINT$size$coverImageId.jpg"
    return imageEndpoint
}

enum class ImageSize {
    COVER_BIG,
    COVER_SMALL
}