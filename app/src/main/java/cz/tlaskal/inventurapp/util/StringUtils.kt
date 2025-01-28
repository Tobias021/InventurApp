package cz.tlaskal.inventurapp.util

fun String.limitLength(maxLength: Int): String {
    return if (this.count() < maxLength) {
        this
    } else {
        this.substring(0, maxLength) + "..."
    }
}

fun String.endOnLineBreak(): String {
    val eolIndex = this.indexOf('\n')
    if (eolIndex != -1) {
        val firstLine = this.substring(0, eolIndex)
        if (
            this.substring(eolIndex, this.count())
                .isNotBlank()
        ) {
            return "$firstLine\n..."
        }
         return firstLine
    }
    return this
}