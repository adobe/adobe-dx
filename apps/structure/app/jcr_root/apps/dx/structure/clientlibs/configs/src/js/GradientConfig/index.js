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

import React, { useEffect, useState } from 'react';
import { Form, FormItem } from '@react/react-spectrum/Form';
import { Grid, GridRow, GridColumn } from '@react/react-spectrum/Grid';
import Textfield from '@react/react-spectrum/Textfield';
import GradientPicker from 'react-gpickr';
import normalizeGradientStr from './utils/normalizeGradientStr';
import GradientControl from './GradientControl';
import GradientDocumentation from './GradientDocumentation';

const DEBOUNCE_MS = 100;
const DEFAULT_STATE = {
    data: {
        configKey: 'dx-gradient',
        // Fade to Adobe Red
        gradientCss: 'linear-gradient(180deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95%)',
        'jcr:primaryType': 'nt:unstructured',
        text: 'DX Gradient',
        value: '',
    },
    name: '',
    replace: true,
};
const EMPTY_STATE = {
    data: {
        configKey: '',
        gradientCss: '',
        text: '',
        value: '',
    },
    name: '',
    replace: true,
};

const PICKR_CONFIG = {
    theme: 'monolith',
    hex: true,
    rgba: true,
    hsla: true,
};

const SUPPORTED_MODES = {
    linear: true,
    radial: true,
};

const cleanName = (value) => value.replace(/\W/g, '');

const mergeConfigData = (prevConfig, updatedProps) => ({
    ...prevConfig,
    data: { ...prevConfig.data, ...updatedProps },
});

const GradientConfig = (props) => {
    const [angle, setAngle] = useState();
    const [config, setConfig] = useState(EMPTY_STATE);
    const [gradientCss, setGradientCss] = useState();
    const [mode, setMode] = useState();

    useEffect(() => {
        if (props.config.data) {
            const { name, data } = props.config;
            setConfig({ name, data, replace: true, cleanName: name });
        } else {
            setConfig(DEFAULT_STATE);
        }
    }, []);

    useEffect(() => {
        if (Object.keys(config).length > 1) props.setConfig(config);
        setGradientCss(config.data.gradientCss);
    }, [config]);

    const updateConfigData = (updatedProps) =>
        setConfig((prevConfig) => mergeConfigData(prevConfig, updatedProps));

    const onAngleChange = (ang) => {
        setAngle(typeof ang === 'number' ? `${ang}deg` : ang);
    };

    const onGradientCssFieldBlur = (e) => {
        const fieldVal = e.currentTarget.value;
        if (fieldVal !== config.data.gradientCss) {
            const gradient = normalizeGradientStr(fieldVal);
            if (gradient) {
                if (gradient !== fieldVal) e.currentTarget.value = gradient;
                updateConfigData({ gradientCss: gradient });
            } else {
                // revert back to original value
                e.currentTarget.value = config.data.gradientCss;
            }
        }
    };

    const onCssFieldChange = (val) => {
        setGradientCss(val);
    };

    const onCssFieldKeypress = (e) => {
        if (e.key === 'Enter') {
            e.currentTarget.blur();
        }
    };

    const onGradientModeChange = (newMode, e) => {
        e.preventDefault();
        setMode(newMode);
    };

    const onGradientPickerChange = (gpickr) => {
        setConfig((prevConfig) => {
            const gradientStr = gpickr.getGradient();
            if (gradientStr !== prevConfig.data.gradientCss) {
                return mergeConfigData(prevConfig, { gradientCss: gradientStr });
            }
            return prevConfig;
        });
    };

    const angleBtnClick = (newAngle, e) => {
        e.preventDefault();
        setAngle(newAngle);
    };

    const onModeChange = (newMode) => {
        setMode(newMode);
    };

    const onTitleChange = (title) => {
        if (props.mode === 'new') {
            const name = cleanName(title);
            setConfig((prevConfig) => mergeConfigData({ ...prevConfig, name }, { value: name }));
        }
        updateConfigData({ text: title });
    };

    return (
        <div className="dx-gradient-picker">
            <Grid>
                <GridRow>
                    <GridColumn size={6}>
                        <Form>
                            <FormItem label="Name">
                                <Textfield disabled name="name" value={config.name} />
                            </FormItem>
                            <FormItem label="Title">
                                <Textfield
                                    name="title"
                                    value={config.data.text}
                                    placeholder="Title"
                                    onChange={onTitleChange}
                                />
                            </FormItem>
                        </Form>
                    </GridColumn>
                    <GridColumn size={6} style={{ paddingRight: 0 }}>
                        <Form>
                            <FormItem
                                label="CSS"
                                labelFor="css-string"
                                className="css-string-formitem"
                            >
                                <Textfield
                                    id="css-string"
                                    className="css-string-formitem-textarea"
                                    name="gradientCss"
                                    value={gradientCss}
                                    onBlur={onGradientCssFieldBlur}
                                    onChange={onCssFieldChange}
                                    onKeyPress={onCssFieldKeypress}
                                    multiLine
                                />
                            </FormItem>
                        </Form>
                    </GridColumn>
                </GridRow>
                <GridRow>
                    <GradientPicker
                        angle={angle}
                        cssString={gradientCss}
                        debounceMS={DEBOUNCE_MS}
                        mode={mode}
                        setMode={onModeChange}
                        modes={SUPPORTED_MODES}
                        onChange={onGradientPickerChange}
                        pickrConfig={PICKR_CONFIG}
                        setAngle={onAngleChange}
                    />
                </GridRow>
                <GridRow>
                    <GridColumn size={6}>
                        <GradientDocumentation />
                    </GridColumn>
                    <GridColumn size={6} style={{ paddingRight: 0 }}>
                        <GradientControl
                            gradientMode={mode}
                            angle={angle}
                            onGradientModeChange={onGradientModeChange}
                            onAngleChange={angleBtnClick}
                        />
                    </GridColumn>
                </GridRow>
            </Grid>
        </div>
    );
};

export default GradientConfig;
