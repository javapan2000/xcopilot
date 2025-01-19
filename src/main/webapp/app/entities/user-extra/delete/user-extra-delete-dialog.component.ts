import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IUserExtra } from '../user-extra.model';
import { UserExtraService } from '../service/user-extra.service';

@Component({
  templateUrl: './user-extra-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UserExtraDeleteDialogComponent {
  userExtra?: IUserExtra;

  protected userExtraService = inject(UserExtraService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userExtraService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
