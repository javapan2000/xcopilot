import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHolding } from '../holding.model';
import { HoldingService } from '../service/holding.service';

const holdingResolve = (route: ActivatedRouteSnapshot): Observable<null | IHolding> => {
  const id = route.params.id;
  if (id) {
    return inject(HoldingService)
      .find(id)
      .pipe(
        mergeMap((holding: HttpResponse<IHolding>) => {
          if (holding.body) {
            return of(holding.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default holdingResolve;
