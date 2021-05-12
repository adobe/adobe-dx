const loadMarketoForm = (url, munchkinId, id) => {
    window.MktoForms2.loadForm(url, munchkinId, id);
};

const getMarketoConfig = () => {
    const marketoFooterScript = document.querySelector('#dx-MarketoFooter-Script');
    if (marketoFooterScript) {
        const { url, munchkinId, formIds, profileUrl } = marketoFooterScript.dataset;
        if (url && munchkinId && formIds) {
            return { url, munchkinId, formIds, profileUrl };
        }
    }
    return null;
};

export { getMarketoConfig, loadMarketoForm };
