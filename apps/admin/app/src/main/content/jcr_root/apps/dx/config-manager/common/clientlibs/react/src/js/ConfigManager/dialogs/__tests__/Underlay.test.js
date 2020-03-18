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
import Underlay from '../Underlay';

describe('Underlay dialog', () => {
    test('should return div with spectrum-Underlay class', () => {
        const underlay = render(<Underlay />);
        const underlayDiv = document.querySelector('.spectrum-Underlay');
        expect(underlayDiv).not.toBeNull();
        expect(underlayDiv).not.toHaveClass('is-open');
    });

    test('should return div with spectrum-Underlay and is-open classes if open prop is defined', () => {
        const underlay = render(<Underlay open/>);
        const underlayDiv = document.querySelector('.spectrum-Underlay');
        expect(underlayDiv).not.toBeNull();
        expect(underlayDiv).toHaveClass('is-open');
    });
});
