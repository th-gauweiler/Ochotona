import { IStorageRoom } from 'app/shared/model/storage-room.model';

export interface IProducts {
  id?: number;
  name?: string;
  url?: string | null;
  ean?: string | null;
  tags?: string | null;
  storeds?: IStorageRoom[] | null;
}

export const defaultValue: Readonly<IProducts> = {};
