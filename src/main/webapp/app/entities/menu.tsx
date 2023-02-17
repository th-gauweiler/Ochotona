import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/products">
        <Translate contentKey="global.menu.entities.products" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/stock-position">
        <Translate contentKey="global.menu.entities.stockPosition" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/storage">
        <Translate contentKey="global.menu.entities.storage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/storage-room">
        <Translate contentKey="global.menu.entities.storageRoom" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
