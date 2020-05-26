import React from 'react';
import Well from '@react/react-spectrum/Well';

export default function GradientDocumentation() {
    return (
        <Well className="well-docs">
            <p>
                The gradient direction can be changed by rotating the white angle line, for finer
                control hold &quot;Ctrl&quot; after clicking to move in smaller increments. For
                radial gradients use the dots to specify placement.
            </p>
            <p>
                New gradient points can be added by clicking on the bottom gradient bar. Gradient
                points can be removed by dragging them off the bar.
            </p>
            <p>
                Corner gradients are slightly different than angles. A <em>Top Right</em> corner
                gradient on a square will match 45deg, but will not on a rectangle. See{' '}
                <a href="https://medium.com/@patrickbrosset/do-you-really-understand-css-linear-gradients-631d9a895caf">
                    here
                </a>{' '}
                for a very detailed explanation.
            </p>
            <p>
                The <em>CSS</em> field is interactive and can be modified. This allows for the use
                of other <a href="https://cssgradient.io/">gradient creators</a> or{' '}
                <a href="https://cssgradient.io/gradient-backgrounds/">pickers</a> and pasting in
                the css. The gradient will update when leaving the field.
            </p>
            <p>Here are a few sample gradients (copy and paste into the CSS field):</p>
            <p>
                <code>linear-gradient(red, orange, yellow, green, blue)</code>
            </p>
            <p>
                <code>
                    linear-gradient(to right, red 20%, orange 20% 40%, yellow 40% 60%, green 60%
                    80%, blue 80%);
                </code>
            </p>
        </Well>
    );
}
