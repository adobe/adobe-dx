const loadMarketoForm = (url, munchkinId, id) => {
    window.MktoForms2.loadForm(url, munchkinId, id);
};

const getMarketoConfig = () => {
    const marketoFooterScript = document.querySelector('#dx-MarketoFooter-Script');
    if (marketoFooterScript) {
        const { url, munchkinId, formIds } = marketoFooterScript.dataset;
        if (url && munchkinId && formIds) {
            return { url, munchkinId, formIds };
        }
    }
    return null;
};

export { getMarketoConfig, loadMarketoForm };
