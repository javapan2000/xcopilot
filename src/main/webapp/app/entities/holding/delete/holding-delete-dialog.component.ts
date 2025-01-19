import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IHolding } from '../holding.model';
import { HoldingService } from '../service/holding.service';

@Component({
  templateUrl: './holding-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class HoldingDeleteDialogComponent {
  holding?: IHolding;

  protected holdingService = inject(HoldingService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.holdingService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
