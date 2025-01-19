import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { IPortfolio } from '../portfolio.model';
import { PortfolioService } from '../service/portfolio.service';
import { PortfolioFormGroup, PortfolioFormService } from './portfolio-form.service';

@Component({
  selector: 'jhi-portfolio-update',
  templateUrl: './portfolio-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PortfolioUpdateComponent implements OnInit {
  isSaving = false;
  portfolio: IPortfolio | null = null;

  userExtrasSharedCollection: IUserExtra[] = [];

  protected portfolioService = inject(PortfolioService);
  protected portfolioFormService = inject(PortfolioFormService);
  protected userExtraService = inject(UserExtraService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PortfolioFormGroup = this.portfolioFormService.createPortfolioFormGroup();

  compareUserExtra = (o1: IUserExtra | null, o2: IUserExtra | null): boolean => this.userExtraService.compareUserExtra(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ portfolio }) => {
      this.portfolio = portfolio;
      if (portfolio) {
        this.updateForm(portfolio);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const portfolio = this.portfolioFormService.getPortfolio(this.editForm);
    if (portfolio.id !== null) {
      this.subscribeToSaveResponse(this.portfolioService.update(portfolio));
    } else {
      this.subscribeToSaveResponse(this.portfolioService.create(portfolio));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPortfolio>>): void {
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

  protected updateForm(portfolio: IPortfolio): void {
    this.portfolio = portfolio;
    this.portfolioFormService.resetForm(this.editForm, portfolio);

    this.userExtrasSharedCollection = this.userExtraService.addUserExtraToCollectionIfMissing<IUserExtra>(
      this.userExtrasSharedCollection,
      portfolio.owner,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userExtraService
      .query()
      .pipe(map((res: HttpResponse<IUserExtra[]>) => res.body ?? []))
      .pipe(
        map((userExtras: IUserExtra[]) =>
          this.userExtraService.addUserExtraToCollectionIfMissing<IUserExtra>(userExtras, this.portfolio?.owner),
        ),
      )
      .subscribe((userExtras: IUserExtra[]) => (this.userExtrasSharedCollection = userExtras));
  }
}
