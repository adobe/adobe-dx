import React from 'react';

import { Menu, MenuItem } from '@react/react-spectrum/Menu';
import Dropdown from '@react/react-spectrum/Dropdown';
import Provider from '@react/react-spectrum/Provider';
import Button from '@react/react-spectrum/Button';

export default class CreateMenu extends React.Component {
    constructor(props) {
        super(props);
        this.buildMenu();
    }

    buildMenu = () => {
        this.menuItems = [];
        this.menuItems.push(<MenuItem value="folder" key="folder">Folder</MenuItem>);
        this.thirdParty = window.dx.configManager.configs;
        Object.keys(this.thirdParty).forEach((configKey) => {
            const config = this.thirdParty[configKey];
            if (config.label && config.app) {
                this.menuItems.push(<MenuItem value={configKey} key={configKey}>{config.label}</MenuItem>);
            }
        });
    }

    create = (key) => {
        if (key === 'folder') {
            this.props.onSelect('folder');
        } else {
            this.props.onSelect('config', key);
        }
    }

    render() {
        return (
            <div className="dx-ActionBar dx-ActionBar--secondary">
                <Provider theme="light" className="dx-ActionBar-Provider dx-ActionBar-Provider--alignEnd">
                    <Dropdown onSelect={this.create}>
                        <Button label="Create" variant="cta" />
                        <Menu>{this.menuItems}</Menu>
                    </Dropdown>
                </Provider>
            </div>
        );
    }
}