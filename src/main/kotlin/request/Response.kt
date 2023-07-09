package request

data class Response(
    var body: String = "",
    var code: Int = -1,
    var url: String = "",
    var bytes: ByteArray = ByteArray(0)
) {

}
