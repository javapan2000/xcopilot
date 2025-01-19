import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserExtraDetailComponent } from './user-extra-detail.component';

describe('UserExtra Management Detail Component', () => {
  let comp: UserExtraDetailComponent;
  let fixture: ComponentFixture<UserExtraDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserExtraDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./user-extra-detail.component').then(m => m.UserExtraDetailComponent),
              resolve: { userExtra: () => of({ id: 9146 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(UserExtraDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserExtraDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userExtra on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', UserExtraDetailComponent);

      // THEN
      expect(instance.userExtra()).toEqual(expect.objectContaining({ id: 9146 }));
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
