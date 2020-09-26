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
import colorfieldMock from '../__mocks__/colorfield.html';
import { handleChange, getColorfieldInput } from '../index';

document.body.innerHTML = colorfieldMock;

test('should use selected value', () => {
    const colorInput = document.querySelector('#selectedValue');
    colorInput.value = '#FE0000';
    const val = handleChange(colorInput);
    expect(val).toBe('red');
});

test('should use custom value', () => {
    const colorInput = document.querySelector('#customValue');
    colorInput.value = '#FE0000';
    const val = handleChange(colorInput);
    expect(val).toBe('#FE0000');
});

test('get colorfield from parent', () => {
    const val = getColorfieldInput(document);
    expect(val).toBeTruthy();
});
