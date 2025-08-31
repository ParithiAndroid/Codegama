package com.parithidb.cgnews.data.api.errorHandling

data class ApiStatusResponse(
    var status: Int,
    var message: String? = null
)