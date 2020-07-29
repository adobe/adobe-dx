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
import { fireEvent, render } from '@testing-library/react';
import '../../../../../../registry/src/js/app';
import CreateMenu from '../CreateMenu';

window.dx.configManager.registerApp('test-app-1', 'Test App 1', () => <div>Test App 1</div>);

window.dx.configManager.registerApp('test-app-2', 'Test App 2', () => <div>Test App 2</div>);

describe('CreateMenu', () => {
    test('should render menu items', () => {
        const onSelect = jest.fn();

        const { getByLabelText, queryByLabelText } = render(<CreateMenu onSelect={onSelect} />);

        const createButton = getByLabelText('Create');
        expect(createButton).toBeInTheDocument();
        fireEvent.click(createButton);

        expect(getByLabelText('Test App 1')).toBeInTheDocument();
        expect(getByLabelText('Test App 2')).toBeInTheDocument();

        expect(queryByLabelText('Test App 3')).not.toBeInTheDocument();

        fireEvent.click(getByLabelText('Test App 2'));
        expect(onSelect).toHaveBeenCalledWith('config', 'test-app-2');
    });

    test('should call onSelect(folder)', () => {
        const onSelect = jest.fn();
        const { getByLabelText } = render(<CreateMenu onSelect={onSelect} />);

        const createButton = getByLabelText('Create');
        fireEvent.click(createButton);

        const folderButton = getByLabelText('Folder');
        expect(folderButton).toBeInTheDocument();

        fireEvent.click(folderButton);
        expect(onSelect).toHaveBeenCalledWith('folder');
    });

    test('should not include app if config.label or config.app are not defined', () => {
        window.dx.configManager.registerApp('test-no-label', undefined, () => (
            <div>Test App 2</div>
        ));

        window.dx.configManager.registerApp('test-no-app', 'No App');

        const { getByLabelText, queryByLabelText } = render(<CreateMenu />);
        const createButton = getByLabelText('Create');
        fireEvent.click(createButton);

        const configList = getByLabelText('Folder').parentElement;
        expect(configList.childElementCount).toBe(3);
        expect(queryByLabelText('No App')).not.toBeInTheDocument();
    });
});
