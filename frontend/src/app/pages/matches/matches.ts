import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Football } from '../../services/football';

@Component({
  selector: 'app-matches',
  imports: [CommonModule],
  templateUrl: './matches.html',
  styleUrl: './matches.scss',
})
export class Matches implements OnInit {
  upcomingMatches: any[] = [];
  liveMatches: any[] = [];

  constructor(
    private footballService: Football,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.footballService.getUpcomingMatches().subscribe((data: any) => {
      this.upcomingMatches = Array.isArray(data) ? data : data?.value || data?.matches || [];
      this.changeDetectorRef.detectChanges();
    });

    this.footballService.getLiveMatches().subscribe((data: any) => {
      this.liveMatches = Array.isArray(data) ? data : data?.value || data?.matches || [];
      this.changeDetectorRef.detectChanges();
    });
  }
}
