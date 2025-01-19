import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IHolding } from 'app/entities/holding/holding.model';
import { HoldingService } from 'app/entities/holding/service/holding.service';
import { TransactionType } from 'app/entities/enumerations/transaction-type.model';
import { TransactionService } from '../service/transaction.service';
import { ITransaction } from '../transaction.model';
import { TransactionFormGroup, TransactionFormService } from './transaction-form.service';

@Component({
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;
  transaction: ITransaction | null = null;
  transactionTypeValues = Object.keys(TransactionType);

  holdingsSharedCollection: IHolding[] = [];

  protected transactionService = inject(TransactionService);
  protected transactionFormService = inject(TransactionFormService);
  protected holdingService = inject(HoldingService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TransactionFormGroup = this.transactionFormService.createTransactionFormGroup();

  compareHolding = (o1: IHolding | null, o2: IHolding | null): boolean => this.holdingService.compareHolding(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      this.transaction = transaction;
      if (transaction) {
        this.updateForm(transaction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transaction = this.transactionFormService.getTransaction(this.editForm);
    if (transaction.id !== null) {
      this.subscribeToSaveResponse(this.transactionService.update(transaction));
    } else {
      this.subscribeToSaveResponse(this.transactionService.create(transaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
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

  protected updateForm(transaction: ITransaction): void {
    this.transaction = transaction;
    this.transactionFormService.resetForm(this.editForm, transaction);

    this.holdingsSharedCollection = this.holdingService.addHoldingToCollectionIfMissing<IHolding>(
      this.holdingsSharedCollection,
      transaction.holding,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.holdingService
      .query()
      .pipe(map((res: HttpResponse<IHolding[]>) => res.body ?? []))
      .pipe(
        map((holdings: IHolding[]) => this.holdingService.addHoldingToCollectionIfMissing<IHolding>(holdings, this.transaction?.holding)),
      )
      .subscribe((holdings: IHolding[]) => (this.holdingsSharedCollection = holdings));
  }
}
