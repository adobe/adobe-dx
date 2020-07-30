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
import ConfigDialog from '../ConfigDialog';
import getCsrf from '../../utils/csrf';

jest.mock('../../utils/csrf');

const DummyConfigComponent = () => {
    return (
        <div>
            <span>Dummy Config Component</span>
        </div>
    );
};

window.dx = { configManager: { configs: {} } };
window.dx.configManager.configs.dummyConfig = {
    label: 'Dummy Config',
    app: DummyConfigComponent,
};

test('Cancel Callback', () => {
    const onDialogClose = jest.fn();

    const { getByText } = render(
        <ConfigDialog onDialogClose={onDialogClose} configKey="dummyConfig" />
    );

    const cancelBtn = getByText('Cancel');
    fireEvent.click(cancelBtn.parentElement);
    expect(onDialogClose).toHaveBeenCalledWith(false);
});

test('Create Label', () => {
    const { getByText } = render(<ConfigDialog configKey="dummyConfig" />);

    const createLabel = getByText('Create');
    expect(createLabel).toBeInTheDocument();
});

test('Edit Config', async () => {
    const itemPath = '/test/item/path/name';
    const item = {
        name: 'TestItem',
        isPage: true,
        path: itemPath,
    };

    window.fetch = jest.fn().mockImplementation(() =>
        Promise.resolve({
            json: () => Promise.resolve({ 'jcr:content': { configKey: 'dummyConfig' } }),
        })
    );

    const onDialogClose = jest.fn();

    const { getByText } = render(<ConfigDialog item={item} onDialogClose={onDialogClose} />);

    // Confirm btn is 'Save'
    await waitFor(() => expect(getByText('Save')).toBeInTheDocument());
    const saveBtn = getByText('Save');

    window.fetch = jest.fn();
    getCsrf.mockImplementation(() => ({ token: 'csrfToken' }));

    fireEvent.click(saveBtn.parentElement);
    await waitFor(() => {
        expect(window.fetch).toHaveBeenCalledWith('/test/item/path', {
            body: expect.any(FormData),
            credentials: 'same-origin',
            headers: { 'CSRF-Token': 'csrfToken' },
            method: 'POST',
        });
    });

    const formData = Array.from(window.fetch.mock.calls[0][1].body.entries()).reduce(
        (acc, [key, val]) => ({ ...acc, [key]: val }),
        {}
    );

    expect(formData).toStrictEqual({
        ':operation': 'import',
        ':contentType': 'json',
        ':name': 'TestItem',
        ':replace': 'true',
        ':content': '{"jcr:content":{"configKey":"dummyConfig"}}',
    });

    expect(onDialogClose).toHaveBeenCalledWith(true, true);
});
