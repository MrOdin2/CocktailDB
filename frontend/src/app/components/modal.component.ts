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
  styles: []
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
