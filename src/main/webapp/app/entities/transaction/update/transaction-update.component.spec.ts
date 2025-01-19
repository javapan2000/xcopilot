import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IHolding } from 'app/entities/holding/holding.model';
import { HoldingService } from 'app/entities/holding/service/holding.service';
import { TransactionService } from '../service/transaction.service';
import { ITransaction } from '../transaction.model';
import { TransactionFormService } from './transaction-form.service';

import { TransactionUpdateComponent } from './transaction-update.component';

describe('Transaction Management Update Component', () => {
  let comp: TransactionUpdateComponent;
  let fixture: ComponentFixture<TransactionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let transactionFormService: TransactionFormService;
  let transactionService: TransactionService;
  let holdingService: HoldingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TransactionUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TransactionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransactionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transactionFormService = TestBed.inject(TransactionFormService);
    transactionService = TestBed.inject(TransactionService);
    holdingService = TestBed.inject(HoldingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Holding query and add missing value', () => {
      const transaction: ITransaction = { id: 15110 };
      const holding: IHolding = { id: 20233 };
      transaction.holding = holding;

      const holdingCollection: IHolding[] = [{ id: 20233 }];
      jest.spyOn(holdingService, 'query').mockReturnValue(of(new HttpResponse({ body: holdingCollection })));
      const additionalHoldings = [holding];
      const expectedCollection: IHolding[] = [...additionalHoldings, ...holdingCollection];
      jest.spyOn(holdingService, 'addHoldingToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(holdingService.query).toHaveBeenCalled();
      expect(holdingService.addHoldingToCollectionIfMissing).toHaveBeenCalledWith(
        holdingCollection,
        ...additionalHoldings.map(expect.objectContaining),
      );
      expect(comp.holdingsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const transaction: ITransaction = { id: 15110 };
      const holding: IHolding = { id: 20233 };
      transaction.holding = holding;

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(comp.holdingsSharedCollection).toContainEqual(holding);
      expect(comp.transaction).toEqual(transaction);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 29476 };
      jest.spyOn(transactionFormService, 'getTransaction').mockReturnValue(transaction);
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(transactionFormService.getTransaction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(transactionService.update).toHaveBeenCalledWith(expect.objectContaining(transaction));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 29476 };
      jest.spyOn(transactionFormService, 'getTransaction').mockReturnValue({ id: null });
      jest.spyOn(transactionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(transactionFormService.getTransaction).toHaveBeenCalled();
      expect(transactionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransaction>>();
      const transaction = { id: 29476 };
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transactionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareHolding', () => {
      it('Should forward to holdingService', () => {
        const entity = { id: 20233 };
        const entity2 = { id: 9274 };
        jest.spyOn(holdingService, 'compareHolding');
        comp.compareHolding(entity, entity2);
        expect(holdingService.compareHolding).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
