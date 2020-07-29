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

import Dialog from '@react/react-spectrum/Dialog';

import deleteResource from '../utils/delete';

const DeleteDialog = ({ item, onDialogClose, open }) => {
    const dialogConfirm = async () => {
        await deleteResource(item.path);
        onDialogClose(true, true);
    };

    const dialogCancel = () => {
        onDialogClose(false);
    };

    return (
        <Dialog
            title="Delete"
            variant="destructive"
            open={open}
            onConfirm={dialogConfirm}
            confirmLabel="Delete"
            onCancel={dialogCancel}
            cancelLabel="Cancel"
        >
            {item ? item.path : ''}
        </Dialog>
    );
};

DeleteDialog.propTypes = {
    item: PropTypes.shape({ path: PropTypes.string }),
    onDialogClose: PropTypes.func,
    open: PropTypes.bool,
};

// Ignore for code coverage
/* istanbul ignore next */
DeleteDialog.defaultProps = {
    item: undefined,
    onDialogClose: () => {},
    open: false,
};

export default DeleteDialog;
