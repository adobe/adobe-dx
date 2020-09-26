// Ignore for code coverage
/* istanbul ignore file */

import RangeSlider from './components/slider';
import { caColorfields } from './components/caColorfield';

(function init($, Granite, $document) {
    $document.on('dialog-loaded', (e) => {
        const editorNamespace = {};

        // Range Slider
        editorNamespace.rangeSlider = new RangeSlider(e.dialog);

        // CA-Colorfield
        const colorfields = e.dialog[0].querySelectorAll('.dx-CA-Colorfield');
        caColorfields(colorfields);
    });
})(jQuery, Granite, jQuery(document));
