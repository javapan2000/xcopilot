import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserExtra, NewUserExtra } from '../user-extra.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserExtra for edit and NewUserExtraFormGroupInput for create.
 */
type UserExtraFormGroupInput = IUserExtra | PartialWithRequiredKeyOf<NewUserExtra>;

type UserExtraFormDefaults = Pick<NewUserExtra, 'id'>;

type UserExtraFormGroupContent = {
  id: FormControl<IUserExtra['id'] | NewUserExtra['id']>;
  fullName: FormControl<IUserExtra['fullName']>;
  phoneNumber: FormControl<IUserExtra['phoneNumber']>;
  user: FormControl<IUserExtra['user']>;
};

export type UserExtraFormGroup = FormGroup<UserExtraFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserExtraFormService {
  createUserExtraFormGroup(userExtra: UserExtraFormGroupInput = { id: null }): UserExtraFormGroup {
    const userExtraRawValue = {
      ...this.getFormDefaults(),
      ...userExtra,
    };
    return new FormGroup<UserExtraFormGroupContent>({
      id: new FormControl(
        { value: userExtraRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fullName: new FormControl(userExtraRawValue.fullName, {
        validators: [Validators.required],
      }),
      phoneNumber: new FormControl(userExtraRawValue.phoneNumber),
      user: new FormControl(userExtraRawValue.user),
    });
  }

  getUserExtra(form: UserExtraFormGroup): IUserExtra | NewUserExtra {
    return form.getRawValue() as IUserExtra | NewUserExtra;
  }

  resetForm(form: UserExtraFormGroup, userExtra: UserExtraFormGroupInput): void {
    const userExtraRawValue = { ...this.getFormDefaults(), ...userExtra };
    form.reset(
      {
        ...userExtraRawValue,
        id: { value: userExtraRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserExtraFormDefaults {
    return {
      id: null,
    };
  }
}
