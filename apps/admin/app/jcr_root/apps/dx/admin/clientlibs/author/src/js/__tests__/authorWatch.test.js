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

class SimulateMutationObserver {
    constructor(callback) {
        this.obs = [];
        this.callback = callback;
        window.observer = this;
    }

    observe(elem, opts) {
        this.obs.push({ elem, opts });
        return this;
    }

    fire(...obj) {
        this.callback(...obj);
    }
}

window.MutationObserver = SimulateMutationObserver;
window.CQ = {};

// use require as import is hoisted
require('../app');

describe('authorWatch', () => {
    beforeEach(() => {
        window.dx.author.watch.functions = [];
    });
    test('should fire when node is added', () => {
        const watchFunc = jest.fn();
        window.dx.author.watch.registerFunction(watchFunc);

        const addedNode = { nodeType: 1, tagName: 'DIV' };
        window.observer.fire([{ addedNodes: [addedNode] }]);
        expect(watchFunc).toHaveBeenCalledWith(addedNode);
    });

    test('should not do anything if no functions are registered', () => {
        // This test doesn't actually test anything, but is the only way to
        // cover the if (window.dx.author.watch.functions.length > 0 condition
        const watchFunc = jest.fn();
        const addedNode = { nodeType: 1, tagName: 'DIV' };
        window.observer.fire([{ addedNodes: [addedNode] }]);
        expect(watchFunc).not.toHaveBeenCalled();
    });

    test('should not fire for non element nodes', () => {
        const watchFunc = jest.fn();
        window.dx.author.watch.registerFunction(watchFunc);

        const addedNode = { nodeType: 3, tagName: 'P' };
        window.observer.fire([{ addedNodes: [addedNode] }]);
        expect(watchFunc).not.toHaveBeenCalled();
    });

    test('should not fire if there is more than one mutation', () => {
        const watchFunc = jest.fn();
        window.dx.author.watch.registerFunction(watchFunc);

        const addedNode = { nodeType: 1, tagName: 'DIV' };
        window.observer.fire([{ addedNodes: [addedNode, addedNode] }]);
        expect(watchFunc).not.toHaveBeenCalled();
    });

    test('should not fire if the tag is a script tag', () => {
        const watchFunc = jest.fn();
        window.dx.author.watch.registerFunction(watchFunc);

        const addedNode = { nodeType: 1, tagName: 'SCRIPT' };
        window.observer.fire([{ addedNodes: [addedNode] }]);
        expect(watchFunc).not.toHaveBeenCalled();
    });
});
