package firebase.companionPersona.enti24

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PersonaApi {

    // GET a lista de todas las personas
    @GET("personas/")
    fun getAllPersonas(): Call<List<Persona>>

    // GET a persona específica, con minúsculas y guiones
    @GET("personas/{personaName}/")
    fun getPersonaByName(@Path("personaName") personaName: String): Call<Persona>
}
