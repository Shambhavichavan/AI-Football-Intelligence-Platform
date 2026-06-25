import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Analytics } from './pages/analytics/analytics';
import { Matches } from './pages/matches/matches';
import { Teams } from './pages/teams/teams';
export const routes: Routes = [
	{ path: '', redirectTo: '/dashboard', pathMatch: 'full' },
	{ path: 'dashboard', component: Dashboard },
	{ path: 'analytics', component: Analytics },
	{ path: 'matches', component: Matches },
	{ path: 'teams', component: Teams },
];
