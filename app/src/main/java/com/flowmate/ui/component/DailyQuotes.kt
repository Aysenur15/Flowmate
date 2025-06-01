package com.flowmate.ui.component

import java.time.LocalDate
import java.time.temporal.ChronoUnit
// Quotes for daily motivation
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
    "Your habits will determine your future." to "Jack Canfield",
    "We are what we repeatedly do. Excellence, then, is not an act, but a habit." to "Aristotle",
    "Don’t watch the clock; do what it does. Keep going." to "Sam Levenson",
    "The best way to get something done is to begin." to "Unknown",
    "It does not matter how slowly you go as long as you do not stop." to "Confucius",
    "You miss 100% of the shots you don’t take." to "Wayne Gretzky",
    "Success is the sum of small efforts, repeated day in and day out." to "Robert Collier",
    "Action is the foundational key to all success." to "Pablo Picasso",
    "Start where you are. Use what you have. Do what you can." to "Arthur Ashe",
    "Believe you can and you're halfway there." to "Theodore Roosevelt",
    "Hard choices, easy life. Easy choices, hard life." to "Jerzy Gregorek",
    "Either you run the day or the day runs you." to "Jim Rohn",
    "Fall seven times, stand up eight." to "Japanese Proverb",
    "Don’t let what you cannot do interfere with what you can do." to "John Wooden",
    "A year from now you may wish you had started today." to "Karen Lamb",
    "The only bad workout is the one that didn’t happen." to "Unknown",
    "Push yourself, because no one else is going to do it for you." to "Unknown",
    "You are never too old to set another goal or to dream a new dream." to "C.S. Lewis",
    "Success usually comes to those who are too busy to be looking for it." to "Henry David Thoreau",
    "The way to get started is to quit talking and begin doing." to "Walt Disney",
    "What you do every day matters more than what you do once in a while." to "Gretchen Rubin"
)

// Function to get a quote for a specific date
fun getQuoteForDate(date: LocalDate): Pair<String, String> {
    val baseDate = LocalDate.of(2025, 1, 1)
    val daysSinceStart = ChronoUnit.DAYS.between(baseDate, date)
    val index = (daysSinceStart % dailyQuotes.size).toInt()
    return dailyQuotes[index]
}

// Default function used in app (today's quote)
fun getTodayQuote(): Pair<String, String> = getQuoteForDate(LocalDate.now())







