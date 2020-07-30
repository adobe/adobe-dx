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
import CreateFolder from '../CreateFolder';

describe('CreateFolder', () => {
    test('should set the name and title values via props', () => {
        const { getByPlaceholderText } = render(<CreateFolder name="MyName" title="MyTitle" />);
        expect(getByPlaceholderText('Name').value).toBe('MyName');
        expect(getByPlaceholderText('Title').value).toBe('MyTitle');
    });

    test('should call onChange prop with target name', () => {
        const changeHandler = jest.fn();
        const { getByLabelText, getByPlaceholderText } = render(
            <CreateFolder onChange={changeHandler} />
        );

        fireEvent.change(getByPlaceholderText('Name'), { target: { value: 'test name' } });
        expect(changeHandler).toHaveBeenCalledWith({ name: 'test name' });
        changeHandler.mockReset();

        fireEvent.change(getByPlaceholderText('Title'), { target: { value: 'test title' } });
        expect(changeHandler).toHaveBeenCalledWith({ title: 'test title' });
        changeHandler.mockReset();

        fireEvent.click(getByLabelText('Ordered'));
        expect(changeHandler).toHaveBeenCalledWith({ orderable: true });
        changeHandler.mockReset();

        fireEvent.click(getByLabelText('Ordered'));
        expect(changeHandler).toHaveBeenCalledWith({ orderable: false });
    });
});
