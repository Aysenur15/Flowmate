package com.flowmate.ui.component

val dailyQuotes = listOf(
    "Small daily improvements are the key to staggering long-term results." to "James Clear",
    "Your future is created by what you do today, not tomorrow." to "Robert Kiyosaki",
    "Discipline is choosing between what you want now and what you want most." to "Abraham Lincoln",
    "Success doesn’t come from what you do occasionally, it comes from what you do consistently." to "Marie Forleo",
    "The secret of your future is hidden in your daily routine." to "Mike Murdock",
    "Every action you take is a vote for the type of person you wish to become." to "James Clear",
    "Progress, not perfection." to "Unknown",
    "You don’t need more time, you just need to decide." to "Seth Godin",
    "Motivation gets you going, but discipline keeps you growing." to "John C. Maxwell",
    "Your habits will determine your future." to "Jack Canfield"
)

fun getTodayQuote(): Pair<String, String> {
    val dayOfYear = java.time.LocalDate.now().dayOfYear
    return dailyQuotes[dayOfYear % dailyQuotes.size]
}

