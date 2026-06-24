import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Football } from '../../services/football';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  matches: any[] = [];

  constructor(private footballService: Football) {}

  ngOnInit(): void {
    this.footballService.getMatches().subscribe((data: any) => {
      this.matches = data.matches || [];
    });
  }
}
