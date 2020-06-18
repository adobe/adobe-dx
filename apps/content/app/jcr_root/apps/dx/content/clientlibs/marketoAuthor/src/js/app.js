import { getMarketoConfig, loadMarketoForm } from '../../../marketoPublish/src/js/shared';

/**
 * Initialize a single Marketo component using its DOM Element.
 * Used for authoring and Dynamic Experience Fragments
 * @param {HTMLElement} element
 */
const initSingle = (element) => {
    const marketoComponent = element.querySelector('.dx-Marketo');
    if (marketoComponent) {
        const id = marketoComponent.id.replace('mktoForm_', '');
        const config = getMarketoConfig();
        if (config) {
            loadMarketoForm(config.url, config.munchkinId, id);
        }
    }
};

if (window.dx) {
    window.dx.author.watch.registerFunction(initSingle);
}
