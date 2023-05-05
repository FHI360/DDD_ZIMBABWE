
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const webpackMerge = require('webpack-merge').merge;
const commonConfig = require('./webpack.common');

module.exports = webpackMerge(commonConfig, {
    mode: 'production',
    output: {
        publicPath: '/js/impilo-gateway/'
    },
    plugins: [
        new MiniCssExtractPlugin({
            filename: "[name].css",
            chunkFilename: "[id].css",
            ignoreOrder: false,
        })
    ]
});

