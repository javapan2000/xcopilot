import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPortfolio } from '../portfolio.model';
import { PortfolioService } from '../service/portfolio.service';

@Component({
  templateUrl: './portfolio-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PortfolioDeleteDialogComponent {
  portfolio?: IPortfolio;

  protected portfolioService = inject(PortfolioService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.portfolioService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
