import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-visitor-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './visitor-menu.component.html',
  styleUrls: ['./visitor-menu.component.css']
})
export class VisitorMenuComponent {
  constructor(private router: Router) {}
}
