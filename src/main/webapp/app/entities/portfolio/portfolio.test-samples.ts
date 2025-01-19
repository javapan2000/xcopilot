import dayjs from 'dayjs/esm';

import { IPortfolio, NewPortfolio } from './portfolio.model';

export const sampleWithRequiredData: IPortfolio = {
  id: 26418,
  name: 'engage yak tankful',
};

export const sampleWithPartialData: IPortfolio = {
  id: 19035,
  name: 'duh till',
};

export const sampleWithFullData: IPortfolio = {
  id: 23407,
  name: 'accidentally coolly t-shirt',
  description: 'unnecessarily woot stiff',
  totalValue: 23388.96,
  lastUpdated: dayjs('2025-01-18T17:10'),
};

export const sampleWithNewData: NewPortfolio = {
  name: 'huzzah yum',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
