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

import CreateFolder from './CreateFolder';

export default class CreateFolderDialog extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: '',
            title: '',
        };
        this.dialogConfirm = this.dialogConfirm.bind(this);
    }

    dialogCancel = () => {
        this.props.onDialogClose(false);
    };

    handleFolderChange = (change) => {
        this.setState(change);
    };

    async dialogConfirm() {
        const formData = new FormData();
        formData.append(':name', this.state.name);

        if (this.state.title) {
            formData.append('jcr:title', this.state.title);
        }

        if (this.state.orderable) {
            formData.append('jcr:primaryType', 'sling:OrderedFolder');
        }
        const csrf = await getCsrf();
        await fetch(`${this.props.item.path}/`, {
            method: 'POST',
            credentials: 'same-origin',
            headers: { 'CSRF-Token': csrf.token },
            body: formData,
        });
        this.props.onDialogClose(true);
    }

    render() {
        return (
            <Dialog
                open={this.props.open}
                onConfirm={this.dialogConfirm}
                onCancel={this.dialogCancel}
                cancelLabel="Cancel"
                confirmLabel="Create"
                title="Create folder"
            >
                <CreateFolder
                    name={this.state.name}
                    title={this.state.title}
                    onChange={this.handleFolderChange}
                />
            </Dialog>
        );
    }
}
