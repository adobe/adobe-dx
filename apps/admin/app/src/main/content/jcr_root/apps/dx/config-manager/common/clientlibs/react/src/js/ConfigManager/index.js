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

// Spectrum Components
import Provider from '@react/react-spectrum/Provider';
import { ColumnView } from '@react/react-spectrum/ColumnView';

// Custom Components
import ConfigDataSource from './data/DataSource';
import columnItem from './data/columnItem';
import CreateMenu from './menus/CreateMenu';
import ActionMenu from './menus/ActionMenu';
import Dialog from './dialogs/Dialog';

class ConfigManager extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dataSource: new ConfigDataSource(props.dataSourcePath),
            fixedActionBar: { show: false },
            selectedItem: { path: '/conf' },
        };
        this.closeDialog = this.closeDialog.bind(this);
    }

    toggleActionBar = () => {
        const { fixedActionBar } = this.state;
        fixedActionBar.show = !fixedActionBar.show;
        // Reset button states
        if (!fixedActionBar.show) {
            fixedActionBar.primary = '';
            fixedActionBar.secondary = '';
        }
        this.setState({ fixedActionBar });
    };

    selectionChange = (items) => {
        const { fixedActionBar } = this.state;
        fixedActionBar.show = items.length !== 0;
        this.setState({ fixedActionBar });
    };

    navigate = (items) => {
        if (items.length > 0) {
            this.setState({ selectedItems: items });
            this.setState({ selectedItem: items[items.length - 1] });
        }
    };

    openDialog = (type, configKey) => {
        this.setState({ dialogType: type });
        if (configKey) {
            this.setState({ configKey });
        }
    };

    setData = () => {
        this.setState({ dataSource: new ConfigDataSource(this.props.dataSourcePath) });
    };

    async closeDialog(resetData, closeActionBar) {
        if (resetData) {
            this.setData();
        }
        if (closeActionBar) {
            this.toggleActionBar();
        }
        this.setState({ configKey: undefined });
        this.setState({ dialogType: undefined });
    }

    render() {
        return (
            <>
                <ActionMenu
                    fixedActionBar={this.state.fixedActionBar}
                    openDialog={this.openDialog}
                    toggleActionBar={this.toggleActionBar}
                />
                <CreateMenu onSelect={this.openDialog} />
                <Provider theme="light" className="dx-Provider--ColumnView">
                    <ColumnView
                        renderItem={columnItem}
                        dataSource={this.state.dataSource}
                        navigatedPath={this.state.selectedItems}
                        onSelectionChange={this.selectionChange}
                        onNavigate={this.navigate}
                        allowsSelection
                    />
                </Provider>
                <Dialog
                    open={this.state.dialogType !== undefined}
                    dialogType={this.state.dialogType}
                    onDialogClose={this.closeDialog}
                    configKey={this.state.configKey}
                    item={this.state.selectedItem}
                />
            </>
        );
    }
}

export default ConfigManager;
