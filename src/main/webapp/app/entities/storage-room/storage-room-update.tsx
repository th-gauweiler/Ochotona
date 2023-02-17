import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IStorage } from 'app/shared/model/storage.model';
import { getEntities as getStorages } from 'app/entities/storage/storage.reducer';
import { IProducts } from 'app/shared/model/products.model';
import { getEntities as getProducts } from 'app/entities/products/products.reducer';
import { IStorageRoom } from 'app/shared/model/storage-room.model';
import { getEntity, updateEntity, createEntity, reset } from './storage-room.reducer';

export const StorageRoomUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const storages = useAppSelector(state => state.storage.entities);
  const products = useAppSelector(state => state.products.entities);
  const storageRoomEntity = useAppSelector(state => state.storageRoom.entity);
  const loading = useAppSelector(state => state.storageRoom.loading);
  const updating = useAppSelector(state => state.storageRoom.updating);
  const updateSuccess = useAppSelector(state => state.storageRoom.updateSuccess);

  const handleClose = () => {
    navigate('/storage-room');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStorages({}));
    dispatch(getProducts({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...storageRoomEntity,
      ...values,
      inherit: storages.find(it => it.id.toString() === values.inherit.toString()),
      products: products.find(it => it.id.toString() === values.products.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...storageRoomEntity,
          inherit: storageRoomEntity?.inherit?.id,
          products: storageRoomEntity?.products?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="ochotonaApp.storageRoom.home.createOrEditLabel" data-cy="StorageRoomCreateUpdateHeading">
            <Translate contentKey="ochotonaApp.storageRoom.home.createOrEditLabel">Create or edit a StorageRoom</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="storage-room-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('ochotonaApp.storageRoom.name')}
                id="storage-room-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                id="storage-room-inherit"
                name="inherit"
                data-cy="inherit"
                label={translate('ochotonaApp.storageRoom.inherit')}
                type="select"
                required
              >
                <option value="" key="0" />
                {storages
                  ? storages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="storage-room-products"
                name="products"
                data-cy="products"
                label={translate('ochotonaApp.storageRoom.products')}
                type="select"
              >
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/storage-room" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default StorageRoomUpdate;
