/*
 *  Copyright 2019 Adobe
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

const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

const stats = require('./webpack.config/stats');

const projectName = 'dx';
const project = `./jcr_root/apps/${projectName}/config-manager/common/clientlibs`;

const isProduction = process.env.NODE_ENV === 'production';

if (isProduction) {
    console.log('Production Build');
}

const rules = [
    {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        enforce: 'pre',
        loader: 'eslint-loader',
        options: {
            failOnError: true,
        },
    },
    {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: {
            loader: 'babel-loader',
        },
    },
    {
        test: /\.(css|less)$/,
        use: [MiniCssExtractPlugin.loader, 'css-loader', 'less-loader'],
    },
];

if (!isProduction) {
    rules.push({
        test: /\.jsx?$/,
        exclude: /node_modules/,
        use: {
            loader: 'prettier-loader',
        },
    });
}

module.exports = {
    entry: {
        react: [`${project}/react/src/js/app.js`, `${project}/react/src/less/app.less`],
    },
    output: {
        path: `${__dirname}/jcr_root/apps/${projectName}/config-manager/common/clientlibs`,
        filename: '[name]/dist/js/app.min.js',
    },
    module: {
        rules,
    },
    optimization: {
        minimize: true,
        minimizer: [new TerserPlugin()],
    },
    plugins: [
        new webpack.DefinePlugin({
            'process.env.SCALE_MEDIUM': 'true',
            'process.env.SCALE_LARGE': 'false',
            'process.env.THEME_LIGHT': 'true',
            'process.env.THEME_LIGHTEST': 'true',
            'process.env.THEME_DARK': 'false',
            'process.env.THEME_DARKEST': 'false',
        }),
        new MiniCssExtractPlugin({ filename: '[name]/dist/css/app.min.css' }),
        new OptimizeCSSAssetsPlugin({}),
    ],
    stats,
};
