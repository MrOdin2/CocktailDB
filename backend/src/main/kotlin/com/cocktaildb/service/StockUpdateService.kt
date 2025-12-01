package com.cocktaildb.service

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class StockUpdateService {
    
    private val emitters = CopyOnWriteArrayList<SseEmitter>()
    private val executor = Executors.newSingleThreadScheduledExecutor()
    
    init {
        // Send heartbeat every 30 seconds to keep connections alive
        executor.scheduleAtFixedRate({
            broadcastHeartbeat()
        }, 30, 30, TimeUnit.SECONDS)
    }
    
    fun createEmitter(): SseEmitter {
        val emitter = SseEmitter(0L) // No timeout
        
        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter) }
        emitter.onError { emitters.remove(emitter) }
        
        emitters.add(emitter)
        
        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data(mapOf("status" to "connected")))
        } catch (e: Exception) {
            emitters.remove(emitter)
        }
        
        return emitter
    }
    
    fun broadcastStockUpdate() {
        val deadEmitters = mutableListOf<SseEmitter>()
        val timestamp = System.currentTimeMillis()
        
        emitters.forEach { emitter ->
            try {
                emitter.send(SseEmitter.event()
                    .name("stock-update")
                    .data(mapOf("timestamp" to timestamp)))
            } catch (e: Exception) {
                deadEmitters.add(emitter)
            }
        }
        
        emitters.removeAll(deadEmitters)
    }
    
    private fun broadcastHeartbeat() {
        val deadEmitters = mutableListOf<SseEmitter>()
        
        emitters.forEach { emitter ->
            try {
                emitter.send(SseEmitter.event()
                    .name("heartbeat")
                    .data(mapOf("timestamp" to System.currentTimeMillis())))
            } catch (e: Exception) {
                deadEmitters.add(emitter)
            }
        }
        
        emitters.removeAll(deadEmitters)
    }
    
    fun getConnectedClients(): Int = emitters.size
}
