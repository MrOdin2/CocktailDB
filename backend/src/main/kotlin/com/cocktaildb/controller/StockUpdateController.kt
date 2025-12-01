package com.cocktaildb.controller

import com.cocktaildb.service.StockUpdateService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/stock-updates")
class StockUpdateController(
    private val stockUpdateService: StockUpdateService
) {
    
    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribeToStockUpdates(): SseEmitter {
        return stockUpdateService.createEmitter()
    }
}
