import React from 'react';

import Dialog from '@react/react-spectrum/Dialog';

import deleteResource from '../utils/delete';

const DeleteDialog = (props) => {
    const dialogConfirm = async () => {
        await deleteResource(props.item.path);
        props.onDialogClose(true, true);
    };

    const dialogCancel = () => {
        props.onDialogClose(false);
    };

    return (
        <Dialog
            title="Delete"
            variant="destructive"
            open={props.open}
            onConfirm={dialogConfirm}
            confirmLabel="Delete"
            onCancel={dialogCancel}
            cancelLabel="Cancel">
            {props.item ? props.item.path : ''}
        </Dialog>
    );

};

export default DeleteDialog;