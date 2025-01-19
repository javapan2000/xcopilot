import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import HoldingResolve from './route/holding-routing-resolve.service';

const holdingRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/holding.component').then(m => m.HoldingComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/holding-detail.component').then(m => m.HoldingDetailComponent),
    resolve: {
      holding: HoldingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/holding-update.component').then(m => m.HoldingUpdateComponent),
    resolve: {
      holding: HoldingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/holding-update.component').then(m => m.HoldingUpdateComponent),
    resolve: {
      holding: HoldingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default holdingRoute;
