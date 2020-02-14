/*
 *  Copyright 2020 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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