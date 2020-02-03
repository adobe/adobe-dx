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

export default class ConfigManager extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dataSource : new ConfigDataSource(props.dataSourcePath),
            fixedActionBar: { show: false },
            selectedItem: { path: '/conf' }
        };
        this.closeDialog = this.closeDialog.bind(this);
    }

    toggleActionBar = () => {
        const fixedActionBar = this.state.fixedActionBar;
        fixedActionBar.show = fixedActionBar.show ? false : true;
        // Reset button states
        if (!fixedActionBar.show) {
            fixedActionBar.primary = '';
            fixedActionBar.secondary = '';
        }
        this.setState({ fixedActionBar });
    }

    selectionChange = (items) => {
        const fixedActionBar = this.state.fixedActionBar;
        fixedActionBar.show = items.length === 0 ? false : true;
        this.setState({ fixedActionBar });
    }

    navigate = (items) => {
        if (items.length > 0) {
            this.setState({ selectedItems: items });
            this.setState({ selectedItem: items[items.length - 1] });
        }
    }

    openDialog = (type, configKey) => {
        this.setState({ dialogType: type });
        if (configKey) {
            this.setState({ configKey });
        }
    }

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

    setData = () => {
        this.setState({ dataSource : new ConfigDataSource(this.props.dataSourcePath) });
    }

    render() {
        return (<>
            <ActionMenu
                fixedActionBar={this.state.fixedActionBar}
                openDialog={this.openDialog}
                toggleActionBar={this.toggleActionBar} />
            <CreateMenu
                onSelect={this.openDialog} />
            <Provider theme="light" className="dx-Provider--ColumnView">
                <ColumnView
                    renderItem={columnItem}
                    dataSource={this.state.dataSource}
                    navigatedPath={this.state.selectedItems}
                    onSelectionChange={this.selectionChange}
                    onNavigate={this.navigate}
                    allowsSelection />
            </Provider>
            <Dialog
                open={this.state.dialogType !== undefined}
                dialogType={this.state.dialogType}
                onDialogClose={this.closeDialog}
                configKey={this.state.configKey}
                item={this.state.selectedItem} />
        </>);
    }
}