package com.cocktaildb.controller

import com.cocktaildb.ingredient.StockUpdateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/stock-updates")
@Tag(name = "Stock Updates", description = "Real-time stock update notifications via Server-Sent Events (SSE)")
class StockUpdateController(
    private val stockUpdateService: StockUpdateService
) {
    
    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Subscribe to stock updates",
        description = "Establish a Server-Sent Events (SSE) connection to receive real-time ingredient stock updates"
    )
    @ApiResponse(
        responseCode = "200",
        description = "SSE connection established",
        content = [Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE)]
    )
    fun subscribeToStockUpdates(): SseEmitter {
        return stockUpdateService.createEmitter()
    }
}
