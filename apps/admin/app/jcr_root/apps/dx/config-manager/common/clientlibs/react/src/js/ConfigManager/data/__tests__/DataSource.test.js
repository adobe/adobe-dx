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

import DataSource from '../DataSource';

window.fetch = jest.fn().mockImplementation((path) =>
    Promise.resolve({
        json: () => Promise.resolve({ items: ['one', 'two'] }),
    })
);

describe('Datasource', () => {
    afterEach(() => {
        window.fetch.mockClear();
    });

    test('should fetch datasourcepath with getTree', async () => {
        const cds = new DataSource('path/to/datasource');
        const items = await cds.getTree();
        expect(items).toEqual(['one', 'two']);
        expect(window.fetch).toHaveBeenCalledTimes(1);
        expect(window.fetch).toHaveBeenCalledWith('path/to/datasource.model.json');
    });

    test('getChildren should use getTree when not passed any arguments ', async () => {
        const cds = new DataSource('path/to/datasource');
        const items = await cds.getChildren();
        expect(items).toEqual(['one', 'two']);
        expect(window.fetch).toHaveBeenCalledTimes(1);
        expect(window.fetch).toHaveBeenCalledWith('path/to/datasource.model.json');
    });

    test('getChildren should return items.children when items arg is passed', async () => {
        const cds = new DataSource('path/to/datasource');
        const item = { children: 'over here' };
        const result = await cds.getChildren(item);
        expect(result).toBe('over here');
    });

    test('hasChildren should return a boolean indicating if item has children', async () => {
        const cds = new DataSource('path/to/datasource');
        const item1 = { children: 'over here' };
        const result1 = await cds.hasChildren(item1);
        expect(result1).toStrictEqual(true);

        const item2 = { notchildren: 'over here' };
        const result2 = await cds.hasChildren(item2);
        expect(result2).toStrictEqual(false);
    });

    test('isItemEqual should return item.label equality', () => {
        const cds = new DataSource('path/to/datasource');
        const item1 = { label: 'labelOne' };
        const itemOne = { label: 'labelOne' };
        const item2 = { label: 'labelTwo' };

        expect(cds.isItemEqual(item1, itemOne)).toStrictEqual(true);
        expect(cds.isItemEqual(item1, item2)).toStrictEqual(false);
    });

    test('getTree should output console error when fetch fails', async () => {
        global.console = { log: jest.fn() };
        window.fetch = jest.fn().mockImplementation(
            (path) =>
                new Promise((resolve, reject) => {
                    reject('fetch error');
                })
        );

        const cds = new DataSource('path/to/datasource');
        try {
            const items = await cds.getTree();
        } catch (e) {
            expect(e.toString()).toBe("TypeError: Cannot read property 'items' of undefined");
        }
        expect(window.fetch).toHaveBeenCalledTimes(1);
        expect(window.fetch).toHaveBeenCalledWith('path/to/datasource.model.json');
        expect(global.console.log).toHaveBeenCalledTimes(1);
        expect(global.console.log.mock.calls[0]).toEqual(['Error: ', 'fetch error']);
    });
});
