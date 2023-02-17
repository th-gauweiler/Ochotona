import { IStorageRoom } from 'app/shared/model/storage-room.model';

export interface IStorage {
  id?: number;
  key?: string;
  storageRoom?: IStorageRoom | null;
}

export const defaultValue: Readonly<IStorage> = {};
