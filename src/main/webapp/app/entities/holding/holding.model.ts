import { IPortfolio } from 'app/entities/portfolio/portfolio.model';

export interface IHolding {
  id: number;
  symbol?: string | null;
  quantity?: number | null;
  averageCost?: number | null;
  currentPrice?: number | null;
  portfolio?: IPortfolio | null;
}

export type NewHolding = Omit<IHolding, 'id'> & { id: null };
