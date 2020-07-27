/*
 *  Copyright 2020 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the 'License');
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an 'AS IS' BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import React from 'react';
import { fireEvent, render, waitFor } from '@testing-library/react';
import DeleteDialog from '../DeleteDialog';
import deleteResource from '../../utils/delete';

jest.mock('../../utils/delete');

describe('DeleteDialog', () => {
    test('should call onDialogClose(false) when canceling', () => {
        const item = { path: '/my/path' };
        const onDialogClose = jest.fn();

        const { getByText } = render(<DeleteDialog item={item} onDialogClose={onDialogClose} />);

        const cancelBtn = getByText('Cancel');
        fireEvent.click(cancelBtn.parentElement);
        expect(onDialogClose).toHaveBeenCalledWith(false);
    });

    test('should delete the resource and call onDialogClose when deleting', async () => {
        const item = { path: '/my/path' };
        const onDialogClose = jest.fn();

        const { getByText } = render(<DeleteDialog item={item} onDialogClose={onDialogClose} />);

        const deleteBtn = getByText('Delete', { ignore: 'h2' });
        fireEvent.click(deleteBtn.parentElement);
        await waitFor(() => expect(deleteResource).toHaveBeenCalledWith('/my/path'));
        expect(onDialogClose).toHaveBeenCalledWith(true, true);
    });
});
