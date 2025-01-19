import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PortfolioResolve from './route/portfolio-routing-resolve.service';

const portfolioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/portfolio.component').then(m => m.PortfolioComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/portfolio-detail.component').then(m => m.PortfolioDetailComponent),
    resolve: {
      portfolio: PortfolioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/portfolio-update.component').then(m => m.PortfolioUpdateComponent),
    resolve: {
      portfolio: PortfolioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/portfolio-update.component').then(m => m.PortfolioUpdateComponent),
    resolve: {
      portfolio: PortfolioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default portfolioRoute;
