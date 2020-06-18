import normalizeGradientStr from '../normalizeGradientStr';

describe('Normalize a gradient string', () => {
    test('should return empty string in jsdom implementation', () => {
        expect(normalizeGradientStr('asdf')).toBe('');
    });

    // TODO: Figure out how to get jsdom to compute backgroundImage properties
    /*
    test("should remove trailing semi-colons", () => {
        const gs =
            "linear-gradient(90deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95.0%);";
        expect(normalizeGradientStr(gs)).toBe(
            "linear-gradient(90deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95.0%)"
        );
    });

    test('should remove "background-image:" prefix', () => {
        const gs =
            "background-image: linear-gradient(90deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95.0%);";
        expect(normalizeGradientStr(gs)).toBe(
            "linear-gradient(90deg, rgba(0, 0, 0, 0.5) 0.1%,rgba(255, 0, 0, 1) 95.0%)"
        );
    });

    test('should convert all gradients to rgba', () => {
        const gs1 = 'linear-gradient(0deg,#f00,#00f);';
        expect(normalizeGradientStr(gs1)).toBe('linear-gradient(0deg, rgba(255, 0, 0, 1) 0.0%,rgba(0, 0, 255, 1) 100.0%)');

        const gs2 = 'radial-gradient(#e66465, #9198e5);'
        expect(normalizeGradientStr(gs2)).toBe('radial-gradient(circle at center, rgba(230, 100, 101, 1) 0.0%,rgba(145, 152, 229, 1) 100.0%)')
    });

    test('should return false for invalid gradient strings', () => {
        expect(normalizeGradientStr('not a valid linear-gradient')).toBe(false);
    });
    */
});
