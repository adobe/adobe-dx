import getCsrf from './csrf';

async function deleteResource(path) {
    const formData = new FormData();
    formData.append(':operation', 'delete');

    const csrf = await getCsrf();

    return await (fetch(path, {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'CSRF-Token': csrf.token },
        body: formData
    }));
}

export default deleteResource;