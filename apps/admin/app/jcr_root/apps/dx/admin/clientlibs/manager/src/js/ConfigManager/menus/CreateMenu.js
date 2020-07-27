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

import { Menu, MenuItem } from '@react/react-spectrum/Menu';
import Dropdown from '@react/react-spectrum/Dropdown';
import Provider from '@react/react-spectrum/Provider';
import Button from '@react/react-spectrum/Button';

class CreateMenu extends React.Component {
    constructor(props) {
        super(props);
        this.buildMenu();
    }

    buildMenu = () => {
        this.menuItems = [];
        this.menuItems.push(
            <MenuItem aria-label="Folder" value="folder" key="folder">
                Folder
            </MenuItem>
        );
        this.thirdParty = window.dx.configManager.configs;
        Object.keys(this.thirdParty).forEach((configKey) => {
            const config = this.thirdParty[configKey];
            if (config.label && config.app) {
                this.menuItems.push(
                    <MenuItem aria-label={config.label} value={configKey} key={configKey}>
                        {config.label}
                    </MenuItem>
                );
            }
        });
    };

    create = (key) => {
        if (key === 'folder') {
            this.props.onSelect('folder');
        } else {
            this.props.onSelect('config', key);
        }
    };

    render() {
        return (
            <div className="dx-ActionBar dx-ActionBar--secondary">
                <Provider
                    theme="light"
                    className="dx-ActionBar-Provider dx-ActionBar-Provider--alignEnd"
                >
                    <Dropdown onSelect={this.create}>
                        <Button aria-label="Create" label="Create" variant="cta" />
                        <Menu>{this.menuItems}</Menu>
                    </Dropdown>
                </Provider>
            </div>
        );
    }
}

CreateMenu.propTypes = {
    onSelect: PropTypes.func,
};

// Ignore for code coverage
/* istanbul ignore next */
CreateMenu.defaultProps = {
    onSelect: () => {},
};

export default CreateMenu;
