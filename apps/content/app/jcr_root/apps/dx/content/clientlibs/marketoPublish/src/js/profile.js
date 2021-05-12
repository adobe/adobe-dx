const urlSchemePattern = /(https?:)?\/\//;
const munchkinCookiePattern = /_mkto_trk=/;
const clsDxMarketoChecking = 'dx-Marketo--profiling';

/**
 * Retrieves the list of fields that are not filled yet for a given form.
 *
 * @param {string} endpoint URL of the middleware on adobe.io runtime
 * @param {string} baseUrl Marketo instance URL
 * @param {string} munchkinId Munchkin ID
 * @param {string} formId form ID in Marketo
 */
const getFieldsToFill = (endpoint, baseUrl, munchkinId, formId) =>
    window.fetch(`${endpoint}?baseUrl=${baseUrl}&munchkinId=${munchkinId}&formId=${formId}`);

const removeProfiling = (formEl) => formEl.classList.remove(clsDxMarketoChecking);

/**
 * Adds data-field-open attribute to fields that are not in fields argument.
 * @param {array} fields list of field names
 * @param {DOMElement} formEl a DOM element
 */
const toggleFormFields = (fields, formEl) => {
    const labels = formEl.querySelectorAll('.mktoFieldDescriptor label');
    for (let i = 0; i < labels.length; i += 1) {
        const label = labels[i];
        const labelFor = label.getAttribute('for');
        if (fields.indexOf(labelFor) < 0) {
            label.parentElement.parentElement.remove();
        }
    }
    removeProfiling(formEl);
};

/**
 * Updates the form submit text with the data-submit-text attribute value.
 * @param {DOMElement} formEl HTML Form element that is rendered for a Marketo form.
 */
const prepareFormToSubmit = (formEl) => {
    formEl.classList.add('readyToSubmit');
    const { submitText = '' } = formEl.dataset;
    if (!submitText) return;
    const button = formEl.querySelector('button[type="submit"]');
    if (!button) return;
    button.textContent = submitText;
};

/**
 * Initialise the marketo form given by the formId and hides already pre-filled fields.
 */
const initProfileService = (url, munchkinId, profileUrl, formId) => {
    const formEl = document.querySelector(`#mktoForm_${formId}[data-profile]`);
    if (!formEl) {
        return Promise.resolve();
    }
    if (!munchkinCookiePattern.test(document.cookie)) {
        // unkown visitor (no munchkin cookie).
        removeProfiling(formEl);
        return Promise.resolve();
    }
    const baseUrl = url.replace(urlSchemePattern, '');
    const profilePromise = getFieldsToFill(profileUrl, baseUrl, munchkinId, formId)
        .then((res) => res.json())
        .catch(() => {
            return { formId, error: true };
        });

    return new Promise((resolve) => {
        profilePromise.then((formFields) => {
            window.MktoForms2.whenReady((form) => {
                if (form.getFormElem().get(0) === formEl) {
                    const { destinationUrl } = formEl.dataset;
                    const { fields = [] } = formFields;
                    if (destinationUrl) {
                        form.onSuccess(() => {
                            if (fields.length > 0) {
                                window.location.assign(destinationUrl);
                            } else {
                                window.location.replace(destinationUrl);
                            }
                            return false; // stop default redirect.
                        });
                    }
                    if (formFields.error) {
                        removeProfiling(formEl);
                    } else {
                        toggleFormFields(fields, formEl);
                        if (fields.length === 0) {
                            prepareFormToSubmit(formEl);
                            if ('autoSubmit' in formEl.dataset) {
                                form.submit();
                            }
                        }
                    }
                    resolve();
                }
            });
        });
    });
};

// eslint-disable-next-line import/prefer-default-export
export { initProfileService };
