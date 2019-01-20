const path = require("path");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const CopyWebpackPlugin = require('copy-webpack-plugin');
const WebpackSourceMapSupport = require("webpack-source-map-support");

module.exports = {
    entry: {
        main: "./src/main.ts"
    },
    stats: {
        modules: false
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: "ts-loader",
                exclude: /node_modules/
            }
        ]
    },
    resolve: {
        extensions: [".ts", ".js"]
    },
    devtool: "inline-source-map",
    plugins: [
        new WebpackSourceMapSupport(),
        new CleanWebpackPlugin([
            path.resolve(__dirname, "dist")
        ]),
        new CopyWebpackPlugin([
            {
                from: "package.json",
                to: "package.json"
            },
            {
                from: "**/*",
                to: "report-template/",
                context: "../app/dist/runner-stats-app/"
            }
        ], {debug: "debug"})
    ],
    output: {
        filename: "[name].bundle.js",
        path: path.resolve(__dirname, "dist")
    },
    target: 'node',
    node: {
        __dirname: false,
        __filename: false,
    }
};
