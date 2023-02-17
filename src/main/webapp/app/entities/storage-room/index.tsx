import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StorageRoom from './storage-room';
import StorageRoomDetail from './storage-room-detail';
import StorageRoomUpdate from './storage-room-update';
import StorageRoomDeleteDialog from './storage-room-delete-dialog';

const StorageRoomRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StorageRoom />} />
    <Route path="new" element={<StorageRoomUpdate />} />
    <Route path=":id">
      <Route index element={<StorageRoomDetail />} />
      <Route path="edit" element={<StorageRoomUpdate />} />
      <Route path="delete" element={<StorageRoomDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StorageRoomRoutes;
