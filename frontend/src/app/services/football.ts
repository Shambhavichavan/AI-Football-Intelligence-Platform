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
    return this.http.get(`${this.apiUrl}/matches/upcoming`);
  }

  getLiveMatches() {
    return this.http.get(`${this.apiUrl}/matches/live`);
  }

  getTeamForm() {
    return this.http.get(`${this.apiUrl}/teams/form`);
  }
}
