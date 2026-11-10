package com.toolbox.ai

import com.toolbox.dashboard.allTools
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EpicToolsCoverageTest {

    @Test
    fun testEpicToolsFullCoverageAndMergedDuplicates() {
        val toolIds = allTools.map { it.id }.toSet()

        // 81 Direct Epic Tools registered in allTools
        val expectedDirectEpicTools = listOf(
            "json-csv", "xml-json", "yaml", "base64", "timezone", "batch",
            "datediff", "subnet", "word-rank", "finance", "image", "image-lab",
            "pdf", "docx-md", "doc-translator", "text", "md-editor", "pomodoro",
            "countdown", "panchangam", "social", "social-downloader", "bluetooth",
            "image-gen", "chat", "local", "api-tester", "sql", "xml-fmt",
            "json-ts", "kusto", "diff", "jwt", "url", "cron",
            "minifier", "qr-barcode", "inspiration", "md-table", "sql-builder",
            "ping", "dns", "whois", "ip-info", "geo", "ssl",
            "anonymizer", "security", "rsa", "otp", "agent-ingest", "agent-generate",
            "agent-results", "agent-setup", "notion-ingest", "notion-folder", "notion-scraper",
            "notion-history", "notion-setup", "viewer", "science", "adv-data",
            "reconcile", "mock", "synthetic", "fashion-guide", "clothes-guide", "traditional-guide",
            "worldwide", "size-guide", "shoes", "accessories", "extension-sources", "archive",
            "url2pdf", "userscripts", "bookmarklets", "url2markdown", "status", "telemetry", "lineage"
        )

        for (id in expectedDirectEpicTools) {
            assertTrue("Expected Epic tool $id must be registered in allTools", id in toolIds)
        }

        // 12 Merged Duplicates mapped to Native Tools
        val mergedNativeTools = listOf(
            "date_calculator",       // AgeCalculator
            "color_picker",          // ColorPicker
            "currency_converter",    // CurrencyConverter
            "json_formatter",        // JsonFormatter
            "ocr",                   // OcrTool
            "password_generator",    // PasswordTool
            "regex_tester",          // RegexTester
            "network_speed_test",    // SpeedTest
            "stopwatch_timer",       // Stopwatch
            "timestamp_converter",   // TimestampTool
            "unit_converter",        // UnitConverter
            "world_clock"            // WorldClock
        )

        for (id in mergedNativeTools) {
            assertTrue("Merged duplicate native tool $id must be registered in allTools", id in toolIds)
        }

        // Total suite coverage: 81 direct + 12 merged = 93 Epic tools accounting
        assertEquals(81, expectedDirectEpicTools.size)
        assertEquals(12, mergedNativeTools.size)
    }
}
