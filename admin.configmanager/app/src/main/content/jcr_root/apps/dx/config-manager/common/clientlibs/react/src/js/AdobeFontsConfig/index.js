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

const JCR_CONTENT = 'jcr:content';
const DEFAULT_STATE = {
    name: 'adobe-fonts',
    data: {
        'jcr:primaryType': 'cq:Page',
        'jcr:content': {
            'jcr:primaryType': 'nt:unstructured',
            'sling:resourceType': 'dx/config-manager/adobe-fonts',
            'jcr:title': 'Adobe Fonts',
            'projectId': '',
            'embedType': 'linkTag',
            'configKey': 'adobe-fonts',
            'tagComponentHeader': 'dx/adobe-fonts/header'
        }
    },
    replace: true
};
const EMBED_OPTIONS = [
    { label: 'Link Tag', value: 'linkTag' },
    { label: 'Style Tag', value: 'styleTag' },
    { label: 'Script Tag', value: 'scriptTag' }
];

export default class AdobeFonts extends React.Component {
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

    onContentChange = (value, e) => {
        const data = this.state.data;
        data[JCR_CONTENT][e.target.name] = value;

        if (e.target.name === 'jcr:title') {
            this.setState({ cleanName: value.replace(/\W/g, '') });
        }

        this.setState({ data }, this.setConfig);
    }

    onSelectChange = (value, name) => {
        const data = this.state.data;
        data[JCR_CONTENT][name] = value;
        this.setState({ data }, this.setConfig);
    }

    onSelectOpen = (e) => {
        this.setState({ currentSelectValue: e.target });
    }

    getNameFieldItem = () => {
        if (this.props.mode === 'create') {
            return (<FormItem label="Name">
                        <Textfield
                            name="name"
                            value={this.state.cleanName}
                            placeholder="name"
                            onChange={this.onNameChange} />
                    </FormItem>);
        }
        return null;
    }

    render() {
        const NameFieldItem = this.getNameFieldItem;

        return (
            <Form>
                <NameFieldItem />
                <FormItem label="Title">
                    <Textfield
                        name="jcr:title"
                        value={this.state.data[JCR_CONTENT]['jcr:title']}
                        placeholder="Title"
                        onChange={this.onContentChange} />
                </FormItem>
                <FormItem label="Project ID">
                    <Textfield
                        name="projectId"
                        value={this.state.data[JCR_CONTENT].projectId}
                        placeholder="Project ID"
                        onChange={this.onContentChange} />
                </FormItem>
                <FormItem label="Embed">
                    <Select
                        name="embedType"
                        defaultValue={this.state.data[JCR_CONTENT].embedType}
                        onChange={(value) => this.onSelectChange(value, 'embedType')}
                        options={EMBED_OPTIONS} />
                </FormItem>
            </Form>
        );
    }
}