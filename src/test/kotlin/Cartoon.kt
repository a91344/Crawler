data class Cartoon(
    var _md5: String = "",
    var title: String = "",
    var author: String = "",
    var cover: String = "",
    var tags: List<String> = ArrayList(),
    var page: Int = 0,
    var imgs: ArrayList<String> = ArrayList(),
    var timeOfReceipt: Long = 0
) {
    companion object {
        fun toSelectCartoon() = "select * from cartoon"
        fun toSelectCartoonImgs() = "select * from cartoon_imgs"
    }
    fun toInstallSqlToCartoonImage() = "replace into cartoon_imgs (_md5,title,imgs) values ('${_md5}','${title}','${imgs.toJson()}')"
    fun toInstallSqlToCartoon() = "replace into cartoon (_md5,title,cover,author,tags,pages,time_of_receipt)  values ('${_md5}','${title}','${cover}','${author}','${tags.joinToString(",")}','${page}','${timeOfReceipt}') "
}