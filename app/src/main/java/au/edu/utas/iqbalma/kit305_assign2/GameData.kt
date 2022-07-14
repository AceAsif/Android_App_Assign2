package au.edu.utas.iqbalma.kit305_assign2

class GameData(
    var id: String? = null,

    var gameStatus: String? = null,
    var repetitionNum: String? = null,

    var startTime: String? = null,
    var endTime: String? = null,
    var durationOfGame: Long = 0,

    var totalButtonClick: Int = 0,
    var buttonList: MutableList<String>? = null,
    var userPicture: String? = null

)
{

}