package com.realtorsuccessiq.util

import com.realtorsuccessiq.data.model.Contact
import com.realtorsuccessiq.data.model.Task
import com.realtorsuccessiq.data.repository.LocalRepository
import kotlinx.coroutines.flow.first

data class Suggestion(
    val contact: Contact,
    val task: Task?,
    val score: Float,
    val reason: String
)

class SuggestionEngine(
    private val localRepository: LocalRepository
) {
    suspend fun getTopSuggestions(limit: Int = 10): List<Suggestion> {
        val contacts = localRepository.getAllContacts().first()
        val tasks = localRepository.getPendingTasks().first()
        val overdueTasks = localRepository.getOverdueTasks()
        val now = System.currentTimeMillis()
        
        val suggestions = mutableListOf<Suggestion>()
        
        // Score each contact
        for (contact in contacts) {
            var score = 0f
            val reasons = mutableListOf<String>()
            
            // Check for overdue task
            val overdueTask = overdueTasks.find { it.personId == contact.id }
            if (overdueTask != null) {
                score += 50f
                reasons.add("Overdue task: ${overdueTask.title}")
            }
            
            // Segment scoring
            when (contact.segment) {
                "A" -> {
                    score += 30f
                    reasons.add("Segment A contact")
                }
                "B" -> {
                    score += 15f
                    reasons.add("Segment B contact")
                }
                "C" -> {
                    score += 5f
                    reasons.add("Segment C contact")
                }
            }
            
            // Days since last contact
            if (contact.lastContactedAt != null) {
                val daysSince = (now - contact.lastContactedAt) / (24 * 60 * 60 * 1000L)
                score += minOf(25f, daysSince.toFloat())
                if (daysSince > 0) {
                    reasons.add("Not contacted in ${daysSince} days")
                }
            } else {
                score += 25f
                reasons.add("Never contacted")
            }
            
            // Tag bonuses
            val tags = contact.getTagsList()
            if (tags.contains("Past Client")) {
                score += 15f
                reasons.add("Past client")
            }
            if (tags.contains("Top 50")) {
                score += 15f
                reasons.add("Top 50 contact")
            }
            
            // Recent contact penalty (within last 3 days)
            if (contact.lastContactedAt != null) {
                val daysSince = (now - contact.lastContactedAt) / (24 * 60 * 60 * 1000L)
                if (daysSince < 3) {
                    score -= 10f
                }
            }
            
            val task = tasks.find { it.personId == contact.id }
            val reason = if (reasons.isNotEmpty()) reasons.joinToString(" • ") else "High priority contact"
            
            suggestions.add(
                Suggestion(
                    contact = contact,
                    task = task,
                    score = score,
                    reason = reason
                )
            )
        }
        
        // Sort by score descending and return top N
        return suggestions.sortedByDescending { it.score }.take(limit)
    }
}

