import dayjs from 'dayjs/esm';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';

export interface IPortfolio {
  id: number;
  name?: string | null;
  description?: string | null;
  totalValue?: number | null;
  lastUpdated?: dayjs.Dayjs | null;
  owner?: IUserExtra | null;
}

export type NewPortfolio = Omit<IPortfolio, 'id'> & { id: null };
