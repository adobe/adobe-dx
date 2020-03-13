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
import ColumnItem from '../columnItem';

jest.mock('@react/react-spectrum/Icon/Folder', () => () => <div data-testid="folderIcon"/>);
jest.mock('@react/react-spectrum/Icon/Settings', () => () => <div data-testid="settingsIcon"/>);

describe('Icon Type', () => {
    test('should show a folder icon with label when iconType is folder', () => {
        const { getByTestId, getByText } = render(<ColumnItem iconType="folder" label="myFolder" />);
        expect(getByText('myFolder')).toHaveClass('dx-ColumnItemLabel');
        expect(getByTestId('folderIcon')).toBeInTheDocument();
    });

    test('should default to a settings icon with label when iconType is not specified', () => {
        const { getByTestId, getByText } = render(<ColumnItem label="mySettings" />);
        expect(getByText('mySettings')).toHaveClass('dx-ColumnItemLabel');
        expect(getByTestId('settingsIcon')).toBeInTheDocument();
    });
});
