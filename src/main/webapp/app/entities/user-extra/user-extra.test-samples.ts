import { IUserExtra, NewUserExtra } from './user-extra.model';

export const sampleWithRequiredData: IUserExtra = {
  id: 10181,
  fullName: 'even clearly a',
};

export const sampleWithPartialData: IUserExtra = {
  id: 12922,
  fullName: 'without besides likewise',
  phoneNumber: 'best-seller sans',
};

export const sampleWithFullData: IUserExtra = {
  id: 8391,
  fullName: 'outfox',
  phoneNumber: 'along blight boohoo',
};

export const sampleWithNewData: NewUserExtra = {
  fullName: 'underneath before psst',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
