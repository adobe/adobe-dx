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