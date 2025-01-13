package firebase.companionPersona.enti24

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Ejemplo de interfaz para la Steam Web API.
  'GetNewsForApp' de Team Fortress 2 (appid=440)
 */
interface SteamApiService {

    @GET("ISteamNews/GetNewsForApp/v2/")
    fun getNewsForApp(
        @Query("appid") appid: Int,
        @Query("count") count: Int,
        @Query("maxlength") maxlength: Int,
        @Query("format") format: String,
        @Query("key") apiKey: String
    ): Call<SteamNewsResponse>
}
