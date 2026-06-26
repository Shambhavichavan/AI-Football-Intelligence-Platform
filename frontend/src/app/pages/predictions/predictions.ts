import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Football } from '../../services/football';

type PredictionResponse = {
  home: string;
  away: string;
  homeWin: number;
  draw: number;
  awayWin: number;
  confidenceScore: number;
};

@Component({
  selector: 'app-predictions',
  imports: [CommonModule, FormsModule],
  templateUrl: './predictions.html',
  styleUrl: './predictions.scss',
})
export class Predictions implements OnInit {
  teams: string[] = [];
  homeTeam = '';
  awayTeam = '';
  prediction: PredictionResponse | null = null;
  errorMessage = '';

  constructor(
    private footballService: Football,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.footballService.getMatches().subscribe((data: any) => {
      const matches = Array.isArray(data) ? data : data?.value || data?.matches || [];
      const set = new Set<string>();
      matches.forEach((m: any) => {
        if (m.homeTeam) set.add(m.homeTeam);
        if (m.awayTeam) set.add(m.awayTeam);
      });
      this.teams = Array.from(set).sort();
      if (this.teams.length >= 2) {
        this.homeTeam = this.teams[0];
        this.awayTeam = this.teams[1];
      }
      this.changeDetectorRef.detectChanges();
    });
  }

  predict(): void {
    this.errorMessage = '';
    if (!this.homeTeam || !this.awayTeam || this.homeTeam === this.awayTeam) {
      this.errorMessage = 'Please choose two different teams.';
      return;
    }

    this.footballService.predictMatch(this.homeTeam, this.awayTeam).subscribe((data: any) => {
      this.prediction = data;
      this.changeDetectorRef.detectChanges();
    }, () => {
      this.prediction = null;
      this.errorMessage = 'Prediction failed. Please ensure backend is running on port 8080.';
      this.changeDetectorRef.detectChanges();
    });
  }
}
