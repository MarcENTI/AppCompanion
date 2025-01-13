package firebase.companionPersona.enti24

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SteamRetrofitInstance {
    private const val BASE_URL = "https://api.steampowered.com/"

    val api: SteamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamApiService::class.java)
    }
}
