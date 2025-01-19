import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IHolding, NewHolding } from '../holding.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHolding for edit and NewHoldingFormGroupInput for create.
 */
type HoldingFormGroupInput = IHolding | PartialWithRequiredKeyOf<NewHolding>;

type HoldingFormDefaults = Pick<NewHolding, 'id'>;

type HoldingFormGroupContent = {
  id: FormControl<IHolding['id'] | NewHolding['id']>;
  symbol: FormControl<IHolding['symbol']>;
  quantity: FormControl<IHolding['quantity']>;
  averageCost: FormControl<IHolding['averageCost']>;
  currentPrice: FormControl<IHolding['currentPrice']>;
  portfolio: FormControl<IHolding['portfolio']>;
};

export type HoldingFormGroup = FormGroup<HoldingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HoldingFormService {
  createHoldingFormGroup(holding: HoldingFormGroupInput = { id: null }): HoldingFormGroup {
    const holdingRawValue = {
      ...this.getFormDefaults(),
      ...holding,
    };
    return new FormGroup<HoldingFormGroupContent>({
      id: new FormControl(
        { value: holdingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      symbol: new FormControl(holdingRawValue.symbol, {
        validators: [Validators.required],
      }),
      quantity: new FormControl(holdingRawValue.quantity, {
        validators: [Validators.required],
      }),
      averageCost: new FormControl(holdingRawValue.averageCost, {
        validators: [Validators.required],
      }),
      currentPrice: new FormControl(holdingRawValue.currentPrice),
      portfolio: new FormControl(holdingRawValue.portfolio),
    });
  }

  getHolding(form: HoldingFormGroup): IHolding | NewHolding {
    return form.getRawValue() as IHolding | NewHolding;
  }

  resetForm(form: HoldingFormGroup, holding: HoldingFormGroupInput): void {
    const holdingRawValue = { ...this.getFormDefaults(), ...holding };
    form.reset(
      {
        ...holdingRawValue,
        id: { value: holdingRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): HoldingFormDefaults {
    return {
      id: null,
    };
  }
}
