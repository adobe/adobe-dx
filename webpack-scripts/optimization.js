const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
    usedExports: true,
    minimize: true,
    minimizer: [
        new TerserPlugin({
            terserOptions: {
                module: true,
            },
        }),
    ],
};
