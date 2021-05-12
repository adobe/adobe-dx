import { getMarketoConfig, loadMarketoForm } from './shared';
import { initProfileService } from './profile';

/**
 * Initialize all Marketo components using the Marketo footer script.
 * Used for static publish views.
 */
const initAll = () => {
    const config = getMarketoConfig();
    if (config) {
        const idArray = JSON.parse(config.formIds);
        idArray.forEach((id) => {
            loadMarketoForm(config.url, config.munchkinId, id);
            if (!window.dx?.author) {
                initProfileService(config.url, config.munchkinId, config.profileUrl, id);
            }
        });
    }
};

initAll();
