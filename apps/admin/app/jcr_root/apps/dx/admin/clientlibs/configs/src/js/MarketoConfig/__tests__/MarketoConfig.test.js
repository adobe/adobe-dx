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
import MarketoConfig from '../index';

const DEFAULT_STATE = {
    name: 'marketo-config',
    data: {
        'jcr:primaryType': 'cq:Page',
        'jcr:content': {
            'jcr:primaryType': 'cq:PageContent',
            'sling:resourceType': 'dx/content/components/marketo',
            'jcr:title': 'Marketo',
            configKey: 'marketo-config',
            tagComponentFooter: 'dx/content/components/marketo/footer',
        },
    },
    replace: true,
};

test('should init with default state', () => {
    const setConfig = jest.fn();
    const { getByPlaceholderText } = render(<MarketoConfig config={{}} setConfig={setConfig} />);
    expect(getByPlaceholderText('Title').value).toBe('Marketo');
    expect(getByPlaceholderText('Base URL').value).toBe('');
    expect(setConfig).toHaveBeenCalledWith(DEFAULT_STATE);
});

test('should init with given config data', () => {
    const setConfig = jest.fn();
    const config = {
        name: 'marketo-config-test',
        data: {
            'jcr:primaryType': 'cq:Page',
            'jcr:content': {
                'jcr:primaryType': 'nt:unstructured',
                'sling:resourceType': 'dx/content/components/marketo',
                'jcr:title': 'Marketo Test',
                baseUrl: 'http://marketo.com',
                configKey: 'marketo-config',
                tagComponentFooter: 'dx/content/components/marketo/footer',
            },
        },
    };
    const { getByPlaceholderText } = render(
        <MarketoConfig config={config} setConfig={setConfig} />
    );
    expect(getByPlaceholderText('Title').value).toBe('Marketo Test');
    expect(getByPlaceholderText('Base URL').value).toBe('http://marketo.com');
    // update config obj with properties added by AdobeFontsConfig
    config.cleanName = 'marketo-config-test';
    config.replace = true;
    expect(setConfig).toHaveBeenCalledWith(config);
});

test('creating and updating a new config', async () => {
    const setConfig = jest.fn();
    const { getByPlaceholderText, getByLabelText, getByText } = render(
        <MarketoConfig config={{}} setConfig={setConfig} mode="create" />
    );

    const nameField = getByLabelText('Name');
    const titleField = getByPlaceholderText('Title');
    const baseUrlField = getByPlaceholderText('Base URL');

    expect(titleField.value).toBe('Marketo');
    expect(baseUrlField.value).toBe('');
    expect(nameField).toBeInTheDocument();

    setConfig.mockClear();
    await userEvent.clear(titleField);
    await userEvent.type(titleField, 'New Marketo Config');

    expect(titleField.value).toBe('New Marketo Config');

    // // Hide the console.error warning about uncontrolled inputs
    // const consoleError = console.error;
    // console.error = () => {};

    // setConfig.mockClear();
    // await userEvent.clear(titleField);
    // await userEvent.type(titleField, 'New FontConfig Title');
    // updatedState = {
    //     ...updatedState,
    //     ...{
    //         cleanName: 'NewFontConfigTitle',
    //         data: {
    //             'jcr:content': {
    //                 ...updatedState.data['jcr:content'],
    //                 'jcr:title': 'New FontConfig Title',
    //             },
    //             'jcr:primaryType': 'cq:Page',
    //         },
    //     },
    // };
    // await waitFor(() => expect(setConfig).toHaveBeenLastCalledWith(updatedState));

    // setConfig.mockClear();
    // fireEvent.click(getByLabelText('Embed'));
    // fireEvent.click(getByText('Script Tag'));
    // updatedState.data['jcr:content'].embedType = 'scriptTag';
    // await waitFor(() => expect(setConfig).toHaveBeenLastCalledWith(updatedState));

    // console.error = consoleError;
});
