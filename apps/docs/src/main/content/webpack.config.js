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

const projectName = 'dx-docs';
const project = `./jcr_root/apps/${projectName}/clientlibs`;

module.exports = {
    entry: {
        configs: [
            `${project}/configs/src/js/app.js`,
        ],
    },
    output: {
        path: `${__dirname}/jcr_root/apps/${projectName}/clientlibs`,
        filename: '[name]/dist/js/app.min.js',
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader'
                }
            },
            {
                test: /\.(css|less)$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader',
                    'less-loader',
                ],
            },
        ],
    },
    plugins: [
        new webpack.DefinePlugin({
            'process.env.SCALE_MEDIUM': 'true',
            'process.env.SCALE_LARGE': 'false',
            'process.env.THEME_LIGHT': 'true',
            'process.env.THEME_LIGHTEST': 'true',
            'process.env.THEME_DARK': 'false',
            'process.env.THEME_DARKEST': 'false'
        }),
        new MiniCssExtractPlugin({ filename: '[name]/dist/css/app.min.css' }),
        new OptimizeCSSAssetsPlugin({}),
    ],
    devtool: 'eval-cheap-module-source-map',
    stats,
};