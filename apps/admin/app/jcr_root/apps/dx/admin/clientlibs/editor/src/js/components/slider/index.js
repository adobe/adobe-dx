/* istanbul ignore file */
const CORAL_SLIDER = 'coral-slider';
const SLIDER_WRAPPER = '.dexter-Form-fieldwrapper.dexter-Form-fieldwrapper--slider';
const DX_TOOLTIP = '.dexter-SliderTooltip';
const DX_TOOLTIP_ENABLED = 'dexter-SliderTooltip--enabled';
const DX_HIDDEN_FIELD = '.dexter-SliderHiddenField';

export default class RangeSlider {
    constructor($dialog) {
        // Setup event handlers for the coral slider change
        const $coralSliderWrappers = $dialog.find(SLIDER_WRAPPER);
        Array.from($coralSliderWrappers).forEach((wrapper) => {
            this.setupSlider(wrapper);
        });
    }

    setupSlider(wrapper) {
        const $slider = $(wrapper).find(CORAL_SLIDER).get(0);

        const tooltip = wrapper.querySelector(DX_TOOLTIP);
        const hiddenField = wrapper.querySelector(DX_HIDDEN_FIELD);

        if (hiddenField) {
            // Setup a disabled slider
            if (!hiddenField.value) {
                $slider.disabled = true;
            }

            // Setup an enabled slider
            if (hiddenField.value) {
                tooltip.innerHTML = `${hiddenField.value}${tooltip.dataset.buttonSuffix}`;
                tooltip.classList.add(DX_TOOLTIP_ENABLED);
                wrapper.removeChild(hiddenField);
            }

            $slider.on('change', (e) => {
                tooltip.innerHTML = `${e.target.value}${tooltip.dataset.buttonSuffix}`;
            });

            tooltip.addEventListener('click', () => {
                // Enable the slider
                if ($slider.disabled) {
                    $slider.disabled = false;
                    // Reset to the dialog's default value
                    $slider.value = $slider.dataset.defaultValue;
                    tooltip.innerHTML = `${$slider.value}${tooltip.dataset.buttonSuffix}`;
                    wrapper.removeChild(hiddenField);
                    tooltip.classList.add(DX_TOOLTIP_ENABLED);
                } else {
                    // Disable the slider
                    $slider.disabled = true;
                    $slider.value = 0;
                    tooltip.classList.remove(DX_TOOLTIP_ENABLED);
                    wrapper.appendChild(hiddenField);
                    hiddenField.removeAttribute('value');
                    tooltip.innerHTML = tooltip.dataset.disabledText;
                }
            });
        }
    }
}
