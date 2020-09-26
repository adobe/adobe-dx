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

// NPM Imports
const webpack = require('webpack');

// Mono Imports
const monoRoot = '../../../';
const spectrumConfig = require(`${monoRoot}webpack-scripts/spectrum.config.js`);
const eslintLoader = require(`${monoRoot}webpack-scripts/eslintLoader.js`);
const babelLoader = require(`${monoRoot}webpack-scripts/babelLoader.js`);
const prettierLoader = require(`${monoRoot}webpack-scripts/prettierLoader.js`);
const {
    miniCssExtractLoader,
    MiniCssExtractPlugin,
} = require(`${monoRoot}webpack-scripts/miniCssExtractWrapper.js`);
const optimization = require(`${monoRoot}webpack-scripts/optimization.js`);
const optimizeCssAssets = require(`${monoRoot}webpack-scripts/optimizeCssAssetsWrapper.js`);
const devtool = require(`${monoRoot}webpack-scripts/devtool.js`);
const performance = require(`${monoRoot}webpack-scripts/performance.js`);
const stats = require(`${monoRoot}webpack-scripts/stats.js`);

// Project Setup
const PROJECT_NAME = 'dx/structure';
const PROJECT_PATH = `${__dirname}/jcr_root/apps/${PROJECT_NAME}/clientlibs`;

// Production Detection
const isProduction = process.env.NODE_ENV === 'production';

// Rules
const rules = [eslintLoader, babelLoader, miniCssExtractLoader()];
if (!isProduction) {
    rules.push(prettierLoader);
}

module.exports = {
    entry: {
        configs: [
            `${PROJECT_PATH}/configs/src/js/app.js`,
            `${PROJECT_PATH}/configs/src/less/app.less`,
        ],
        author: [
            `${PROJECT_PATH}/author/src/js/app.js`,
            `${PROJECT_PATH}/author/src/less/app.less`,
        ],
        editor: [
            `${PROJECT_PATH}/editor/src/js/app.js`,
            `${PROJECT_PATH}/editor/src/less/app.less`,
        ],
        publish: [
            `${PROJECT_PATH}/publish/src/js/app.js`,
            `${PROJECT_PATH}/publish/src/less/app.less`,
        ],
    },
    output: {
        path: `${PROJECT_PATH}`,
        filename: '[name]/dist/js/app.min.js',
    },
    module: { rules },
    externals: {
        react: 'React',
        'react-dom': 'ReactDOM',
    },
    devtool: devtool(),
    plugins: [
        new webpack.DefinePlugin(spectrumConfig),
        new MiniCssExtractPlugin({ filename: '[name]/dist/css/app.min.css' }),
        optimizeCssAssets,
    ],
    performance,
    stats,
};

if (isProduction) {
    module.exports.optimization = optimization;
}
