import React from 'react';

import Settings from '@react/react-spectrum/Icon/Settings';
import Folder from '@react/react-spectrum/Icon/Folder';

const CLASS_NAME = "dx-Icon dx-Icon--ColumnItem";

const columnItem = (item) => {
    let iconType = <Settings className={CLASS_NAME}/>;
    if (item.iconType === 'folder') {
        iconType = <Folder className={CLASS_NAME}/>;
    }
    return (
        <React.Fragment>
            {iconType}
            <span className="dx-ColumnItemLabel">{item.label}</span>
        </React.Fragment>
    );
}

export default columnItem;