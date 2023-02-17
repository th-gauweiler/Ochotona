import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Storage from './storage';
import StorageDetail from './storage-detail';
import StorageUpdate from './storage-update';
import StorageDeleteDialog from './storage-delete-dialog';

const StorageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Storage />} />
    <Route path="new" element={<StorageUpdate />} />
    <Route path=":id">
      <Route index element={<StorageDetail />} />
      <Route path="edit" element={<StorageUpdate />} />
      <Route path="delete" element={<StorageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StorageRoutes;
