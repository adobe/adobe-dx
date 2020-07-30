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
import { shallow } from 'enzyme';
import ConfigManager from '../index';

window.dx = { configManager: { configs: {} } };

// Consumer Code
window.dx.configManager.configs['adobe-fonts'] = {
    label: 'Adobe Fonts',
    app: () => {},
};

describe('ConfigManager', () => {
    test('Actionbar display', async () => {
        const wrapper = shallow(<ConfigManager dataSourcePath="myContent" />);
        const instance = wrapper.instance();
        expect(instance.state.fixedActionBar.show).toBeFalsy();
        instance.selectionChange(['selected item']);
        expect(instance.state.fixedActionBar.show).toBeTruthy();
        instance.toggleActionBar();
        expect(instance.state.fixedActionBar.show).toBeFalsy();
    });

    test('Dialog', () => {
        const wrapper = shallow(<ConfigManager dataSourcePath="myContent" />);
        const instance = wrapper.instance();
        expect(instance.state.dialogType).toBeFalsy();
        expect(instance.state.configKey).toBeFalsy();

        instance.openDialog('OpenDialog', 'ConfigKey');
        expect(instance.state.dialogType).toBe('OpenDialog');
        expect(instance.state.configKey).toBe('ConfigKey');

        instance.closeDialog();
        expect(instance.state.dialogType).toBeUndefined();
        expect(instance.state.configKey).toBeUndefined();

        const dataSource = instance.state.dataSource;
        instance.closeDialog(true, true);
        expect(instance.state.fixedActionBar.show).toBeTruthy();
        expect(instance.state.dataSource).not.toBe(dataSource);
    });

    test('Items', () => {
        const wrapper = shallow(<ConfigManager dataSourcePath="myContent" />);
        const instance = wrapper.instance();
        expect(instance.state.selectedItem).toStrictEqual({ path: '/conf' });

        const items = [{ path: 'item1' }, { path: 'item2' }, { path: 'item3' }];
        instance.navigate(items);

        expect(instance.state.selectedItem).toStrictEqual({ path: 'item3' });
        expect(instance.state.selectedItems).toStrictEqual(items);
    });
});
