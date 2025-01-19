import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserExtra } from '../user-extra.model';
import { UserExtraService } from '../service/user-extra.service';

const userExtraResolve = (route: ActivatedRouteSnapshot): Observable<null | IUserExtra> => {
  const id = route.params.id;
  if (id) {
    return inject(UserExtraService)
      .find(id)
      .pipe(
        mergeMap((userExtra: HttpResponse<IUserExtra>) => {
          if (userExtra.body) {
            return of(userExtra.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default userExtraResolve;
