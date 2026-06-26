import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Football } from '../../services/football';

type Player = {
  id: number;
  name: string;
  team: string;
  goals: number;
  assists: number;
  shots: number;
  minutes: number;
  rating: number;
  yellowCards: number;
  redCards: number;
  passAccuracy: number;
};

type PlayerAnalytics = {
  topScorer: string;
  mostAssists: string;
  bestPlayer: string;
  worstForm: string;
  averageRating: number;
};

@Component({
  selector: 'app-players',
  imports: [CommonModule],
  templateUrl: './players.html',
  styleUrl: './players.scss',
})
export class Players implements OnInit {
  players: Player[] = [];
  analytics: PlayerAnalytics | null = null;

  constructor(
    private footballService: Football,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.footballService.getPlayers().subscribe((playersData: any) => {
      this.players = Array.isArray(playersData) ? playersData : playersData?.value || [];

      this.footballService.getPlayerAnalytics().subscribe((analyticsData: any) => {
        this.analytics = analyticsData;
        this.changeDetectorRef.detectChanges();
      });
    });
  }
}
