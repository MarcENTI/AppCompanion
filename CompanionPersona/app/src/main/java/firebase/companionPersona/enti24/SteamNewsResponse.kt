package firebase.companionPersona.enti24

/**
 * https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/
 */
data class SteamNewsResponse(
    val appnews: AppNews?
)

data class AppNews(
    val appid: Int?,
    val newsitems: List<NewsItem>?
)

data class NewsItem(
    val gid: String?,
    val title: String?,
    val url: String?,
    val is_external_url: Boolean?,
    val author: String?,
    val contents: String?,
    val feedlabel: String?,
    val date: Long?,
    val feedname: String?,
    val feed_type: Int?,
    val appid: Int?
)
