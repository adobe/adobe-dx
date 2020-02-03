import React from 'react';

import Provider from '@react/react-spectrum/Provider';
import Underlay from './Underlay';
import FolderDialog from './FolderDialog';
import ConfigDialog from './ConfigDialog';
import DeleteDialog from './DeleteDialog';

const Dialog = (props) => {
    const empty = () => {
        return null;
    };

    const DialogType = props.dialogType === 'folder' ? FolderDialog
                     : props.dialogType === 'delete' ? DeleteDialog
                     : props.dialogType === 'config' ? ConfigDialog
                     : props.dialogType === 'edit' ? ConfigDialog
                     : empty;

    return (
        <Provider theme="light">
            <Underlay open={props.open} />
            <DialogType {...props} />
        </Provider>
    );
}

export default Dialog;
