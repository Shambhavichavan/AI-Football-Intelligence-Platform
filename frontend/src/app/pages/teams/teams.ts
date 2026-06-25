import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Football } from '../../services/football';

type TeamStat = {
  name: string;
  played: number;
  wins: number;
  draws: number;
  losses: number;
  goalsFor: number;
  goalsAgainst: number;
};

@Component({
  selector: 'app-teams',
  imports: [CommonModule],
  templateUrl: './teams.html',
  styleUrl: './teams.scss',
})
export class Teams implements OnInit {
  teams: TeamStat[] = [];

  constructor(
    private footballService: Football,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.footballService.getMatches().subscribe((data: any) => {
      const matches = Array.isArray(data) ? data : data?.value || data?.matches || [];
      this.teams = this.buildTeamStats(matches);
      this.changeDetectorRef.detectChanges();
    });
  }

  private buildTeamStats(matches: any[]): TeamStat[] {
    const table = new Map<string, TeamStat>();

    const ensureTeam = (name: string): TeamStat => {
      if (!table.has(name)) {
        table.set(name, {
          name,
          played: 0,
          wins: 0,
          draws: 0,
          losses: 0,
          goalsFor: 0,
          goalsAgainst: 0,
        });
      }
      return table.get(name)!;
    };

    matches.forEach((match: any) => {
      const homeName = match?.homeTeam;
      const awayName = match?.awayTeam;
      if (!homeName || !awayName) {
        return;
      }

      const home = ensureTeam(homeName);
      const away = ensureTeam(awayName);

      const homeGoals = match?.homeScore;
      const awayGoals = match?.awayScore;

      if (homeGoals === null || awayGoals === null || homeGoals === undefined || awayGoals === undefined) {
        home.played += 1;
        away.played += 1;
        return;
      }

      home.played += 1;
      away.played += 1;

      home.goalsFor += homeGoals;
      home.goalsAgainst += awayGoals;
      away.goalsFor += awayGoals;
      away.goalsAgainst += homeGoals;

      if (homeGoals > awayGoals) {
        home.wins += 1;
        away.losses += 1;
      } else if (awayGoals > homeGoals) {
        away.wins += 1;
        home.losses += 1;
      } else {
        home.draws += 1;
        away.draws += 1;
      }
    });

    return Array.from(table.values()).sort((a, b) => b.wins - a.wins || b.goalsFor - a.goalsFor);
  }
}
