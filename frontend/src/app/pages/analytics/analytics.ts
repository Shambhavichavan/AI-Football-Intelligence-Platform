import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Football } from '../../services/football';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-analytics',
  imports: [CommonModule],
  templateUrl: './analytics.html',
  styleUrl: './analytics.scss',
})
export class Analytics implements OnInit, OnDestroy {
  @ViewChild('winPercentageChart', { static: false }) winPercentageChart: ElementRef | undefined;
  @ViewChild('goalsScoredChart', { static: false }) goalsScoredChart: ElementRef | undefined;
  @ViewChild('goalsConcededChart', { static: false }) goalsConcededChart: ElementRef | undefined;

  teamForm: any = null;
  stats: any = {
    wins: 0,
    draws: 0,
    losses: 0,
    goalsScored: 0,
    goalsConceded: 0,
    winPercentage: 0,
  };

  chartInstances: { [key: string]: Chart | undefined } = {};

  constructor(private footballService: Football) {}

  ngOnInit(): void {
    this.footballService.getTeamForm().subscribe((data: any) => {
      this.teamForm = data;
      this.calculateStats(data);
      setTimeout(() => this.createCharts(), 100);
    });
  }

  calculateStats(data: any): void {
    if (data && data.matches) {
      this.stats.wins = data.matches.filter((m: any) => m.result === 'W').length;
      this.stats.draws = data.matches.filter((m: any) => m.result === 'D').length;
      this.stats.losses = data.matches.filter((m: any) => m.result === 'L').length;
      this.stats.goalsScored = data.matches.reduce((sum: number, m: any) => sum + (m.goalsFor || 0), 0);
      this.stats.goalsConceded = data.matches.reduce((sum: number, m: any) => sum + (m.goalsAgainst || 0), 0);
      
      const totalMatches = this.stats.wins + this.stats.draws + this.stats.losses;
      this.stats.winPercentage = totalMatches > 0 ? Math.round((this.stats.wins / totalMatches) * 100) : 0;
    }
  }

  createCharts(): void {
    this.createWinPercentageChart();
    this.createGoalsScoredChart();
    this.createGoalsConcededChart();
  }

  createWinPercentageChart(): void {
    if (this.winPercentageChart?.nativeElement) {
      const ctx = this.winPercentageChart.nativeElement.getContext('2d');
      if (this.chartInstances['winPercentage']) {
        this.chartInstances['winPercentage']?.destroy();
      }
      this.chartInstances['winPercentage'] = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['Wins', 'Draws', 'Losses'],
          datasets: [
            {
              data: [this.stats.wins, this.stats.draws, this.stats.losses],
              backgroundColor: ['#4CAF50', '#FFC107', '#F44336'],
              borderColor: ['#388E3C', '#FFA000', '#D32F2F'],
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          plugins: {
            legend: {
              position: 'bottom',
            },
          },
        },
      });
    }
  }

  createGoalsScoredChart(): void {
    if (this.goalsScoredChart?.nativeElement) {
      const ctx = this.goalsScoredChart.nativeElement.getContext('2d');
      if (this.chartInstances['goalsScored']) {
        this.chartInstances['goalsScored']?.destroy();
      }
      this.chartInstances['goalsScored'] = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Total Goals Scored'],
          datasets: [
            {
              label: 'Goals',
              data: [this.stats.goalsScored],
              backgroundColor: ['#2196F3'],
              borderColor: ['#1565C0'],
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          indexAxis: 'y' as const,
          plugins: {
            legend: {
              display: false,
            },
          },
          scales: {
            x: {
              beginAtZero: true,
              max: Math.max(this.stats.goalsScored, 50),
            },
          },
        },
      });
    }
  }

  createGoalsConcededChart(): void {
    if (this.goalsConcededChart?.nativeElement) {
      const ctx = this.goalsConcededChart.nativeElement.getContext('2d');
      if (this.chartInstances['goalsConceded']) {
        this.chartInstances['goalsConceded']?.destroy();
      }
      this.chartInstances['goalsConceded'] = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Total Goals Conceded'],
          datasets: [
            {
              label: 'Goals',
              data: [this.stats.goalsConceded],
              backgroundColor: ['#FF5722'],
              borderColor: ['#E64A19'],
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: true,
          indexAxis: 'y' as const,
          plugins: {
            legend: {
              display: false,
            },
          },
          scales: {
            x: {
              beginAtZero: true,
              max: Math.max(this.stats.goalsConceded, 50),
            },
          },
        },
      });
    }
  }

  ngOnDestroy(): void {
    Object.values(this.chartInstances).forEach((chart) => chart?.destroy());
  }
}
