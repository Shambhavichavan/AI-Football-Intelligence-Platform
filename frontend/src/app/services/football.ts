import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Football {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

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
    const url = team ? `${this.apiUrl}/team-form?team=${encodeURIComponent(team)}` : `${this.apiUrl}/team-form`;
    return this.http.get(url);
  }
}
