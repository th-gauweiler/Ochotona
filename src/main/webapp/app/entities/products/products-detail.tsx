import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './products.reducer';

export const ProductsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const productsEntity = useAppSelector(state => state.products.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productsDetailsHeading">
          <Translate contentKey="ochotonaApp.products.detail.title">Products</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productsEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="ochotonaApp.products.name">Name</Translate>
            </span>
          </dt>
          <dd>{productsEntity.name}</dd>
          <dt>
            <span id="url">
              <Translate contentKey="ochotonaApp.products.url">Url</Translate>
            </span>
          </dt>
          <dd>{productsEntity.url}</dd>
          <dt>
            <span id="ean">
              <Translate contentKey="ochotonaApp.products.ean">Ean</Translate>
            </span>
          </dt>
          <dd>{productsEntity.ean}</dd>
          <dt>
            <span id="tags">
              <Translate contentKey="ochotonaApp.products.tags">Tags</Translate>
            </span>
          </dt>
          <dd>{productsEntity.tags}</dd>
        </dl>
        <Button tag={Link} to="/products" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/products/${productsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductsDetail;
