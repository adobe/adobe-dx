import { queryByText } from '@testing-library/dom';
import '@testing-library/jest-dom/extend-expect';
import { initProfileService } from '../profile';

const { location } = global;
delete global.location;
global.location = { ...location, replace: jest.fn(), assign: jest.fn() };

const fields = ['FirstName', 'LastName', 'Email', 'Company'];
const fieldsEls = fields
    .map(
        (field) => `
<div class="mktoFormRow">
  <div class="mktoFieldDescriptor">
    <div class="mktoFieldWrap mktoRequiredField">
      <label for="${field}" id="Lbl${field}"> ${field} label:</label>
      <input
        id="${field}"
        name="${field}"
        maxlength="255"
        aria-labelledby="Lbl${field} Instruct${field}"
      />
    </div>
  </div>
</div>
`
    )
    .join('\n');

const url = '//app-test.marketo.com/';
const munchkinId = '123-abc-456';
const formId = 1234;
const profileUrl = 'http://localhost:1234';
const destinationUrl = '/dest.html';
const submitText = 'Click to access the asset';

const setCookie = (exprInDays) => {
    const date = new Date();
    date.setTime(date.getTime() + exprInDays * 24 * 60 * 60 * 1000);
    document.cookie = `_mkto_trk=xyz; expires=${date.toGMTString()}`;
};

const setDom = (profile = false, autoSubmit = false, cookieExpr = 1) => {
    document.body.innerHTML = `
<div>
  <div class="marketo">
    <form
      class="dx-Marketo ${profile && 'dx-Marketo--profiling'}"
      id="mktoForm_1234"
      ${profile && 'data-profile'}
      ${autoSubmit && 'data-auto-submit'}
      data-submit-text="${submitText}"
      data-destination-url="${destinationUrl}"
    >
    ${fieldsEls}
    <div class="mktoButtonRow"><span class="mktoButtonWrap"><button type="submit" class="mktoButton">Submit</button></span></div>
    </form>
    <script
      id="dx-MarketoFooter-Script"
      data-url="${url}"
      data-munchkin-id="${munchkinId}"
      data-form-ids="[${formId}]"
      data-profileUrl="${profileUrl}"
      src="//app-test.marketo.com//js/forms2/js/forms2.js"
    ></script>
  </div>
</body>
`;
    setCookie(cookieExpr);
};

const doProfile = () => initProfileService(url, munchkinId, profileUrl, formId);

describe('Marketo profile Forms', () => {
    let onSuccessCb;
    const submitFn = jest.fn(() => setTimeout(onSuccessCb(), 10));

    beforeEach(() => {
        jest.clearAllMocks();
        window.MktoForms2 = {
            whenReady: (cb) =>
                setTimeout(
                    cb({
                        onSuccess: (osCb) => {
                            onSuccessCb = osCb;
                        },
                        getId: () => 1234,
                        getFormElem: () => ({
                            get: () => document.querySelector('form'),
                        }),
                        submit: submitFn,
                    }),
                    10
                ),
        };
    });

    test('Form without profile service', async () => {
        setDom();
        expect(document.querySelector('[data-profile]')).toBeNull();
        await doProfile();
        expect(fetch.mock.calls.length).toEqual(0);
        expect(document.querySelector('[data-field-open]')).toBeNull();
    });

    test('Form with profile service and no field is pre-filled', async () => {
        setDom(true);
        fetch.mockResponseOnce(JSON.stringify({ fields }));
        await doProfile();

        expect(fetch.mock.calls.length).toEqual(1);
        expect(document.querySelectorAll('.mktoFieldDescriptor').length).toEqual(4);
    });

    test('Form with profile service and some fields are pre-filled', async () => {
        setDom(true);
        fetch.mockResponseOnce(JSON.stringify({ fields: ['FirstName', 'LastName'] }));
        await doProfile();

        expect(fetch.mock.calls.length).toEqual(1);
        expect(document.querySelectorAll('.mktoFieldDescriptor').length).toEqual(2);
    });

    test('Form with profile service and all the fields are pre-filled', async () => {
        setDom(true);
        fetch.mockResponseOnce(JSON.stringify({ fields: [] }));
        await doProfile();

        expect(fetch.mock.calls.length).toEqual(1);
        expect(document.querySelectorAll('.mktoFieldDescriptor').length).toEqual(0);
        expect(queryByText(document, submitText)).toBeTruthy();
    });

    test('Form with profile service and auto submit is enabled', async () => {
        setDom(true, true);
        fetch.mockResponseOnce(JSON.stringify({ fields: ['FirstName', 'LastName'] }));
        await doProfile();

        expect(fetch.mock.calls.length).toEqual(1);
        expect(document.querySelectorAll('.mktoFieldDescriptor').length).toEqual(2);
        expect(submitFn.mock.calls.length).toEqual(0);
    });

    test.only('Form with profile service, all the fields are pre-filled and auto submit is enabled', async () => {
        setDom(true, true);
        fetch.mockResponseOnce(JSON.stringify({ fields: [] }));
        await doProfile();

        expect(fetch.mock.calls.length).toEqual(1);
        expect(document.querySelectorAll('.mktoFieldDescriptor').length).toEqual(0);
        expect(submitFn.mock.calls.length).toEqual(1);
        expect(window.location.replace).toBeCalledWith(destinationUrl);
        expect(window.location.assign.mock.calls.length).toEqual(0);
    });

    test('Form with profile service, first visit (no munckin cookie)', async () => {
        setDom(true, true, 0);
        await doProfile();
        expect(document.querySelector('.dx-Marketo--profiling')).toBeNull();
        expect(fetch.mock.calls.length).toEqual(0);
        expect(submitFn.mock.calls.length).toEqual(0);
    });

    test('marketo profile profiling service request fails', async () => {
        setDom(true, true);
        fetch.mockReject(new Error('Service unreachable'));
        await doProfile();
        expect(fetch.mock.calls.length).toEqual(1);
        expect(submitFn.mock.calls.length).toEqual(0);
        expect(document.querySelector('.dx-Marketo--profiling')).toBeNull();
    });
});
