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
import Dialog from '@react/react-spectrum/Dialog';
import getCsrf from '../utils/csrf';

const SLING_TREE_SUFFIX = '.1.json';

export default class ConfigDialog extends React.Component {
    constructor(props) {
        super(props);
        // New Config - The config type has been passed in from menu
        if (props.configKey) {
            this.mode = 'new';
            this.state = {
                view: window.dx.configManager.configs[props.configKey],
                config: {},
            };
        }

        // Edit Config - Determine what kind of config to edit
        if (!props.configKey && props.item) {
            this.mode = 'edit';
            this.state = {};
            this.state = this.setupEditConfig();
        }
    }

    setupEditConfig = async () => {
        const configPath = `${this.props.item.path}${SLING_TREE_SUFFIX}`;
        const configTree = await fetch(configPath)
            .then((res) => {
                return res.json();
            })
            .catch((err) => {
                // eslint-disable-next-line no-console
                console.log('Error: ', err);
            });
        if (this.props.item.isPage) {
            const { configKey } = configTree['jcr:content'];
            if (configKey) {
                this.setState({ view: window.dx.configManager.configs[configKey] });
                const config = {
                    name: this.props.item.name,
                    data: configTree,
                };
                this.setState({ config });
            }
        }
    };

    getSaveUrl = () => {
        let url = this.props.item.path;
        if (this.mode === 'new') {
            url += '/';
        }
        return url;
    };

    dialogConfirm = async () => {
        const formData = new FormData();
        formData.append(':operation', 'import');
        formData.append(':contentType', 'json');

        if (this.mode === 'new') {
            formData.append(':name', this.state.config.name);
        }

        if (this.mode === 'edit' || this.state.config.replace) {
            formData.append(':replace', true);
        }

        formData.append(':content', JSON.stringify(this.state.config.data));

        const csrf = await getCsrf();
        const url = this.getSaveUrl();

        await fetch(url, {
            method: 'POST',
            credentials: 'same-origin',
            headers: { 'CSRF-Token': csrf.token },
            body: formData,
        });
        this.props.onDialogClose(true, true);
    };

    dialogCancel = () => {
        this.props.onDialogClose(false);
    };

    onChange = (config) => {
        this.setState({ config });
    };

    getConfig = () => {
        if (this.state.view) {
            return this.state.view.app;
        }
        return null;
    };

    empty = () => {
        return null;
    };

    getLabel = () => {
        return this.mode === 'new' ? 'Create' : 'Save';
    };

    render() {
        const Config = !this.state.config ? this.empty : this.getConfig();
        const label = this.getLabel();
        return (
            <Dialog
                open={this.props.open}
                onConfirm={this.dialogConfirm}
                onCancel={this.dialogCancel}
                title={this.state.view ? this.state.view.label : ''}
                mode="fullscreen"
                confirmLabel={label}
                cancelLabel="Cancel"
            >
                <Config mode={this.mode} setConfig={this.onChange} config={this.state.config} />
            </Dialog>
        );
    }
}
