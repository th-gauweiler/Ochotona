import { IStorage } from 'app/shared/model/storage.model';
import { IProducts } from 'app/shared/model/products.model';

export interface IStorageRoom {
  id?: number;
  name?: string | null;
  inherit?: IStorage;
  contains?: IStorage[] | null;
  products?: IProducts | null;
}

export const defaultValue: Readonly<IStorageRoom> = {};
