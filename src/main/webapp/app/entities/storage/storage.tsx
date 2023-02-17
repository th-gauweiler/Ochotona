import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IStorage } from 'app/shared/model/storage.model';
import { getEntities } from './storage.reducer';

export const Storage = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const storageList = useAppSelector(state => state.storage.entities);
  const loading = useAppSelector(state => state.storage.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="storage-heading" data-cy="StorageHeading">
        <Translate contentKey="ochotonaApp.storage.home.title">Storages</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="ochotonaApp.storage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/storage/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="ochotonaApp.storage.home.createLabel">Create new Storage</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {storageList && storageList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="ochotonaApp.storage.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="ochotonaApp.storage.key">Key</Translate>
                </th>
                <th>
                  <Translate contentKey="ochotonaApp.storage.storageRoom">Storage Room</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {storageList.map((storage, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/storage/${storage.id}`} color="link" size="sm">
                      {storage.id}
                    </Button>
                  </td>
                  <td>{storage.key}</td>
                  <td>{storage.storageRoom ? <Link to={`/storage-room/${storage.storageRoom.id}`}>{storage.storageRoom.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/storage/${storage.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/storage/${storage.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/storage/${storage.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="ochotonaApp.storage.home.notFound">No Storages found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Storage;
