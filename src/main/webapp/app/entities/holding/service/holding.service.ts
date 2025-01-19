import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHolding, NewHolding } from '../holding.model';

export type PartialUpdateHolding = Partial<IHolding> & Pick<IHolding, 'id'>;

export type EntityResponseType = HttpResponse<IHolding>;
export type EntityArrayResponseType = HttpResponse<IHolding[]>;

@Injectable({ providedIn: 'root' })
export class HoldingService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/holdings');

  create(holding: NewHolding): Observable<EntityResponseType> {
    return this.http.post<IHolding>(this.resourceUrl, holding, { observe: 'response' });
  }

  update(holding: IHolding): Observable<EntityResponseType> {
    return this.http.put<IHolding>(`${this.resourceUrl}/${this.getHoldingIdentifier(holding)}`, holding, { observe: 'response' });
  }

  partialUpdate(holding: PartialUpdateHolding): Observable<EntityResponseType> {
    return this.http.patch<IHolding>(`${this.resourceUrl}/${this.getHoldingIdentifier(holding)}`, holding, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHolding>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHolding[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHoldingIdentifier(holding: Pick<IHolding, 'id'>): number {
    return holding.id;
  }

  compareHolding(o1: Pick<IHolding, 'id'> | null, o2: Pick<IHolding, 'id'> | null): boolean {
    return o1 && o2 ? this.getHoldingIdentifier(o1) === this.getHoldingIdentifier(o2) : o1 === o2;
  }

  addHoldingToCollectionIfMissing<Type extends Pick<IHolding, 'id'>>(
    holdingCollection: Type[],
    ...holdingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const holdings: Type[] = holdingsToCheck.filter(isPresent);
    if (holdings.length > 0) {
      const holdingCollectionIdentifiers = holdingCollection.map(holdingItem => this.getHoldingIdentifier(holdingItem));
      const holdingsToAdd = holdings.filter(holdingItem => {
        const holdingIdentifier = this.getHoldingIdentifier(holdingItem);
        if (holdingCollectionIdentifiers.includes(holdingIdentifier)) {
          return false;
        }
        holdingCollectionIdentifiers.push(holdingIdentifier);
        return true;
      });
      return [...holdingsToAdd, ...holdingCollection];
    }
    return holdingCollection;
  }
}
