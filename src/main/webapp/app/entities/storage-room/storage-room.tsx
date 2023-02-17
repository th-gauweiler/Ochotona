import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IStorageRoom } from 'app/shared/model/storage-room.model';
import { getEntities } from './storage-room.reducer';

export const StorageRoom = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const storageRoomList = useAppSelector(state => state.storageRoom.entities);
  const loading = useAppSelector(state => state.storageRoom.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="storage-room-heading" data-cy="StorageRoomHeading">
        <Translate contentKey="ochotonaApp.storageRoom.home.title">Storage Rooms</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="ochotonaApp.storageRoom.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/storage-room/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="ochotonaApp.storageRoom.home.createLabel">Create new Storage Room</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {storageRoomList && storageRoomList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="ochotonaApp.storageRoom.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="ochotonaApp.storageRoom.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="ochotonaApp.storageRoom.inherit">Inherit</Translate>
                </th>
                <th>
                  <Translate contentKey="ochotonaApp.storageRoom.products">Products</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {storageRoomList.map((storageRoom, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/storage-room/${storageRoom.id}`} color="link" size="sm">
                      {storageRoom.id}
                    </Button>
                  </td>
                  <td>{storageRoom.name}</td>
                  <td>{storageRoom.inherit ? <Link to={`/storage/${storageRoom.inherit.id}`}>{storageRoom.inherit.id}</Link> : ''}</td>
                  <td>{storageRoom.products ? <Link to={`/products/${storageRoom.products.id}`}>{storageRoom.products.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/storage-room/${storageRoom.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/storage-room/${storageRoom.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/storage-room/${storageRoom.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="ochotonaApp.storageRoom.home.notFound">No Storage Rooms found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default StorageRoom;
