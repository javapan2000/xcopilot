import dayjs from 'dayjs/esm';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 3793,
  transactionType: 'REBALANCE',
  quantity: 2419.29,
  price: 23189.74,
  timestamp: dayjs('2025-01-18T22:44'),
};

export const sampleWithPartialData: ITransaction = {
  id: 21074,
  transactionType: 'BUY',
  quantity: 26845.95,
  price: 1969.88,
  timestamp: dayjs('2025-01-18T13:08'),
};

export const sampleWithFullData: ITransaction = {
  id: 20285,
  transactionType: 'BUY',
  quantity: 25161.85,
  price: 19965.99,
  timestamp: dayjs('2025-01-18T21:32'),
};

export const sampleWithNewData: NewTransaction = {
  transactionType: 'REBALANCE',
  quantity: 29048,
  price: 31394.35,
  timestamp: dayjs('2025-01-18T19:43'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
