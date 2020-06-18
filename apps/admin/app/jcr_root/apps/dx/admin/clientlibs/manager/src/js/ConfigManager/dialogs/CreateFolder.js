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
import Checkbox from '@react/react-spectrum/Checkbox';

const CreateFolder = (props) => {
    const handleChange = (value, e) => {
        const change = {};
        change[e.target.name] = value;
        props.onChange(change);
    };

    return (
        <Form method="POST">
            <FormItem label="Name">
                <Textfield
                    name="name"
                    value={props.name}
                    placeholder="name"
                    onChange={handleChange}
                />
            </FormItem>
            <FormItem label="Title">
                <Textfield
                    name="title"
                    value={props.title}
                    placeholder="Title"
                    onChange={handleChange}
                />
            </FormItem>
            <FormItem>
                <Checkbox label="Ordered" name="orderable" onChange={handleChange} />
            </FormItem>
        </Form>
    );
};

export default CreateFolder;
