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
