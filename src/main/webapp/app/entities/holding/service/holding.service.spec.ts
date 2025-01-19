import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IHolding } from '../holding.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../holding.test-samples';

import { HoldingService } from './holding.service';

const requireRestSample: IHolding = {
  ...sampleWithRequiredData,
};

describe('Holding Service', () => {
  let service: HoldingService;
  let httpMock: HttpTestingController;
  let expectedResult: IHolding | IHolding[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(HoldingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Holding', () => {
      const holding = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(holding).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Holding', () => {
      const holding = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(holding).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Holding', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Holding', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Holding', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addHoldingToCollectionIfMissing', () => {
      it('should add a Holding to an empty array', () => {
        const holding: IHolding = sampleWithRequiredData;
        expectedResult = service.addHoldingToCollectionIfMissing([], holding);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(holding);
      });

      it('should not add a Holding to an array that contains it', () => {
        const holding: IHolding = sampleWithRequiredData;
        const holdingCollection: IHolding[] = [
          {
            ...holding,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHoldingToCollectionIfMissing(holdingCollection, holding);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Holding to an array that doesn't contain it", () => {
        const holding: IHolding = sampleWithRequiredData;
        const holdingCollection: IHolding[] = [sampleWithPartialData];
        expectedResult = service.addHoldingToCollectionIfMissing(holdingCollection, holding);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(holding);
      });

      it('should add only unique Holding to an array', () => {
        const holdingArray: IHolding[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const holdingCollection: IHolding[] = [sampleWithRequiredData];
        expectedResult = service.addHoldingToCollectionIfMissing(holdingCollection, ...holdingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const holding: IHolding = sampleWithRequiredData;
        const holding2: IHolding = sampleWithPartialData;
        expectedResult = service.addHoldingToCollectionIfMissing([], holding, holding2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(holding);
        expect(expectedResult).toContain(holding2);
      });

      it('should accept null and undefined values', () => {
        const holding: IHolding = sampleWithRequiredData;
        expectedResult = service.addHoldingToCollectionIfMissing([], null, holding, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(holding);
      });

      it('should return initial array if no Holding is added', () => {
        const holdingCollection: IHolding[] = [sampleWithRequiredData];
        expectedResult = service.addHoldingToCollectionIfMissing(holdingCollection, undefined, null);
        expect(expectedResult).toEqual(holdingCollection);
      });
    });

    describe('compareHolding', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHolding(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 20233 };
        const entity2 = null;

        const compareResult1 = service.compareHolding(entity1, entity2);
        const compareResult2 = service.compareHolding(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 20233 };
        const entity2 = { id: 9274 };

        const compareResult1 = service.compareHolding(entity1, entity2);
        const compareResult2 = service.compareHolding(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 20233 };
        const entity2 = { id: 20233 };

        const compareResult1 = service.compareHolding(entity1, entity2);
        const compareResult2 = service.compareHolding(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
