import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './storage-room.reducer';

export const StorageRoomDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const storageRoomEntity = useAppSelector(state => state.storageRoom.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="storageRoomDetailsHeading">
          <Translate contentKey="ochotonaApp.storageRoom.detail.title">StorageRoom</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{storageRoomEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="ochotonaApp.storageRoom.name">Name</Translate>
            </span>
          </dt>
          <dd>{storageRoomEntity.name}</dd>
          <dt>
            <Translate contentKey="ochotonaApp.storageRoom.inherit">Inherit</Translate>
          </dt>
          <dd>{storageRoomEntity.inherit ? storageRoomEntity.inherit.id : ''}</dd>
          <dt>
            <Translate contentKey="ochotonaApp.storageRoom.products">Products</Translate>
          </dt>
          <dd>{storageRoomEntity.products ? storageRoomEntity.products.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/storage-room" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/storage-room/${storageRoomEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StorageRoomDetail;
