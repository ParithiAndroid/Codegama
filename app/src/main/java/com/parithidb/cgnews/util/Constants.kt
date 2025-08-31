package com.parithidb.cgnews.util

class Constants {
    companion object {
        var DEVELOPMENT_MODE: Boolean = true
        val NEWS_SERVER: String = "https://newsapi.org/v2/"

        // API Response Codes

        val STATUS_CODE_SUCCESS: Int = 200

        val STATUS_CODE_SERVER_RESPONSE_MISSING_DATA: Int = 400

        val STATUS_CODE_CONNECTIVITY_ISSUE: Int = 504

        val STATUS_CODE_TIMEOUT: Int = 408

        val DATA_NOT_FOUND: Int = 403

        val STATUS_CODE_RESPONSE_NOT_FOUND: Int = 404
    }
}