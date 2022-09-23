fun List<String>.toJson() = this.joinToString("\",\"", "[\"", "\"]")

fun Array<String>.toJson() = this.joinToString("\",\"", "[\"", "\"]")
