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
import { render } from '@testing-library/react';
import ConfigDialog from '../ConfigDialog';
import Dialog from '../Dialog';

jest.mock('../ConfigDialog');

window.dx = {
    configManager: {
        configs: {
            'config-key': {
                label: 'My Config Key',
                app: () => <div> My Config </div>,
            },
        },
    },
};

describe('Dialog', () => {
    describe('should render the passed in dialogType', () => {
        beforeEach(() => {
            ConfigDialog.mockClear();
        });

        test('folder', () => {
            const { getByText } = render(<Dialog dialogType="folder" />);
            expect(getByText('Create folder')).toBeInTheDocument();
        });

        test('delete', () => {
            const { getAllByText } = render(<Dialog dialogType="delete" />);
            // Delete H2 and Delete Button
            expect(getAllByText('Delete')).toHaveLength(2);
        });

        test('config', () => {
            const { getByText } = render(<Dialog dialogType="config" configKey="config-key" />);
            expect(ConfigDialog).toHaveBeenCalledTimes(1);
        });

        test('edit', () => {
            const { getByText } = render(<Dialog dialogType="edit" />);
            expect(ConfigDialog).toHaveBeenCalledTimes(1);
        });

        test('invalid or no dialogType passed in', () => {
            const { container } = render(<Dialog dialogType="" />);
            expect(container.outerHTML).toBe(
                '<div><div class="react-spectrum-provider spectrum spectrum--light spectrum--medium"><div class="spectrum-Underlay"></div></div></div>'
            );
        });
    });
});
