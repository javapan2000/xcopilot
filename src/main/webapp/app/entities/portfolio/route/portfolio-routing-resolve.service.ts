import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPortfolio } from '../portfolio.model';
import { PortfolioService } from '../service/portfolio.service';

const portfolioResolve = (route: ActivatedRouteSnapshot): Observable<null | IPortfolio> => {
  const id = route.params.id;
  if (id) {
    return inject(PortfolioService)
      .find(id)
      .pipe(
        mergeMap((portfolio: HttpResponse<IPortfolio>) => {
          if (portfolio.body) {
            return of(portfolio.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default portfolioResolve;
