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
import { fireEvent, render, waitFor } from '@testing-library/react';
import getCsrf from '../../utils/csrf';
import FolderDialog from '../FolderDialog';

jest.mock('../../utils/csrf');
window.fetch = jest.fn();
window.FormData = class FormData {
    constructor() {
        this.values = {};
    }

    append = (name, val) => {
        this.values[name] = val;
    };
};

describe('FolderDialog', () => {
    const item = {
        name: 'TestItem',
        title: 'TestTitle',
        path: '/test/path',
    };

    test('should call onDialogClose(false) when canceling', () => {
        const onDialogClose = jest.fn();

        const { getByText } = render(<FolderDialog item={item} onDialogClose={onDialogClose} />);

        const cancelBtn = getByText('Cancel');
        fireEvent.click(cancelBtn.parentElement);
        expect(onDialogClose).toHaveBeenCalledWith(false);
    });

    test('should call onDialogClose(true) when confirming', async () => {
        const onDialogClose = jest.fn();
        getCsrf.mockImplementation(() => ({ token: 'heres_a_token' }));

        const { getByLabelText, getByPlaceholderText, getByText } = render(
            <FolderDialog item={item} onDialogClose={onDialogClose} />
        );

        fireEvent.change(getByPlaceholderText('Name'), { target: { value: 'FolderName' } });
        fireEvent.change(getByPlaceholderText('Title'), { target: { value: 'FolderTitle' } });

        const createBtn = getByText('Create');
        fireEvent.click(createBtn.parentElement);

        await waitFor(() => expect(onDialogClose).toHaveBeenCalledWith(true));

        await waitFor(() => expect(fetch.mock.calls[0][0]).toBe('/test/path/'));

        await waitFor(() =>
            expect(fetch.mock.calls[0][1].headers['CSRF-Token']).toBe('heres_a_token')
        );

        await waitFor(() =>
            expect(fetch.mock.calls[0][1].body.values).toStrictEqual({
                ':name': 'FolderName',
                'jcr:title': 'FolderTitle',
            })
        );

        fireEvent.click(getByLabelText('Ordered'));
        fireEvent.click(createBtn.parentElement);

        await waitFor(() =>
            expect(fetch.mock.calls[1][1].body.values).toStrictEqual({
                ':name': 'FolderName',
                'jcr:title': 'FolderTitle',
                'jcr:primaryType': 'sling:OrderedFolder',
            })
        );
    });

    test('should have name and title fields', () => {
        const { getByLabelText } = render(<FolderDialog item={item} />);
        expect(getByLabelText('Name')).toBeDefined();
        expect(getByLabelText('Title')).toBeDefined();
    });
});
