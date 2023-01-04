enum class DaysOfTheWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}

fun main() {
    for (i in DaysOfTheWeek.values()) {
        println(i.name)
    }
}
