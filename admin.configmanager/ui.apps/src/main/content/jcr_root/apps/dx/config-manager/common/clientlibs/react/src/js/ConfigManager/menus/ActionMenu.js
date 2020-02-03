import React from 'react';

import Provider from '@react/react-spectrum/Provider';
import ButtonGroup from '@react/react-spectrum/ButtonGroup';
import Button from '@react/react-spectrum/Button';

// Spectrum Icons
import Edit from '@react/react-spectrum/Icon/Edit';
import Close from '@react/react-spectrum/Icon/Close';
import Delete from '@react/react-spectrum/Icon/Delete';

const actionMenu = (props) => {
    let actionBarStyle = 'dx-ActionBar dx-ActionBar--fixed';
    if (props.fixedActionBar.show) {
        actionBarStyle += ' is-Active';
    }

    return (
        <div className={actionBarStyle}>
            <Provider theme="lightest" className="dx-ActionBar-Provider">
                <ButtonGroup
                    value={props.fixedActionBar.primary}
                    aria-label="PrimaryButtons"
                    onChange={(value) => props.openDialog(value, undefined)}>
                    <Button label="Edit" value="edit" icon={<Edit />} />
                    <Button label="Delete" value="delete" icon={<Delete />} />
                </ButtonGroup>
                <ButtonGroup
                    value={props.fixedActionBar.secondary}
                    aria-label="SecondaryButtons"
                    onChange={props.toggleActionBar}>
                    <Button label="Close" value="close" icon={<Close />}
                            className="spectrum-ActionButton--alignLeft" />
                </ButtonGroup>
            </Provider>
        </div>
    );
}

export default actionMenu;