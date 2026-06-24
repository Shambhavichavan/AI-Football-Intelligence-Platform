import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Football {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getMatches(): Observable<any> {
    return this.http.get(`${this.apiUrl}/matches`);
  }

  getUpcomingMatches(): Observable<any> {
    return this.http.get(`${this.apiUrl}/matches/upcoming`);
  }

  getLiveMatches(): Observable<any> {
    return this.http.get(`${this.apiUrl}/matches/live`);
  }

  getTeamForm(): Observable<any> {
    return this.http.get(`${this.apiUrl}/teams/form`);
  }
}
