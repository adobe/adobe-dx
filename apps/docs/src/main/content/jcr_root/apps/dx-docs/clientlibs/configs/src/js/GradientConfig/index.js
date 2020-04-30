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

import { Form, FormItem } from '@react/react-spectrum/Form';
import Textfield from '@react/react-spectrum/Textfield';
import Select from '@react/react-spectrum/Select';

const DEFAULT_STATE = {
    data: {
        'jcr:primaryType': 'nt:unstructured',
        'configKey': 'dx-gradient',
    },
    replace: true
};

export default class GradientConfig extends React.Component {
    constructor(props) {
        super(props);
        this.setupState();
        this.setConfig();
    }

    setupState() {
        if (this.props.config.data) {
            const { name, data } = this.props.config;
            this.state = { name, data, replace: true, cleanName: name };
        } else {
            this.state = DEFAULT_STATE;
        }
    }

    setConfig = () => {
        this.props.setConfig(this.state);
    }

    onNameChange = (value) => {
        this.setState({ name: value }, this.setConfig);
    }

    cleanName = (value) => {
        return value.replace(/\W/g, '');
    }

    onContentChange = (value, e) => {
        const data = this.state.data;
        data[e.target.name] = value;
        if (e.target.name === 'text' && this.props.mode === 'new') {
            this.setState({ name: this.cleanName(value) });
        }
        this.setState({ data }, this.setConfig);
    }

    render() {
        return (
            <Form>
                <FormItem label="Name">
                    <Textfield
                        disabled
                        name="name"
                        value={this.state.name}
                        placeholder="name"
                        onChange={this.onNameChange} />
                </FormItem>
                <FormItem label="Text">
                    <Textfield
                        name="text"
                        value={this.state.data.text}
                        placeholder="Text"
                        onChange={this.onContentChange} />
                </FormItem>
                <FormItem label="Value">
                    <Textfield
                        name="value"
                        value={this.state.data.value}
                        placeholder="Value"
                        onChange={this.onContentChange} />
                </FormItem>
            </Form>
        );
    }
}