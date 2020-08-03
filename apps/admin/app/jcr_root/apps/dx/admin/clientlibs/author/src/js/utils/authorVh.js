/**
 * Author VH
 * A utility to set VH values as pixels in authoring mode (editor.html)
 * This is needed because editor.html's iframe cannot handle VH.
 *
 * This utility supports:
 * 1) Initial load of page
 * 2) Changing the mobile emulator width
 * 3) Saving dialog and adding new components (through AuthorWatch)
 */

// Setup Size Names
const DESKTOP = 'desktop';
const TABLET = 'tablet';
const MOBILE = 'mobile';

// Setup Sizing
const MOBILE_MAX = 599;
const TABLET_MIN = 600;
const TABLET_MAX = 1199;
const DESKTOP_MIN = 1200;

// Setup Media Queries
const MOBILE_QUERY = `(max-width: ${MOBILE_MAX}px)`;
const TABLET_QUERY = `(min-width: ${TABLET_MIN}px) and (max-width: ${TABLET_MAX}px)`;
const DESKTOP_QUERY = `(min-width: ${DESKTOP_MIN}px)`;

const AUTHOR_VH_SELECTOR = '.has-AuthorVh';
const FLEX_CLASS = 'flex';
const FLEX_CONTAINER_SELECTOR = '.dexter-FlexContainer';
const AEM_TOOLBAR_HEIGHT = 110;

/**
 * Get the equivalent px value for a vh value
 * @param {number} viewHeight - The current window height in px
 * @param {number} vhValue - the vh height to convert to px
 * @returns {number} vhValue in px
 */
const getVhAsPx = (viewHeight, vhValue) => {
    if (!(vhValue > 0)) return null;
    const vhDecimal = vhValue / 100;
    const pixelHeight = viewHeight * vhDecimal;
    return pixelHeight;
};

const mergeArrays = (arr, mergeArr) => {
    if (!arr || arr.length === 0) return mergeArr ? [...mergeArr] : [];
    if (!mergeArr || mergeArr.length === 0) return arr ? [...arr] : [];

    let merged = [...arr].map((val, i) => {
        if (!(val || val === 0) && mergeArr[i]) {
            return mergeArr[i];
        }
        return val;
    });

    if (merged.length < mergeArr.length) {
        // add addtional mergeArr entries to the end of merged
        merged = [...merged, ...mergeArr.slice(merged.length)];
    }

    return merged;
};

/**
 * Get the equivalent px value for a vh value
 * Note that this is a curried function requiring viewHeight first
 * @param {number} viewHeight - The current window height in px
 * @param {string]} vhs - A comma delimited string of vhs to convert to px
 * @returns {array[number]} array of vhs in px
 */
const getVhPxArray = (mediaName, viewHeight, dataset) => {
    // If any array slots are empty, need to inherit the values from smaller screen size if defined
    const mobile = dataset.authorMobileItemsVh && dataset.authorMobileItemsVh.split(',');
    const tablet = dataset.authorTabletItemsVh && dataset.authorTabletItemsVh.split(',');
    const desktop = dataset.authorDesktopItemsVh && dataset.authorDesktopItemsVh.split(',');

    let vhValues = [];
    switch (mediaName) {
        case DESKTOP:
            vhValues = mergeArrays(desktop, mergeArrays(tablet, mobile));
            break;
        case TABLET:
            vhValues = mergeArrays(tablet, mobile);
            break;
        default:
            vhValues = mobile;
    }

    return vhValues && vhValues.map((val) => getVhAsPx(viewHeight, val));
};

const breakpointSwitch = (mediaName, viewHeight, dataset) => {
    let vhValue;
    switch (mediaName) {
        case DESKTOP:
            vhValue =
                getVhAsPx(viewHeight, dataset.authorDesktopVh) ||
                getVhAsPx(viewHeight, dataset.authorTabletVh) ||
                getVhAsPx(viewHeight, dataset.authorMobileVh);
            break;
        case TABLET:
            vhValue =
                getVhAsPx(viewHeight, dataset.authorTabletVh) ||
                getVhAsPx(viewHeight, dataset.authorMobileVh);
            break;
        default:
            vhValue = getVhAsPx(viewHeight, dataset.authorMobileVh);
    }
    return vhValue;
};

const getBreakpointVh = (mediaName, viewHeight, dataset) => ({
    flexVh: breakpointSwitch(mediaName, viewHeight, dataset),
    itemVhs: getVhPxArray(mediaName, viewHeight, dataset),
});

const getInitialWidth = () => {
    const { innerWidth } = window;
    if (innerWidth >= DESKTOP_MIN) {
        return DESKTOP;
    }
    if (innerWidth >= TABLET_MIN && innerWidth <= TABLET_MAX) {
        return TABLET;
    }
    return MOBILE;
};

/**
 * Get the parent window height because AEM's content editor
 * iframe height can change often.
 */
const getEditorHeight = () => window.parent.innerHeight - AEM_TOOLBAR_HEIGHT;

const findAuthorVhElements = (el) => {
    if (el) {
        const flexContainer =
            el === document || el.classList.contains(FLEX_CLASS)
                ? el
                : el.closest(FLEX_CONTAINER_SELECTOR);
        return flexContainer ? flexContainer.querySelectorAll(AUTHOR_VH_SELECTOR) : [];
    }
    return [];
};

/**
 * Set the element VH
 * @param {HTMLElement} element The element you want to change.
 * @param {String} mediaName the media breakpoint name.
 * @param {Number} viewHeight the view height.
 */
const setVh = (element, mediaName, viewHeight) => {
    const { flexVh, itemVhs } = getBreakpointVh(mediaName, viewHeight, element.dataset);

    if (flexVh) {
        element.style.minHeight = `${flexVh}px`;
    }

    if (itemVhs && itemVhs.length) {
        const children = [...element.children];
        children.forEach((el, i) => {
            if (el.nodeName === 'CQ') return;

            if (el.classList.contains('newpar')) {
                el.style.minHeight = '0';
            } else if (itemVhs[i]) {
                el.style.minHeight = `${itemVhs[i]}px`;
            } else {
                el.style.minHeight = '';
            }
        });
    }
    return element;
};

const loopThroughVhElements = (elements, mediaName) => {
    const viewHeight = getEditorHeight();
    elements.forEach((element) => {
        setVh(element, mediaName, viewHeight);
    });
};

const setupMediaListeners = () => {
    const mediaMatches = [
        { mediaName: MOBILE, mediaQuery: window.matchMedia(MOBILE_QUERY) },
        { mediaName: TABLET, mediaQuery: window.matchMedia(TABLET_QUERY) },
        { mediaName: DESKTOP, mediaQuery: window.matchMedia(DESKTOP_QUERY) },
    ];

    mediaMatches.forEach((match) => {
        match.mediaQuery.addListener((event) => {
            if (event.matches) {
                // Re-find any elments that may have been added
                // since we initially setup the listener.
                const elements = findAuthorVhElements(document);
                loopThroughVhElements(elements, match.mediaName);
            }
        });
    });
};

const initAuthorVh = (el) => {
    const elements = findAuthorVhElements(el);
    if (elements.length > 0) {
        // Run on first load of page / element
        loopThroughVhElements(elements, getInitialWidth());

        // AuthorWatch may pass in a single component as the parent so only setup
        // media query listeners if the passed in element is a document.
        if (el === document) {
            setupMediaListeners();
        }
    }
};

export {
    initAuthorVh,
    findAuthorVhElements,
    setVh,
    getEditorHeight,
    getInitialWidth,
    getBreakpointVh,
    getVhAsPx,
    getVhPxArray,
};
