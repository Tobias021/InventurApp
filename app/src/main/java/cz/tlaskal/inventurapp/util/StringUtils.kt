package cz.tlaskal.inventurapp.util

fun String.limitLength(maxLength: Int): String {
    return if(this.count() < maxLength)
    {
        this
    }else{
        this.substring(0, maxLength) + "..."
    }
}