import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../holding.test-samples';

import { HoldingFormService } from './holding-form.service';

describe('Holding Form Service', () => {
  let service: HoldingFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HoldingFormService);
  });

  describe('Service methods', () => {
    describe('createHoldingFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHoldingFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            symbol: expect.any(Object),
            quantity: expect.any(Object),
            averageCost: expect.any(Object),
            currentPrice: expect.any(Object),
            portfolio: expect.any(Object),
          }),
        );
      });

      it('passing IHolding should create a new form with FormGroup', () => {
        const formGroup = service.createHoldingFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            symbol: expect.any(Object),
            quantity: expect.any(Object),
            averageCost: expect.any(Object),
            currentPrice: expect.any(Object),
            portfolio: expect.any(Object),
          }),
        );
      });
    });

    describe('getHolding', () => {
      it('should return NewHolding for default Holding initial value', () => {
        const formGroup = service.createHoldingFormGroup(sampleWithNewData);

        const holding = service.getHolding(formGroup) as any;

        expect(holding).toMatchObject(sampleWithNewData);
      });

      it('should return NewHolding for empty Holding initial value', () => {
        const formGroup = service.createHoldingFormGroup();

        const holding = service.getHolding(formGroup) as any;

        expect(holding).toMatchObject({});
      });

      it('should return IHolding', () => {
        const formGroup = service.createHoldingFormGroup(sampleWithRequiredData);

        const holding = service.getHolding(formGroup) as any;

        expect(holding).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHolding should not enable id FormControl', () => {
        const formGroup = service.createHoldingFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHolding should disable id FormControl', () => {
        const formGroup = service.createHoldingFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
