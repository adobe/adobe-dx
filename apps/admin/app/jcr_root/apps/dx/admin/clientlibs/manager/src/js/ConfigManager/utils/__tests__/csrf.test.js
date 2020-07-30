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

window.fetch = jest.fn(() =>
    Promise.resolve({
        json: () => Promise.resolve({ token: 'csrf_token' }),
    })
);

test('should fetch a token', async () => {
    const csrfResponse = await getCsrf();
    await waitFor(() => expect(fetch.mock.calls[0][0]).toBe('/libs/granite/csrf/token.json'));
    expect(csrfResponse).toStrictEqual({ token: 'csrf_token' });
});
test('should error for bad fetch', async () => {
    fetch.mockImplementationOnce(() => Promise.reject('API is down'));
    const csrfResponse = await getCsrf();
    expect(csrfResponse).toBeUndefined();
});
