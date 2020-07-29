window.dx = { configManager: { configs: {} } };

/**
 * Register a React component to be added to the list of available
 * ConfigManager apps.
 *
 * @param {string} name The key name for the app
 * @param {string} label The displayed label
 * @param {element} app The react component to render
 */
window.dx.configManager.registerApp = (name, label, app) => {
    window.dx.configManager.configs[name] = {
        label,
        app,
    };
};
