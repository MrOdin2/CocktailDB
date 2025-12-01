import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { Subject, Observable } from 'rxjs';
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

  constructor(private ngZone: NgZone) {}

  /**
   * Connects to the SSE endpoint for stock updates.
   * Should be called when components need real-time updates.
   */
  connect(): void {
    if (this.eventSource) {
      return; // Already connected
    }

    this.createEventSource();
  }

  private createEventSource(): void {
    const baseUrl = environment.apiUrl.replace('/api', '');
    const sseUrl = `${baseUrl}/api/stock-updates`;

    try {
      this.eventSource = new EventSource(sseUrl);

      this.eventSource.onopen = () => {
        this.ngZone.run(() => {
          this.reconnectAttempts = 0;
          this.connectionStatus.next(true);
        });
      };

      this.eventSource.addEventListener('connected', (event: MessageEvent) => {
        console.log('Connected to stock updates stream');
      });

      this.eventSource.addEventListener('stock-update', (event: MessageEvent) => {
        this.ngZone.run(() => {
          this.stockUpdateSubject.next();
        });
      });

      this.eventSource.addEventListener('heartbeat', () => {
        // Heartbeat received - connection is alive
      });

      this.eventSource.onerror = (error) => {
        console.error('SSE connection error:', error);
        this.handleConnectionError();
      };

    } catch (error) {
      console.error('Failed to create EventSource:', error);
      this.handleConnectionError();
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
   * Disconnects from the SSE endpoint.
   * Should be called when no components need updates anymore.
   */
  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    this.closeEventSource();
    this.reconnectAttempts = 0;
  }

  ngOnDestroy(): void {
    this.disconnect();
    this.stockUpdateSubject.complete();
    this.connectionStatus.complete();
  }
}
