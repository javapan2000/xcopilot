import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import UserExtraResolve from './route/user-extra-routing-resolve.service';

const userExtraRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-extra.component').then(m => m.UserExtraComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-extra-detail.component').then(m => m.UserExtraDetailComponent),
    resolve: {
      userExtra: UserExtraResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-extra-update.component').then(m => m.UserExtraUpdateComponent),
    resolve: {
      userExtra: UserExtraResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-extra-update.component').then(m => m.UserExtraUpdateComponent),
    resolve: {
      userExtra: UserExtraResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userExtraRoute;
