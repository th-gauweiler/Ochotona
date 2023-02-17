import { IStorage } from 'app/shared/model/storage.model';

export interface IStockPosition {
  id?: number;
  amount?: number | null;
  serialNo?: string | null;
  inherit?: IStorage;
}

export const defaultValue: Readonly<IStockPosition> = {};
