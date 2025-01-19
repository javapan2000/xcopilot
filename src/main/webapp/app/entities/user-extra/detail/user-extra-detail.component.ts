import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IUserExtra } from '../user-extra.model';

@Component({
  selector: 'jhi-user-extra-detail',
  templateUrl: './user-extra-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class UserExtraDetailComponent {
  userExtra = input<IUserExtra | null>(null);

  previousState(): void {
    window.history.back();
  }
}
