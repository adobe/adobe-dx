import { ColumnViewDataSource } from '@react/react-spectrum/ColumnView';

export default class ConfigDataSource extends ColumnViewDataSource {
    constructor(dataSourcePath) {
        super();
        this.dataSourcePath = dataSourcePath;
    }

    async getChildren(item) {
        if (!item) {
            return this.getTree();
        }
        return item.children;
    }

    hasChildren(item) {
        return !!item.children;
    }

    isItemEqual(a, b) {
        return a.label === b.label;
    }

    async getTree() {
        const response = await (await (
            fetch(`${this.dataSourcePath}.model.json`).then(res => {
                return res.json();
            }).catch(err => {
                console.log('Error: ', err);
            })
        ));
        return response.items;
    }
}