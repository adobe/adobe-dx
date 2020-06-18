import React from 'react';
import Button from '@react/react-spectrum/Button';
import ButtonGroup from '@react/react-spectrum/ButtonGroup';
import FieldLabel from '@react/react-spectrum/FieldLabel';
import { LINEAR_DIRECTION, MODE } from 'react-gpickr';

const GradientControl = ({ gradientMode, angle, onGradientModeChange, onAngleChange }) => {
    return (
        <div className="gradient-control">
            <FieldLabel label="Gradient Type">
                <ButtonGroup onChange={onGradientModeChange} value={gradientMode}>
                    <Button
                        element="button"
                        label="Linear"
                        value={MODE.LINEAR}
                        selected={gradientMode === MODE.LINEAR}
                    />
                    <Button
                        element="button"
                        label="Radial"
                        value={MODE.RADIAL}
                        selected={gradientMode === MODE.RADIAL}
                    />
                </ButtonGroup>
            </FieldLabel>
            <FieldLabel label="Corner Gradients" className="gradient-control-corner">
                <ButtonGroup
                    onChange={onAngleChange}
                    value={angle}
                    disabled={gradientMode !== MODE.LINEAR}
                    className="gradient-control-corner-buttons"
                >
                    <Button
                        element="button"
                        label="Top Left"
                        value={LINEAR_DIRECTION.TO_TOP_LEFT}
                        selected={angle === LINEAR_DIRECTION.TO_TOP_LEFT}
                    />
                    <Button
                        element="button"
                        label="Top Right"
                        value={LINEAR_DIRECTION.TO_TOP_RIGHT}
                        selected={angle === LINEAR_DIRECTION.TO_TOP_RIGHT}
                    />
                    <Button
                        element="button"
                        label="Bottom Left"
                        value={LINEAR_DIRECTION.TO_BOTTOM_LEFT}
                        selected={angle === LINEAR_DIRECTION.TO_BOTTOM_LEFT}
                    />
                    <Button
                        element="button"
                        label="Bottom Right"
                        value={LINEAR_DIRECTION.TO_BOTTOM_RIGHT}
                        selected={angle === LINEAR_DIRECTION.TO_BOTTOM_RIGHT}
                    />
                </ButtonGroup>
            </FieldLabel>
        </div>
    );
};

export default GradientControl;
