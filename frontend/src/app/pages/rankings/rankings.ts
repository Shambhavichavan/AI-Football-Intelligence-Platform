import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Football } from '../../services/football';

type RankingItem = {
  name: string;
  rating: number;
};

@Component({
  selector: 'app-rankings',
  imports: [CommonModule],
  templateUrl: './rankings.html',
  styleUrl: './rankings.scss',
})
export class Rankings implements OnInit {
  rankings: RankingItem[] = [];

  constructor(
    private footballService: Football,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.footballService.getTeamRankings().subscribe((data: any) => {
      const list = Array.isArray(data) ? data : data?.value || [];
      this.rankings = list;
      this.changeDetectorRef.detectChanges();
    });
  }
}
