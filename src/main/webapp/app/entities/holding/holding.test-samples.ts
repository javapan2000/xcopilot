import { IHolding, NewHolding } from './holding.model';

export const sampleWithRequiredData: IHolding = {
  id: 16518,
  symbol: 'above',
  quantity: 11647.44,
  averageCost: 13408.89,
};

export const sampleWithPartialData: IHolding = {
  id: 22049,
  symbol: 'poetry eek aha',
  quantity: 11225.04,
  averageCost: 9627.1,
};

export const sampleWithFullData: IHolding = {
  id: 10369,
  symbol: 'prance apropos which',
  quantity: 22038.17,
  averageCost: 26024.83,
  currentPrice: 1289.13,
};

export const sampleWithNewData: NewHolding = {
  symbol: 'gee once',
  quantity: 7442.58,
  averageCost: 12544.62,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
