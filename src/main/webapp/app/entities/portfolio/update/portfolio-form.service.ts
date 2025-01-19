import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPortfolio, NewPortfolio } from '../portfolio.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPortfolio for edit and NewPortfolioFormGroupInput for create.
 */
type PortfolioFormGroupInput = IPortfolio | PartialWithRequiredKeyOf<NewPortfolio>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPortfolio | NewPortfolio> = Omit<T, 'lastUpdated'> & {
  lastUpdated?: string | null;
};

type PortfolioFormRawValue = FormValueOf<IPortfolio>;

type NewPortfolioFormRawValue = FormValueOf<NewPortfolio>;

type PortfolioFormDefaults = Pick<NewPortfolio, 'id' | 'lastUpdated'>;

type PortfolioFormGroupContent = {
  id: FormControl<PortfolioFormRawValue['id'] | NewPortfolio['id']>;
  name: FormControl<PortfolioFormRawValue['name']>;
  description: FormControl<PortfolioFormRawValue['description']>;
  totalValue: FormControl<PortfolioFormRawValue['totalValue']>;
  lastUpdated: FormControl<PortfolioFormRawValue['lastUpdated']>;
  owner: FormControl<PortfolioFormRawValue['owner']>;
};

export type PortfolioFormGroup = FormGroup<PortfolioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PortfolioFormService {
  createPortfolioFormGroup(portfolio: PortfolioFormGroupInput = { id: null }): PortfolioFormGroup {
    const portfolioRawValue = this.convertPortfolioToPortfolioRawValue({
      ...this.getFormDefaults(),
      ...portfolio,
    });
    return new FormGroup<PortfolioFormGroupContent>({
      id: new FormControl(
        { value: portfolioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(portfolioRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(portfolioRawValue.description),
      totalValue: new FormControl(portfolioRawValue.totalValue),
      lastUpdated: new FormControl(portfolioRawValue.lastUpdated),
      owner: new FormControl(portfolioRawValue.owner),
    });
  }

  getPortfolio(form: PortfolioFormGroup): IPortfolio | NewPortfolio {
    return this.convertPortfolioRawValueToPortfolio(form.getRawValue() as PortfolioFormRawValue | NewPortfolioFormRawValue);
  }

  resetForm(form: PortfolioFormGroup, portfolio: PortfolioFormGroupInput): void {
    const portfolioRawValue = this.convertPortfolioToPortfolioRawValue({ ...this.getFormDefaults(), ...portfolio });
    form.reset(
      {
        ...portfolioRawValue,
        id: { value: portfolioRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PortfolioFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastUpdated: currentTime,
    };
  }

  private convertPortfolioRawValueToPortfolio(rawPortfolio: PortfolioFormRawValue | NewPortfolioFormRawValue): IPortfolio | NewPortfolio {
    return {
      ...rawPortfolio,
      lastUpdated: dayjs(rawPortfolio.lastUpdated, DATE_TIME_FORMAT),
    };
  }

  private convertPortfolioToPortfolioRawValue(
    portfolio: IPortfolio | (Partial<NewPortfolio> & PortfolioFormDefaults),
  ): PortfolioFormRawValue | PartialWithRequiredKeyOf<NewPortfolioFormRawValue> {
    return {
      ...portfolio,
      lastUpdated: portfolio.lastUpdated ? portfolio.lastUpdated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
