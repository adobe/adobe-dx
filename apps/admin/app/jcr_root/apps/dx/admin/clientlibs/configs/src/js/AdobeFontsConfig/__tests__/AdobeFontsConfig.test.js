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
import userEvent from '@testing-library/user-event';
import AdobeFontsConfig from '../index';

const DEFAULT_STATE = {
    name: 'adobe-fonts',
    data: {
        'jcr:primaryType': 'cq:Page',
        'jcr:content': {
            'jcr:primaryType': 'nt:unstructured',
            'sling:resourceType': 'dx/structure/components/adobe-fonts',
            'jcr:title': 'Adobe Fonts',
            projectId: '',
            embedType: 'linkTag',
            configKey: 'adobe-fonts',
            tagComponentHeader: 'dx/structure/components/adobe-fonts/header',
        },
    },
    replace: true,
};

test('should init with default state', () => {
    const setConfig = jest.fn();
    const { getByPlaceholderText } = render(<AdobeFontsConfig config={{}} setConfig={setConfig} />);
    expect(getByPlaceholderText('Title').value).toBe('Adobe Fonts');
    expect(getByPlaceholderText('Project ID').value).toBe('');
    expect(setConfig).toHaveBeenCalledWith(DEFAULT_STATE);
});
test('should init with given config data', () => {
    const setConfig = jest.fn();
    const config = {
        name: 'fonts-config-test',
        data: {
            'jcr:primaryType': 'cq:Page',
            'jcr:content': {
                'jcr:primaryType': 'nt:unstructured',
                'sling:resourceType': 'dx/structure/components/adobe-fonts',
                'jcr:title': 'Adobe Fonts Test',
                projectId: '3333',
                embedType: 'styleTag',
                configKey: 'adobe-fonts',
                tagComponentHeader: 'dx/structure/components/adobe-fonts/header',
            },
        },
    };
    const { getByPlaceholderText } = render(
        <AdobeFontsConfig config={config} setConfig={setConfig} />
    );
    expect(getByPlaceholderText('Title').value).toBe('Adobe Fonts Test');
    expect(getByPlaceholderText('Project ID').value).toBe('3333');
    // update config obj with properties added by AdobeFontsConfig
    config.cleanName = 'fonts-config-test';
    config.replace = true;
    expect(setConfig).toHaveBeenCalledWith(config);
});

test('creating and updating a new fonts config', async () => {
    const setConfig = jest.fn();
    const { getByPlaceholderText, getByLabelText, getByText } = render(
        <AdobeFontsConfig config={{}} setConfig={setConfig} mode="create" />
    );

    const nameField = getByPlaceholderText('name');
    const titleField = getByPlaceholderText('Title');

    expect(titleField.value).toBe('Adobe Fonts');
    expect(getByPlaceholderText('Project ID').value).toBe('');
    expect(nameField).toBeInTheDocument();

    setConfig.mockClear();
    await userEvent.clear(nameField);
    await userEvent.type(nameField, 'New FontConfig Name');

    let updatedState = { ...DEFAULT_STATE, name: 'New FontConfig Name' };
    await waitFor(() => expect(setConfig).toHaveBeenLastCalledWith(updatedState));

    // Hide the console.error warning about uncontrolled inputs
    const consoleError = console.error;
    console.error = () => {};

    setConfig.mockClear();
    await userEvent.clear(titleField);
    await userEvent.type(titleField, 'New FontConfig Title');
    updatedState = {
        ...updatedState,
        ...{
            cleanName: 'NewFontConfigTitle',
            data: {
                'jcr:content': {
                    ...updatedState.data['jcr:content'],
                    'jcr:title': 'New FontConfig Title',
                },
                'jcr:primaryType': 'cq:Page',
            },
        },
    };
    await waitFor(() => expect(setConfig).toHaveBeenLastCalledWith(updatedState));

    setConfig.mockClear();
    fireEvent.click(getByLabelText('Embed'));
    fireEvent.click(getByText('Script Tag'));
    updatedState.data['jcr:content'].embedType = 'scriptTag';
    await waitFor(() => expect(setConfig).toHaveBeenLastCalledWith(updatedState));

    console.error = consoleError;
});
