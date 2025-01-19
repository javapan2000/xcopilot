import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { UserExtraService } from 'app/entities/user-extra/service/user-extra.service';
import { PortfolioService } from '../service/portfolio.service';
import { IPortfolio } from '../portfolio.model';
import { PortfolioFormService } from './portfolio-form.service';

import { PortfolioUpdateComponent } from './portfolio-update.component';

describe('Portfolio Management Update Component', () => {
  let comp: PortfolioUpdateComponent;
  let fixture: ComponentFixture<PortfolioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let portfolioFormService: PortfolioFormService;
  let portfolioService: PortfolioService;
  let userExtraService: UserExtraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PortfolioUpdateComponent],
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
      .overrideTemplate(PortfolioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PortfolioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    portfolioFormService = TestBed.inject(PortfolioFormService);
    portfolioService = TestBed.inject(PortfolioService);
    userExtraService = TestBed.inject(UserExtraService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserExtra query and add missing value', () => {
      const portfolio: IPortfolio = { id: 12057 };
      const owner: IUserExtra = { id: 9146 };
      portfolio.owner = owner;

      const userExtraCollection: IUserExtra[] = [{ id: 9146 }];
      jest.spyOn(userExtraService, 'query').mockReturnValue(of(new HttpResponse({ body: userExtraCollection })));
      const additionalUserExtras = [owner];
      const expectedCollection: IUserExtra[] = [...additionalUserExtras, ...userExtraCollection];
      jest.spyOn(userExtraService, 'addUserExtraToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ portfolio });
      comp.ngOnInit();

      expect(userExtraService.query).toHaveBeenCalled();
      expect(userExtraService.addUserExtraToCollectionIfMissing).toHaveBeenCalledWith(
        userExtraCollection,
        ...additionalUserExtras.map(expect.objectContaining),
      );
      expect(comp.userExtrasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const portfolio: IPortfolio = { id: 12057 };
      const owner: IUserExtra = { id: 9146 };
      portfolio.owner = owner;

      activatedRoute.data = of({ portfolio });
      comp.ngOnInit();

      expect(comp.userExtrasSharedCollection).toContainEqual(owner);
      expect(comp.portfolio).toEqual(portfolio);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPortfolio>>();
      const portfolio = { id: 17791 };
      jest.spyOn(portfolioFormService, 'getPortfolio').mockReturnValue(portfolio);
      jest.spyOn(portfolioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ portfolio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: portfolio }));
      saveSubject.complete();

      // THEN
      expect(portfolioFormService.getPortfolio).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(portfolioService.update).toHaveBeenCalledWith(expect.objectContaining(portfolio));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPortfolio>>();
      const portfolio = { id: 17791 };
      jest.spyOn(portfolioFormService, 'getPortfolio').mockReturnValue({ id: null });
      jest.spyOn(portfolioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ portfolio: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: portfolio }));
      saveSubject.complete();

      // THEN
      expect(portfolioFormService.getPortfolio).toHaveBeenCalled();
      expect(portfolioService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPortfolio>>();
      const portfolio = { id: 17791 };
      jest.spyOn(portfolioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ portfolio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(portfolioService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserExtra', () => {
      it('Should forward to userExtraService', () => {
        const entity = { id: 9146 };
        const entity2 = { id: 16751 };
        jest.spyOn(userExtraService, 'compareUserExtra');
        comp.compareUserExtra(entity, entity2);
        expect(userExtraService.compareUserExtra).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
