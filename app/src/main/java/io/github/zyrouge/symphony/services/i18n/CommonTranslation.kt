package io.github.zyrouge.symphony.services.i18n

@Suppress("Unused", "ConstPropertyName", "FunctionName")
object CommonTranslation {
    const val SomethingWentHorriblyWrong = "Something went horribly wrong!"
    const val System = "System"

    fun ErrorX(x: String) = "Error: $x"
    fun SomethingWentHorriblyWrongErrorX(x: String) =
        "$SomethingWentHorriblyWrong (${ErrorX(x)})"
}
