import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './stock-position.reducer';

export const StockPositionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const stockPositionEntity = useAppSelector(state => state.stockPosition.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockPositionDetailsHeading">
          <Translate contentKey="ochotonaApp.stockPosition.detail.title">StockPosition</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockPositionEntity.id}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="ochotonaApp.stockPosition.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{stockPositionEntity.amount}</dd>
          <dt>
            <span id="serialNo">
              <Translate contentKey="ochotonaApp.stockPosition.serialNo">Serial No</Translate>
            </span>
          </dt>
          <dd>{stockPositionEntity.serialNo}</dd>
          <dt>
            <Translate contentKey="ochotonaApp.stockPosition.inherit">Inherit</Translate>
          </dt>
          <dd>{stockPositionEntity.inherit ? stockPositionEntity.inherit.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/stock-position" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock-position/${stockPositionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockPositionDetail;
