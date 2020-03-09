import React from 'react';
import { render } from '@testing-library/react';
import ColumnItem from '../columnItem';

jest.mock('@react/react-spectrum/Icon/Folder', () => () => <div data-testid="folderIcon"/>);
jest.mock('@react/react-spectrum/Icon/Settings', () => () => <div data-testid="settingsIcon"/>);

describe('Icon Type', () => {
    test('should show a folder icon with label when iconType is folder', () => {
        const { getByTestId, getByText } = render(<ColumnItem iconType="folder" label="myFolder" />);
        expect(getByText('myFolder')).toHaveClass('dx-ColumnItemLabel');
        expect(getByTestId('folderIcon')).toBeInTheDocument();
    });

    test('should default to a settings icon with label when iconType is not specified', () => {
        const { getByTestId, getByText } = render(<ColumnItem label="mySettings" />);
        expect(getByText('mySettings')).toHaveClass('dx-ColumnItemLabel');
        expect(getByTestId('settingsIcon')).toBeInTheDocument();
    });
});
