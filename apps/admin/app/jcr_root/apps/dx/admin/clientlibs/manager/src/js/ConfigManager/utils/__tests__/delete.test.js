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

import { waitFor } from '@testing-library/dom';
import getCsrf from '../csrf';
import deleteResource from '../delete';

jest.mock('../../utils/csrf');
getCsrf.mockImplementation(() => ({ token: 'heres_a_token' }));

window.fetch = jest.fn();

window.FormData = class FormData {
    constructor() {
        this.values = {};
    }

    append = (name, val) => {
        this.values[name] = val;
    };
};

test('should post a form with delete operation', async () => {
    await deleteResource('my/path/');
    await waitFor(() => expect(fetch.mock.calls[0][1].headers['CSRF-Token']).toBe('heres_a_token'));
    await waitFor(() =>
        expect(fetch.mock.calls[0][1].body.values).toStrictEqual({ ':operation': 'delete' })
    );
});
