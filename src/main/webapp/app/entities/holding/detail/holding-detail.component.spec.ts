import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { HoldingDetailComponent } from './holding-detail.component';

describe('Holding Management Detail Component', () => {
  let comp: HoldingDetailComponent;
  let fixture: ComponentFixture<HoldingDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HoldingDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./holding-detail.component').then(m => m.HoldingDetailComponent),
              resolve: { holding: () => of({ id: 20233 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(HoldingDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HoldingDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load holding on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', HoldingDetailComponent);

      // THEN
      expect(instance.holding()).toEqual(expect.objectContaining({ id: 20233 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
