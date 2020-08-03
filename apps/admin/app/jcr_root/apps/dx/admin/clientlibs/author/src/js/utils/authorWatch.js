window.dx = window.dx || {};
window.dx.author = { watch: { functions: [] } };

window.dx.author.watch.registerFunction = (func) => {
    window.dx.author.watch.functions.push(func);
};

const TAG_SCRIPT = 'SCRIPT';
const WATCH_CONFIG = {
    childList: true,
    subtree: true,
};

/**
 * Watch for Author mutations and run functions when they meet node type criteria.
 *
 * @param {Array} apps The functions or classes to instantiate when a mutation occurs
 * @param {DOMElment} parent The top level parent to start the observation
 */
const watch = (document) => {
    const parentToWatch = document.querySelector('body');

    const callback = (mutationsList) => {
        mutationsList.forEach((mutation) => {
            // Attempt to cut down on noise from all mutations.
            // An AEM component mutation will have only one added node. No more. No less.
            if (window.dx.author.watch.functions.length > 0 && mutation.addedNodes.length === 1) {
                const addedNode = mutation.addedNodes[0];
                if (addedNode.nodeType === 1 && addedNode.tagName !== TAG_SCRIPT) {
                    // Loop through each function and instantiate
                    // it with the added node.
                    window.dx.author.watch.functions.forEach((app) => {
                        app(addedNode);
                    });
                }
            }
        });
    };

    // Create an observer instance linked to the callback function
    const observer = new MutationObserver(callback);
    observer.observe(parentToWatch, WATCH_CONFIG);
};

const authorWatch = (document) => {
    if (typeof CQ !== 'undefined' && document) {
        watch(document);
    }
};

export default authorWatch;
