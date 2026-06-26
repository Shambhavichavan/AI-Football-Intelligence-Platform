import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Football {
  private apiUrl = this.resolveApiUrl();

  constructor(private http: HttpClient) {}

  private resolveApiUrl(): string {
    if (typeof window === 'undefined') {
      return 'http://localhost:8080/api';
    }

    if (window.location.hostname === 'localhost' && window.location.port === '4200') {
      return 'http://localhost:8080/api';
    }

    return `${window.location.origin}/api`;
  }

  getMatches() {
    return this.http.get(`${this.apiUrl}/matches`);
  }

  getUpcomingMatches() {
    return this.http.get(`${this.apiUrl}/upcoming-matches`);
  }

  getLiveMatches() {
    return this.http.get(`${this.apiUrl}/live-matches`);
  }

  getTeamForm(team?: string) {
    const url = team ? `${this.apiUrl}/team-form/${encodeURIComponent(team)}` : `${this.apiUrl}/team-form/Argentina`;
    return this.http.get(url);
  }

  getTeamStatistics(team?: string) {
    const url = team
      ? `${this.apiUrl}/team-statistics/${encodeURIComponent(team)}`
      : `${this.apiUrl}/team-statistics/Argentina`;
    return this.http.get(url);
  }

  getTeamRankings() {
    return this.http.get(`${this.apiUrl}/team-rankings`);
  }

  predictMatch(home: string, away: string) {
    return this.http.post(`${this.apiUrl}/predict`, { home, away });
  }

  getPlayers() {
    return this.http.get(`${this.apiUrl}/players`);
  }

  getPlayerAnalytics() {
    return this.http.get(`${this.apiUrl}/players/analytics`);
  }

  getSentiments() {
    return this.http.get(`${this.apiUrl}/sentiments`);
  }

  getSentimentSummary() {
    return this.http.get(`${this.apiUrl}/sentiments/summary`);
  }

  analyzeSentiment(fanName: string, team: string, message: string) {
    return this.http.post(`${this.apiUrl}/sentiments/analyze`, { fanName, team, message });
  }
}
