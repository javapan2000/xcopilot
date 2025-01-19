import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IPortfolio } from '../portfolio.model';

@Component({
  selector: 'jhi-portfolio-detail',
  templateUrl: './portfolio-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class PortfolioDetailComponent {
  portfolio = input<IPortfolio | null>(null);

  previousState(): void {
    window.history.back();
  }
}
