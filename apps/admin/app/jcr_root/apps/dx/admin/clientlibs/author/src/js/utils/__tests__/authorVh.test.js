import vhHtml from '../__mocks__/vh.html';
import {
    initAuthorVh,
    findAuthorVhElements,
    getEditorHeight,
    setVh,
    getInitialWidth,
    getVhAsPx,
    getBreakpointVh,
} from '../authorVh';

// Define our innerWidth
Object.defineProperties(window, {
    innerWidth: { value: 300, configurable: true, writable: true },
    innerHeight: { value: 720 },
});

document.body.innerHTML = vhHtml;

let elements;
let editorHeight;

beforeAll(() => {
    elements = findAuthorVhElements(document);
});

describe('count elements with VH', () => {
    test('four total elements', () => {
        expect(window.innerWidth).toEqual(300);
        expect(elements).toHaveLength(8);
    });
});

describe('get editor height', () => {
    test('height is height minus AEM toolbar', () => {
        editorHeight = getEditorHeight();
        expect(editorHeight).toEqual(610);
    });
});

describe('setting VH', () => {
    test('VH is correctly set on first element', () => {
        const mobileElement = setVh(elements[0], 'mobile', editorHeight);
        expect(mobileElement.style.minHeight).toEqual('122px');
    });
    test('bad data is not set on element', () => {
        const badElement = document.querySelector('.bad-element');
        expect(badElement.style.minHeight).toBeFalsy();
    });
});

describe('get initial width', () => {
    test('width should be mobile', () => {
        const initialWidth = getInitialWidth();
        expect(initialWidth).toEqual('mobile');
    });

    test('width should be tablet', () => {
        Object.defineProperties(window, {
            innerWidth: { value: 768, configurable: true, writable: true },
        });
        const initialWidth = getInitialWidth();
        expect(initialWidth).toEqual('tablet');
    });
    test('width should be desktop', () => {
        Object.defineProperties(window, {
            innerWidth: { value: 1366, configurable: true, writable: true },
        });
        const initialWidth = getInitialWidth();
        expect(initialWidth).toEqual('desktop');
    });
});

describe('get vh to px', () => {
    test('vh should not convert to px', () => {
        const pxValue = getVhAsPx(900, 'foo');
        expect(pxValue).toBeNull();
    });
    test('vh should convert to px', () => {
        const pxValue = getVhAsPx(900, 66);
        expect(pxValue).toEqual(594);
    });
});

describe('get breakpoint vh as px', () => {
    test('breakpoint vh should be mobile', () => {
        const el = elements[2];
        const pxValueMobile = getBreakpointVh('mobile', window.innerHeight, el.dataset);
        expect(pxValueMobile.flexVh).toEqual(144);
    });

    test('breakpoint vh should be tablet', () => {
        const el = elements[2];
        const pxValueMobile = getBreakpointVh('tablet', window.innerHeight, el.dataset);
        expect(pxValueMobile.flexVh).toEqual(288);
    });

    test('breakpoint vh should be desktop', () => {
        const el = elements[2];
        const pxValueMobile = getBreakpointVh('desktop', window.innerHeight, el.dataset);
        expect(pxValueMobile.flexVh).toEqual(432);
    });
});

describe('breakpoint vh with items defined', () => {
    test('mobile only', () => {
        const el = elements[4];
        const { flexVh, itemVhs } = getBreakpointVh('mobile', window.innerHeight, el.dataset);
        expect(flexVh).toEqual(144);
        expect(itemVhs).toEqual([720]);
    });

    test('tablet override', () => {
        const el = elements[5];
        const { flexVh, itemVhs } = getBreakpointVh('mobile', window.innerHeight, el.dataset);
        expect(flexVh).toEqual(144);
        expect(itemVhs).toEqual([144, null, 288]);

        const { flexVh: flexVh1, itemVhs: itemVhs1 } = getBreakpointVh(
            'tablet',
            window.innerHeight,
            el.dataset
        );
        expect(flexVh1).toEqual(288);
        expect(itemVhs1).toEqual([null, 108, 648]);
    });

    test('desktop override + item inheritance', () => {
        const el = elements[6];

        const item1 = el.querySelector('#i1');
        const item2 = el.querySelector('#i2');
        const item3 = el.querySelector('#i3');
        const item4 = el.querySelector('#i4');

        setVh(el, 'mobile', window.innerHeight, el.dataset);
        expect(item1.style.minHeight).toBe('');
        expect(item2.style.minHeight).toBe('');
        expect(item3.style.minHeight).toBe('712.8px');
        expect(item4.style.minHeight).toBe('216px');

        setVh(el, 'tablet', window.innerHeight, el.dataset);
        expect(item1.style.minHeight).toBe('');
        expect(item2.style.minHeight).toBe('108px');
        expect(item3.style.minHeight).toBe('712.8px');
        expect(item4.style.minHeight).toBe('288px');

        setVh(el, 'desktop', window.innerHeight, el.dataset);
        expect(item1.style.minHeight).toBe('72px');
        expect(item2.style.minHeight).toBe('108px');
        expect(item3.style.minHeight).toBe('712.8px');
        expect(item4.style.minHeight).toBe('288px');
    });
});

describe('initAuthorVh', () => {
    test('four total elements', () => {
        const addListener = jest.fn();
        global.matchMedia = () => ({ addListener });
        initAuthorVh(document);
        expect(addListener).toHaveBeenCalled();
    });
});
