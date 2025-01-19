import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'xcopilotApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-extra',
    data: { pageTitle: 'xcopilotApp.userExtra.home.title' },
    loadChildren: () => import('./user-extra/user-extra.routes'),
  },
  {
    path: 'portfolio',
    data: { pageTitle: 'xcopilotApp.portfolio.home.title' },
    loadChildren: () => import('./portfolio/portfolio.routes'),
  },
  {
    path: 'holding',
    data: { pageTitle: 'xcopilotApp.holding.home.title' },
    loadChildren: () => import('./holding/holding.routes'),
  },
  {
    path: 'transaction',
    data: { pageTitle: 'xcopilotApp.transaction.home.title' },
    loadChildren: () => import('./transaction/transaction.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
