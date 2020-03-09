import DataSource from '../DataSource';

window.fetch = jest.fn().mockImplementation((path) => Promise.resolve({
    json: () => Promise.resolve({'items': ['one', 'two']}),
}));

describe('Datasource', () => {
    test('should fetch datasourcepath with getTree', async () => {
        const cds = new DataSource('path/to/datasource');
        const items = await cds.getTree();
        expect(items).toEqual(['one', 'two']);
        expect(window.fetch).toHaveBeenCalledWith('path/to/datasource.model.json');
    });
});
