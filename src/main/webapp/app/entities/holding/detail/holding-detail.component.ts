import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IHolding } from '../holding.model';

@Component({
  selector: 'jhi-holding-detail',
  templateUrl: './holding-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class HoldingDetailComponent {
  holding = input<IHolding | null>(null);

  previousState(): void {
    window.history.back();
  }
}
