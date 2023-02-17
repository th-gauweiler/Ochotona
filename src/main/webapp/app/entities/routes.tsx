import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Products from './products';
import StockPosition from './stock-position';
import Storage from './storage';
import StorageRoom from './storage-room';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="products/*" element={<Products />} />
        <Route path="stock-position/*" element={<StockPosition />} />
        <Route path="storage/*" element={<Storage />} />
        <Route path="storage-room/*" element={<StorageRoom />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
