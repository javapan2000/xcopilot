import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPortfolio } from 'app/entities/portfolio/portfolio.model';
import { PortfolioService } from 'app/entities/portfolio/service/portfolio.service';
import { IHolding } from '../holding.model';
import { HoldingService } from '../service/holding.service';
import { HoldingFormGroup, HoldingFormService } from './holding-form.service';

@Component({
  selector: 'jhi-holding-update',
  templateUrl: './holding-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class HoldingUpdateComponent implements OnInit {
  isSaving = false;
  holding: IHolding | null = null;

  portfoliosSharedCollection: IPortfolio[] = [];

  protected holdingService = inject(HoldingService);
  protected holdingFormService = inject(HoldingFormService);
  protected portfolioService = inject(PortfolioService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: HoldingFormGroup = this.holdingFormService.createHoldingFormGroup();

  comparePortfolio = (o1: IPortfolio | null, o2: IPortfolio | null): boolean => this.portfolioService.comparePortfolio(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ holding }) => {
      this.holding = holding;
      if (holding) {
        this.updateForm(holding);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const holding = this.holdingFormService.getHolding(this.editForm);
    if (holding.id !== null) {
      this.subscribeToSaveResponse(this.holdingService.update(holding));
    } else {
      this.subscribeToSaveResponse(this.holdingService.create(holding));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHolding>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(holding: IHolding): void {
    this.holding = holding;
    this.holdingFormService.resetForm(this.editForm, holding);

    this.portfoliosSharedCollection = this.portfolioService.addPortfolioToCollectionIfMissing<IPortfolio>(
      this.portfoliosSharedCollection,
      holding.portfolio,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.portfolioService
      .query()
      .pipe(map((res: HttpResponse<IPortfolio[]>) => res.body ?? []))
      .pipe(
        map((portfolios: IPortfolio[]) =>
          this.portfolioService.addPortfolioToCollectionIfMissing<IPortfolio>(portfolios, this.holding?.portfolio),
        ),
      )
      .subscribe((portfolios: IPortfolio[]) => (this.portfoliosSharedCollection = portfolios));
  }
}
