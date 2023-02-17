import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StockPosition from './stock-position';
import StockPositionDetail from './stock-position-detail';
import StockPositionUpdate from './stock-position-update';
import StockPositionDeleteDialog from './stock-position-delete-dialog';

const StockPositionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StockPosition />} />
    <Route path="new" element={<StockPositionUpdate />} />
    <Route path=":id">
      <Route index element={<StockPositionDetail />} />
      <Route path="edit" element={<StockPositionUpdate />} />
      <Route path="delete" element={<StockPositionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StockPositionRoutes;
