import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPortfolio } from 'app/entities/portfolio/portfolio.model';
import { PortfolioService } from 'app/entities/portfolio/service/portfolio.service';
import { HoldingService } from '../service/holding.service';
import { IHolding } from '../holding.model';
import { HoldingFormService } from './holding-form.service';

import { HoldingUpdateComponent } from './holding-update.component';

describe('Holding Management Update Component', () => {
  let comp: HoldingUpdateComponent;
  let fixture: ComponentFixture<HoldingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let holdingFormService: HoldingFormService;
  let holdingService: HoldingService;
  let portfolioService: PortfolioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HoldingUpdateComponent],
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
      .overrideTemplate(HoldingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HoldingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    holdingFormService = TestBed.inject(HoldingFormService);
    holdingService = TestBed.inject(HoldingService);
    portfolioService = TestBed.inject(PortfolioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Portfolio query and add missing value', () => {
      const holding: IHolding = { id: 9274 };
      const portfolio: IPortfolio = { id: 17791 };
      holding.portfolio = portfolio;

      const portfolioCollection: IPortfolio[] = [{ id: 17791 }];
      jest.spyOn(portfolioService, 'query').mockReturnValue(of(new HttpResponse({ body: portfolioCollection })));
      const additionalPortfolios = [portfolio];
      const expectedCollection: IPortfolio[] = [...additionalPortfolios, ...portfolioCollection];
      jest.spyOn(portfolioService, 'addPortfolioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ holding });
      comp.ngOnInit();

      expect(portfolioService.query).toHaveBeenCalled();
      expect(portfolioService.addPortfolioToCollectionIfMissing).toHaveBeenCalledWith(
        portfolioCollection,
        ...additionalPortfolios.map(expect.objectContaining),
      );
      expect(comp.portfoliosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const holding: IHolding = { id: 9274 };
      const portfolio: IPortfolio = { id: 17791 };
      holding.portfolio = portfolio;

      activatedRoute.data = of({ holding });
      comp.ngOnInit();

      expect(comp.portfoliosSharedCollection).toContainEqual(portfolio);
      expect(comp.holding).toEqual(holding);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHolding>>();
      const holding = { id: 20233 };
      jest.spyOn(holdingFormService, 'getHolding').mockReturnValue(holding);
      jest.spyOn(holdingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ holding });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: holding }));
      saveSubject.complete();

      // THEN
      expect(holdingFormService.getHolding).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(holdingService.update).toHaveBeenCalledWith(expect.objectContaining(holding));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHolding>>();
      const holding = { id: 20233 };
      jest.spyOn(holdingFormService, 'getHolding').mockReturnValue({ id: null });
      jest.spyOn(holdingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ holding: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: holding }));
      saveSubject.complete();

      // THEN
      expect(holdingFormService.getHolding).toHaveBeenCalled();
      expect(holdingService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHolding>>();
      const holding = { id: 20233 };
      jest.spyOn(holdingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ holding });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(holdingService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePortfolio', () => {
      it('Should forward to portfolioService', () => {
        const entity = { id: 17791 };
        const entity2 = { id: 12057 };
        jest.spyOn(portfolioService, 'comparePortfolio');
        comp.comparePortfolio(entity, entity2);
        expect(portfolioService.comparePortfolio).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
