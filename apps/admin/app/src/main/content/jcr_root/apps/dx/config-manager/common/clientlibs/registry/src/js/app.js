///////// dx.configmanager.registry
window.dx = { configManager: { configs: { } } };

window.dx.configManager.registerApp = (name, label, app) => {
    window.dx.configManager.configs[name] = {
        label,
        app,
    }
}