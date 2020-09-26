const CORAL_INPUT_SELECTOR = '._coral-ColorInput';
const SELECTED_SELECTOR = '[selected]';

const handleChange = (target) => {
    const selectedItem = target.querySelector(SELECTED_SELECTOR);
    const input = target.nextElementSibling;
    if (selectedItem && target.value) {
        input.value = selectedItem.dataset.dxValue;
    } else {
        input.value = target.value;
    }
    return input.value;
};

const getColorfieldInput = (element) => {
    return element.querySelector(CORAL_INPUT_SELECTOR);
};

const caColorfields = (colorfields) => {
    colorfields.forEach((colorfield) => {
        getColorfieldInput(colorfield).addEventListener('change', (e) => {
            handleChange(e.target);
        });
    });
};

export { handleChange, getColorfieldInput, caColorfields };
