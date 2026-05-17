package com.example.madhumargaagriculture

import com.example.madhumargaagriculture.model.AlertItem

object HiveHealthAnalyzer {

    fun analyzeHive(
        queenSeen: Boolean,
        pestsSeen: Boolean,
        honeyFlow: String,
        colonyLevel: String
    ): String {

        return when {

            !queenSeen ->
                "Queen may be missing."

            pestsSeen ->
                "Pest treatment required."

            honeyFlow == "Low" ->
                "Low nectar collection detected."

            colonyLevel == "Low" ->
                "Low colony activity alert."

            else ->
                "Hive is healthy."
        }
    }

    fun generateAlerts(
        queenSeen: Boolean,
        pestsSeen: Boolean,
        temperature: Int,
        beeActivity: String
    ): List<AlertItem> {

        val alerts = mutableListOf<AlertItem>()

        if (!queenSeen) {
            alerts.add(
                AlertItem(
                    title = "Queen Missing",
                    message = "Queen bee not detected.",
                    type = "CRITICAL"
                )
            )
        }

        if (pestsSeen) {
            alerts.add(
                AlertItem(
                    title = "Pest Alert",
                    message = "Pests detected in hive.",
                    type = "WARNING"
                )
            )
        }

        if (beeActivity == "Low") {
            alerts.add(
                AlertItem(
                    title = "Low Activity Alert",
                    message = "Immediate hive inspection needed.",
                    type = "CRITICAL"
                )
            )
        }

        if (temperature > 40) {
            alerts.add(
                AlertItem(
                    title = "High Temperature",
                    message = "Hive overheating risk detected.",
                    type = "WARNING"
                )
            )
        }

        return alerts
    }
}
