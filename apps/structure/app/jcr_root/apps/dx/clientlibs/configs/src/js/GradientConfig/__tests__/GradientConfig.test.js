import React from 'react';
import { render, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import GradientConfig from '../index';

const DEFAULT_TITLE = 'DX Gradient';

describe('GradientConfig', () => {
    test('should be initially set to default values', async () => {
        const setConfigCallback = (config) => {};

        const { getByLabelText, getByText } = render(
            <GradientConfig mode="new" config={{}} setConfig={setConfigCallback} />
        );

        const titleField = getByLabelText('Title');
        await waitFor(() => expect(titleField.value).toBe(DEFAULT_TITLE));

        const nameField = getByLabelText('Name');
        const cssField = getByLabelText('CSS');

        expect(nameField.value).toBe('');
        expect(cssField.value).toBe(
            'linear-gradient(180deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95%)'
        );
    });

    test('should set the Name field based on Title field', async () => {
        const setConfigCallback = (config) => {};

        const { getByLabelText } = render(
            <GradientConfig mode="new" config={{}} setConfig={setConfigCallback} />
        );

        const titleField = getByLabelText('Title');
        const nameField = getByLabelText('Name');

        titleField.value = '';
        await userEvent.type(titleField, "I'm a title!");
        expect(nameField.value).toBe('Imatitle');
    });

    test('should use passed in config values', async () => {
        const setConfigCallback = (config) => {};
        const config = {
            data: {
                configKey: 'dx-gradient',
                gradientCss:
                    'radial-gradient(circle at center, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95.0%)',
                'jcr:primaryType': 'nt:unstructured',
                text: 'My Gradient',
                value: 'MyGradient',
            },
            name: '',
            replace: true,
        };

        const { getByLabelText } = render(
            <GradientConfig mode="new" config={config} setConfig={setConfigCallback} />
        );

        const titleField = getByLabelText('Title');
        const cssField = getByLabelText('CSS');

        await waitFor(() => expect(titleField.value).toBe('My Gradient'));
        expect(cssField.value).toBe(
            'radial-gradient(circle at center, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95.0%)'
        );
    });

    test('should be able to change css via textfield', async () => {
        const setConfigCallback = (config) => {};
        const config = {};

        const { getByLabelText } = render(
            <GradientConfig mode="new" config={config} setConfig={setConfigCallback} />
        );

        const cssField = getByLabelText('CSS');
        cssField.value = '';
        await userEvent.type(
            cssField,
            'linear-gradient(180deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95.0%)'
        );
        fireEvent.keyPress(cssField, { key: 'Enter', code: 13, charCode: 13 });
        // the blur event does not fire in jsdom, so trigger manually
        fireEvent.blur(cssField);
        expect(cssField.value).toBe(
            'linear-gradient(180deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95%)'
        );
    });
});
