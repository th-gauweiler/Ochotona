import products from 'app/entities/products/products.reducer';
import stockPosition from 'app/entities/stock-position/stock-position.reducer';
import storage from 'app/entities/storage/storage.reducer';
import storageRoom from 'app/entities/storage-room/storage-room.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  products,
  stockPosition,
  storage,
  storageRoom,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
