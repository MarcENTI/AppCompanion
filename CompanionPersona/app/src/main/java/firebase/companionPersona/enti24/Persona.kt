package firebase.companionPersona.enti24

data class Persona(
    val id: Int,
    val name: String,
    val arcana: String,
    val level: Int,
    val description: String,
    val image: String,
    val strength: Int,
    val magic: Int,
    val endurance: Int,
    val agility: Int,
    val luck: Int,
    val weak: List<String>,
    val resists: List<String>,
    val reflects: List<String>,
    val absorbs: List<String>,
    val nullifies: List<String>,
    val dlc: Int,
    val query: String
)
