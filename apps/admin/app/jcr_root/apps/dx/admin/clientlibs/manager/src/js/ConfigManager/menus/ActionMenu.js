/*
 *  Copyright 2020 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import React from 'react';
import PropTypes from 'prop-types';

import Provider from '@react/react-spectrum/Provider';
import ButtonGroup from '@react/react-spectrum/ButtonGroup';
import Button from '@react/react-spectrum/Button';

// Spectrum Icons
import Edit from '@react/react-spectrum/Icon/Edit';
import Close from '@react/react-spectrum/Icon/Close';
import Delete from '@react/react-spectrum/Icon/Delete';

const ActionMenu = ({ fixedActionBar, openDialog, toggleActionBar }) => {
    let actionBarStyle = 'dx-ActionBar dx-ActionBar--fixed';
    if (fixedActionBar.show) {
        actionBarStyle += ' is-Active';
    }

    return (
        <div className={actionBarStyle}>
            <Provider theme="lightest" className="dx-ActionBar-Provider">
                <ButtonGroup
                    value={fixedActionBar.primary}
                    aria-label="PrimaryButtons"
                    onChange={(value) => openDialog(value, undefined)}
                >
                    <Button aria-label="Edit" label="Edit" value="edit" icon={<Edit />} />
                    <Button aria-label="Delete" label="Delete" value="delete" icon={<Delete />} />
                </ButtonGroup>
                <ButtonGroup
                    value={fixedActionBar.secondary}
                    aria-label="SecondaryButtons"
                    onChange={toggleActionBar}
                >
                    <Button
                        aria-label="Close"
                        label="Close"
                        value="close"
                        icon={<Close />}
                        className="spectrum-ActionButton--alignLeft"
                    />
                </ButtonGroup>
            </Provider>
        </div>
    );
};

ActionMenu.propTypes = {
    fixedActionBar: PropTypes.shape({
        show: PropTypes.bool.isRequired,
        primary: PropTypes.string,
        secondary: PropTypes.string,
    }),
    openDialog: PropTypes.func,
    toggleActionBar: PropTypes.func,
};

// Ignore for code coverage
/* istanbul ignore next */
ActionMenu.defaultProps = {
    fixedActionBar: {
        show: false,
        primary: '',
        secondary: '',
    },
    openDialog: () => {},
    toggleActionBar: () => {},
};

export default ActionMenu;
