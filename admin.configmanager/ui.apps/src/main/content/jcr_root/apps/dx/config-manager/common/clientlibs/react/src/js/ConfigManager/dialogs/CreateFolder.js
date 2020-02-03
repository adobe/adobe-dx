import React from 'react';
import {Form, FormItem} from '@react/react-spectrum/Form';
import Textfield from '@react/react-spectrum/Textfield';
import Checkbox from '@react/react-spectrum/Checkbox';

const CreateFolder = (props) => {
    const handleChange = (value, e) => {
        const change = {};
        change[e.target.name] = value;
        props.onChange(change);
    }

    return (
        <Form method="POST">
            <FormItem label="Name">
                <Textfield
                    name="name"
                    value={props.name}
                    placeholder="name"
                    onChange={handleChange} />
            </FormItem>
            <FormItem label="Title">
                <Textfield
                    name="title"
                    value={props.title}
                    placeholder="Title"
                    onChange={handleChange} />
            </FormItem>
            <FormItem>
                <Checkbox
                    label="Ordered"
                    name="orderable"
                    onChange={handleChange} />
            </FormItem>
        </Form>
    );
}

export default CreateFolder;
