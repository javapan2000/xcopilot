import dayjs from 'dayjs/esm';
import { IHolding } from 'app/entities/holding/holding.model';
import { TransactionType } from 'app/entities/enumerations/transaction-type.model';

export interface ITransaction {
  id: number;
  transactionType?: keyof typeof TransactionType | null;
  quantity?: number | null;
  price?: number | null;
  timestamp?: dayjs.Dayjs | null;
  holding?: IHolding | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
