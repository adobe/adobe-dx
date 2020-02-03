import React from 'react';

import getCsrf from '../utils/csrf';

import Dialog from '@react/react-spectrum/Dialog';
import CreateFolder from './CreateFolder';

export default class CreateFolderDialog extends React.Component {
    constructor(props) {
        super(props);
        this.state = { 
            name: '',
            title: ''
        };
        this.dialogConfirm = this.dialogConfirm.bind(this);
    }

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
            body: formData
        });
        this.props.onDialogClose(true);
    }

    dialogCancel = () => {
        this.props.onDialogClose(false);
    }

    handleFolderChange = (change) => {
        this.setState(change);
    }
    
    render() {
        return (
            <Dialog
                open={this.props.open}
                onConfirm={this.dialogConfirm}
                onCancel={this.dialogCancel}
                cancelLabel="Cancel"
                confirmLabel="Create"
                title="Create folder">
                <CreateFolder
                    name={this.state.name}
                    title={this.state.title}
                    onChange={this.handleFolderChange} />
            </Dialog>
        );
    }
}