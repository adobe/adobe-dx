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
import { Grid, GridRow, GridColumn } from '@react/react-spectrum/Grid';
import Textfield from '@react/react-spectrum/Textfield';

const ENCRYPT_URL = '/apps/dx/admin/services/encryptValues.json';
const JCR_CONTENT = 'jcr:content';
const DEFAULT_STATE = {
    name: 'marketo-config',
    data: {
        'jcr:primaryType': 'cq:Page',
        'jcr:content': {
            'jcr:primaryType': 'cq:PageContent',
            'sling:resourceType': 'dx/content/components/marketo',
            'jcr:title': 'Marketo',
            configKey: 'marketo-config',
            tagComponentFooter: 'dx/content/components/marketo/footer',
        },
    },
    replace: true,
};
export default class MarketoConfig extends React.Component {
    constructor(props) {
        super(props);
        this.setupState();
        this.setConfig();
    }

    setupState() {
        if (this.props.config.data) {
            const { name, data } = this.props.config;
            // eslint-disable-next-line react/no-direct-mutation-state
            this.state = { name, data, replace: true, cleanName: name };
        } else {
            // eslint-disable-next-line react/no-direct-mutation-state
            this.state = DEFAULT_STATE;
        }
    }

    setConfig = () => {
        this.props.setConfig(this.state);
    };

    onContentChange = (value, e) => {
        const { data } = this.state;
        data[JCR_CONTENT][e.target.name] = value;
        this.setState({ data }, this.setConfig);
    };

    getEncryptedValue = async (value) => {
        return fetch(`${ENCRYPT_URL}?toBeEncrypted=${value}`)
            .then((res) => {
                return res.json();
            })
            .catch((err) => {
                // eslint-disable-next-line no-console
                console.log('Error: ', err);
            });
    };

    onEncryptContentChange = async (value, e) => {
        const { name } = e.target;
        const encryptedName = name.replace('Plain', '');
        const { encrypted } = await this.getEncryptedValue(value);
        const { data } = this.state;
        data[JCR_CONTENT][encryptedName] = encrypted;
        this.setState({ data }, this.setConfig);
    };

    render() {
        return (
            <Form>
                <Grid>
                    <GridRow>
                        <GridColumn size={6}>
                            <FormItem label="Name">
                                <Textfield name="name" disabled value={this.state.name} />
                            </FormItem>
                            <FormItem label="Title">
                                <Textfield
                                    name="jcr:title"
                                    value={this.state.data[JCR_CONTENT]['jcr:title']}
                                    placeholder="Title"
                                    onChange={this.onContentChange}
                                />
                            </FormItem>
                            <FormItem label="Base URL">
                                <Textfield
                                    name="baseUrl"
                                    value={this.state.data[JCR_CONTENT].baseUrl}
                                    placeholder="Base URL"
                                    onChange={this.onContentChange}
                                />
                            </FormItem>
                            <FormItem label="Munchkin ID">
                                <Textfield
                                    name="munchkinId"
                                    value={this.state.data[JCR_CONTENT].munchkinId}
                                    placeholder="Munchkin ID"
                                    onChange={this.onContentChange}
                                />
                            </FormItem>
                        </GridColumn>
                        <GridColumn size={6}>
                            <FormItem label="Client ID">
                                <Textfield
                                    name="clientId"
                                    value={this.state.data[JCR_CONTENT].clientId}
                                    placeholder="Client ID"
                                    onChange={this.onContentChange}
                                />
                            </FormItem>
                            <FormItem label="Client Secret">
                                <Textfield
                                    name="clientSecretPlain"
                                    placeholder="Client Secret"
                                    onChange={this.onEncryptContentChange}
                                />
                            </FormItem>
                            <FormItem label="Encrypted">
                                <Textfield
                                    disabled
                                    name="clientSecret"
                                    value={this.state.data[JCR_CONTENT].clientSecret}
                                />
                            </FormItem>
                            <FormItem label="REST API URL">
                                <Textfield
                                    name="restApiBaseUrl"
                                    value={this.state.data[JCR_CONTENT].restApiBaseUrl}
                                    placeholder="REST API URL"
                                    onChange={this.onContentChange}
                                />
                            </FormItem>
                        </GridColumn>
                    </GridRow>
                </Grid>
            </Form>
        );
    }
}
