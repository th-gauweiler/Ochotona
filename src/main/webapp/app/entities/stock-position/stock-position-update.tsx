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
import { IStockPosition } from 'app/shared/model/stock-position.model';
import { getEntity, updateEntity, createEntity, reset } from './stock-position.reducer';

export const StockPositionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const storages = useAppSelector(state => state.storage.entities);
  const stockPositionEntity = useAppSelector(state => state.stockPosition.entity);
  const loading = useAppSelector(state => state.stockPosition.loading);
  const updating = useAppSelector(state => state.stockPosition.updating);
  const updateSuccess = useAppSelector(state => state.stockPosition.updateSuccess);

  const handleClose = () => {
    navigate('/stock-position');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStorages({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...stockPositionEntity,
      ...values,
      inherit: storages.find(it => it.id.toString() === values.inherit.toString()),
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
          ...stockPositionEntity,
          inherit: stockPositionEntity?.inherit?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="ochotonaApp.stockPosition.home.createOrEditLabel" data-cy="StockPositionCreateUpdateHeading">
            <Translate contentKey="ochotonaApp.stockPosition.home.createOrEditLabel">Create or edit a StockPosition</Translate>
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
                  id="stock-position-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('ochotonaApp.stockPosition.amount')}
                id="stock-position-amount"
                name="amount"
                data-cy="amount"
                type="text"
              />
              <ValidatedField
                label={translate('ochotonaApp.stockPosition.serialNo')}
                id="stock-position-serialNo"
                name="serialNo"
                data-cy="serialNo"
                type="text"
              />
              <ValidatedField
                id="stock-position-inherit"
                name="inherit"
                data-cy="inherit"
                label={translate('ochotonaApp.stockPosition.inherit')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stock-position" replace color="info">
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

export default StockPositionUpdate;
