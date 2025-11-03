import { Component, EventEmitter, Input, Output, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-backdrop" *ngIf="isOpen" (click)="onBackdropClick()">
      <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="modal-title" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3 id="modal-title">{{ title }}</h3>
          <button type="button" class="close-button" aria-label="Close modal" (click)="close()">&times;</button>
        </div>
        <div class="modal-body">
          <ng-content></ng-content>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-backdrop {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }

    .modal-content {
      background: white;
      border-radius: 8px;
      max-width: 600px;
      width: 90%;
      max-height: 90vh;
      overflow-y: auto;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #ddd;
    }

    .modal-header h3 {
      margin: 0;
      color: #333;
    }

    .close-button {
      background: none;
      border: none;
      font-size: 28px;
      cursor: pointer;
      color: #666;
      padding: 0;
      width: 30px;
      height: 30px;
      line-height: 1;
    }

    .close-button:hover {
      color: #000;
    }

    .modal-body {
      padding: 20px;
    }
  `]
})
export class ModalComponent {
  @Input() isOpen = false;
  @Input() title = '';
  @Output() closed = new EventEmitter<void>();

  // Static stack to track open modals
  private static openModals: ModalComponent[] = [];

  // Track previous isOpen value to detect changes
  private wasOpen = false;

  ngDoCheck(): void {
    // Detect when isOpen changes
    if (this.isOpen && !this.wasOpen) {
      // Opened: push to stack if not already present
      if (!ModalComponent.openModals.includes(this)) {
        ModalComponent.openModals.push(this);
      }
    } else if (!this.isOpen && this.wasOpen) {
      // Closed: remove from stack
      this.removeFromStack();
    }
    this.wasOpen = this.isOpen;
  }

  ngOnDestroy(): void {
    this.removeFromStack();
  }

  private removeFromStack(): void {
    const idx = ModalComponent.openModals.indexOf(this);
    if (idx !== -1) {
      ModalComponent.openModals.splice(idx, 1);
    }
  }

  @HostListener('document:keydown.escape')
  onEscapeKey(): void {
    if (
      this.isOpen &&
      ModalComponent.openModals.length > 0 &&
      ModalComponent.openModals[ModalComponent.openModals.length - 1] === this
    ) {
      this.close();
    }
  }

  close(): void {
    this.isOpen = false;
    this.closed.emit();
    this.removeFromStack();
  }

  onBackdropClick(): void {
    this.close();
  }
}
