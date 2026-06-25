import { ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Football } from '../../services/football';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-analytics',
  imports: [CommonModule, FormsModule],
  templateUrl: './analytics.html',
  styleUrl: './analytics.scss',
})
export class Analytics implements OnInit, OnDestroy {
  @ViewChild('winPercentageChart', { static: false }) winPercentageChart: ElementRef | undefined;
  @ViewChild('goalsScoredChart', { static: false }) goalsScoredChart: ElementRef | undefined;
  @ViewChild('goalsConcededChart', { static: false }) goalsConcededChart: ElementRef | undefined;

  matches: any[] = [];
  teams: string[] = [];
  selectedTeam: string = '';
  stats: any = {
    wins: 0,
    draws: 0,
    losses: 0,
    goalsScored: 0,
    goalsConceded: 0,
    winPercentage: 0,
    completedMatches: 0,
  };

  chartInstances: { [key: string]: Chart | undefined } = {};

  constructor(
    private footballService: Football,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // First, load all teams from matches
    this.footballService.getMatches().subscribe((data: any) => {
      this.matches = Array.isArray(data) ? data : data?.value || data?.matches || [];
      this.extractTeams();
      if (this.teams.length > 0) {
        this.selectedTeam = this.teams[0];
        this.loadTeamAnalytics(this.selectedTeam);
      }
    });
  }

  extractTeams(): void {
    const teamSet = new Set<string>();
    this.matches.forEach((match) => {
      if (match.homeTeam) teamSet.add(match.homeTeam);
      if (match.awayTeam) teamSet.add(match.awayTeam);
    });
    this.teams = Array.from(teamSet).sort();
  }

  onTeamChange(team: any): void {
    const teamValue = team.target?.value || team;
    this.selectedTeam = teamValue;
    this.loadTeamAnalytics(teamValue);
  }

  loadTeamAnalytics(team: string): void {
    this.footballService.getTeamForm(team).subscribe((data: any) => {
      const teamData = Array.isArray(data) ? data[0] : data;
      this.calculateStats(teamData);
      this.changeDetectorRef.detectChanges();
      setTimeout(() => this.createCharts(), 100);
    });
  }

  calculateStats(teamData: any): void {
    this.stats.wins = teamData?.wins || 0;
    this.stats.draws = teamData?.draws || 0;
    this.stats.losses = teamData?.losses || 0;
    this.stats.goalsScored = 0;
    this.stats.goalsConceded = 0;

    const totalMatches = this.stats.wins + this.stats.draws + this.stats.losses;
    this.stats.winPercentage = totalMatches > 0 ? Math.round((this.stats.wins / totalMatches) * 100) : 0;
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
