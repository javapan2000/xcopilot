import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUserExtra } from '../user-extra.model';
import { UserExtraService } from '../service/user-extra.service';
import { UserExtraFormGroup, UserExtraFormService } from './user-extra-form.service';

@Component({
  selector: 'jhi-user-extra-update',
  templateUrl: './user-extra-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UserExtraUpdateComponent implements OnInit {
  isSaving = false;
  userExtra: IUserExtra | null = null;

  usersSharedCollection: IUser[] = [];

  protected userExtraService = inject(UserExtraService);
  protected userExtraFormService = inject(UserExtraFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserExtraFormGroup = this.userExtraFormService.createUserExtraFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userExtra }) => {
      this.userExtra = userExtra;
      if (userExtra) {
        this.updateForm(userExtra);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userExtra = this.userExtraFormService.getUserExtra(this.editForm);
    if (userExtra.id !== null) {
      this.subscribeToSaveResponse(this.userExtraService.update(userExtra));
    } else {
      this.subscribeToSaveResponse(this.userExtraService.create(userExtra));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserExtra>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(userExtra: IUserExtra): void {
    this.userExtra = userExtra;
    this.userExtraFormService.resetForm(this.editForm, userExtra);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userExtra.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userExtra?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
