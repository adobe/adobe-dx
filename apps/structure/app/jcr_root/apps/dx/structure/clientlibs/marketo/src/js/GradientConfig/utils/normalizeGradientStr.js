/**
 * Clean up a given gradient string so it can be processed by GradientConfig
 * @param {string} gradientString string to normalize
 */
const normalizeGradientStr = (gradientString) => {
    const gradient = gradientString.replace(';', '').replace('background-image:', '');
    const el = document.createElement('p');
    document.body.appendChild(el);
    el.style.backgroundImage = gradient;
    const gradientStr = getComputedStyle(el).backgroundImage;
    document.body.removeChild(el);
    return gradientStr !== 'none' ? gradientStr : false;
};

export default normalizeGradientStr;
