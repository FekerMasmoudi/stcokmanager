import { IArticle, NewArticle } from './article.model';

export const sampleWithRequiredData: IArticle = {
  id: '98a873ee-1442-4553-9412-ea586fba6b1e',
  name: 'channels Sports Poland',
};

export const sampleWithPartialData: IArticle = {
  id: 'a2425bfb-c490-4c95-a7b6-18f61b7505b5',
  name: 'blue',
  price: 98678,
  quantity: 27942,
  idcategory: 'Officer',
};

export const sampleWithFullData: IArticle = {
  id: 'b54a42df-0bde-451c-8885-3c41caf49d52',
  name: 'deposit',
  price: 40254,
  quantity: 17845,
  idcategory: 'deposit Multi-layered e-markets',
};

export const sampleWithNewData: NewArticle = {
  name: 'calculate Square',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
