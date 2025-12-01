import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { Subject, Observable, Subscription } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StockUpdateService implements OnDestroy {
  private eventSource: EventSource | null = null;
  private stockUpdateSubject = new Subject<void>();
  private connectionStatus = new Subject<boolean>();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private connectionCount = 0;

  constructor(private ngZone: NgZone) {}

  /**
   * Helper method that handles connect, subscribe, and returns a cleanup function.
   * Use this in ngOnInit and call the returned function in ngOnDestroy.
   * 
   * @param onUpdate Callback function to invoke when stock updates occur
   * @returns Cleanup function to call in ngOnDestroy
   * 
   * @example
   * ```typescript
   * private cleanupStockUpdates?: () => void;
   * 
   * ngOnInit(): void {
   *   this.cleanupStockUpdates = this.stockUpdateService.subscribeToUpdates(() => {
   *     this.loadData();
   *   });
   * }
   * 
   * ngOnDestroy(): void {
   *   this.cleanupStockUpdates?.();
   * }
   * ```
   */
  subscribeToUpdates(onUpdate: () => void): () => void {
    this.connect();
    const subscription = this.stockUpdates$.subscribe(() => {
      onUpdate();
    });

    return () => {
      subscription.unsubscribe();
      this.disconnect();
    };
  }

  /**
   * Connects to the SSE endpoint for stock updates.
   * Uses reference counting to manage connections across multiple components.
   */
  connect(): void {
    this.connectionCount++;
    
    if (this.eventSource) {
      return; // Already connected
    }

    this.createEventSource();
  }

  private createEventSource(): void {
    const sseUrl = this.buildSseUrl();

    try {
      this.eventSource = new EventSource(sseUrl);

      this.eventSource.onopen = () => {
        this.ngZone.run(() => {
          this.reconnectAttempts = 0;
          this.connectionStatus.next(true);
        });
      };

      this.eventSource.addEventListener('connected', () => {
        // Connection established
      });

      this.eventSource.addEventListener('stock-update', () => {
        this.ngZone.run(() => {
          this.stockUpdateSubject.next();
        });
      });

      this.eventSource.addEventListener('heartbeat', () => {
        // Heartbeat received - connection is alive
      });

      this.eventSource.onerror = () => {
        this.handleConnectionError();
      };

    } catch (error) {
      console.error('Failed to create EventSource:', error);
      this.handleConnectionError();
    }
  }

  private buildSseUrl(): string {
    const apiUrl = environment.apiUrl;
    
    // Handle different API URL formats
    if (apiUrl.endsWith('/api')) {
      // e.g., 'http://localhost:8080/api' -> 'http://localhost:8080/api/stock-updates'
      return `${apiUrl}/stock-updates`;
    } else if (apiUrl === '/api') {
      // Relative URL for production
      return '/api/stock-updates';
    } else {
      // Fallback: append stock-updates path
      return `${apiUrl}/stock-updates`;
    }
  }

  private handleConnectionError(): void {
    this.ngZone.run(() => {
      this.connectionStatus.next(false);
    });

    this.closeEventSource();

    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
      console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts})`);

      this.reconnectTimer = setTimeout(() => {
        this.createEventSource();
      }, delay);
    } else {
      console.warn('Max reconnection attempts reached');
    }
  }

  private closeEventSource(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }

  /**
   * Observable that emits when a stock update occurs.
   * Components should subscribe to this to refresh their data.
   */
  get stockUpdates$(): Observable<void> {
    return this.stockUpdateSubject.asObservable();
  }

  /**
   * Observable that emits the connection status.
   */
  get connectionStatus$(): Observable<boolean> {
    return this.connectionStatus.asObservable();
  }

  /**
   * Decrements the connection count and disconnects when no components need updates.
   * Should be called in ngOnDestroy of components using this service.
   */
  disconnect(): void {
    this.connectionCount = Math.max(0, this.connectionCount - 1);
    
    if (this.connectionCount === 0) {
      this.forceDisconnect();
    }
  }

  private forceDisconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    this.closeEventSource();
    this.reconnectAttempts = 0;
  }

  ngOnDestroy(): void {
    this.forceDisconnect();
    this.stockUpdateSubject.complete();
    this.connectionStatus.complete();
  }
}
