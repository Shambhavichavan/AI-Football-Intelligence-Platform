import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Analytics } from './pages/analytics/analytics';
import { Matches } from './pages/matches/matches';
import { Teams } from './pages/teams/teams';
import { Rankings } from './pages/rankings/rankings';
import { Predictions } from './pages/predictions/predictions';
import { Players } from './pages/players/players';
import { Sentiment } from './pages/sentiment/sentiment';
export const routes: Routes = [
	{ path: '', redirectTo: '/dashboard', pathMatch: 'full' },
	{ path: 'dashboard', component: Dashboard },
	{ path: 'analytics', component: Analytics },
	{ path: 'matches', component: Matches },
	{ path: 'teams', component: Teams },
	{ path: 'players', component: Players },
	{ path: 'sentiment', component: Sentiment },
	{ path: 'rankings', component: Rankings },
	{ path: 'predictions', component: Predictions },
];
