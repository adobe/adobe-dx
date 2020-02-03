
const GRANITE_CSRF_ENDPOINT = '/libs/granite/csrf/token.json';

async function getCsrf() {
    return await (
        fetch(GRANITE_CSRF_ENDPOINT).then(res => {
            return res.json();
        }).catch(err => {
            console.log('Error: ', err);
        })
    );
}

export default getCsrf;