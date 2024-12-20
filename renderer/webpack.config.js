const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const { DefinePlugin } = require('webpack');
const path = require('path');

const makeConfig = (argv, { entry, out, target, library = 'commonjs' }) => ({
    mode: argv.mode,
    devtool: argv.mode === 'production' ? false : 'inline-source-map',
    entry,
    target,
    output: {
        path: path.join(__dirname, path.dirname(out)),
        filename: path.basename(out),
        publicPath: '',
        libraryTarget: library,
        chunkFormat: library,
    },
    externalsPresets: { node: true },
    externals: {
        vscode: 'commonjs vscode',

    },
    resolve: {
        extensions: ['.ts', '.tsx', '.js', '.jsx', '.css'],
        fallback: target === 'web' || target === 'webworker' ? {
            "fs": false,
            "path": require.resolve("path-browserify"),
            "readline": false,
            "os": require.resolve("os-browserify/browser"),
        } : {},
        // alias: {
        //     "react": "preact/compat",
        //     "react-dom/test-utils": "preact/test-utils",
        //     "react-dom": "preact/compat",     // Must be below test-utils
        //     "react/jsx-runtime": "preact/jsx-runtime"
        // },
    },
    experiments: {
        outputModule: target === 'web' ? true : false,
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                loader: 'ts-loader',
                options: {
                    configFile: path.join(path.dirname(entry), 'tsconfig.json'),
                    transpileOnly: true,
                    compilerOptions: {
                        noEmit: false,
                    },
                },
            },
            {
                test: /\.css$/,
                use: [
                    'style-loader',
                    {
                        loader: 'css-loader',
                        options: {
                            importLoaders: 1,
                            modules: true,
                        },
                    },
                ],
            },
            {
                test: /^node:/,
                loader: 'node-loader', // Use node-loader for node: prefixed modules
            },
        ],
    },
    plugins: [
        new ForkTsCheckerWebpackPlugin({
            typescript: {
                configFile: path.join(path.dirname(entry), 'tsconfig.json'),
            },
        }),
        new DefinePlugin({
            __webpack_relative_entrypoint_to_root__: JSON.stringify(
                path.posix.relative(path.posix.dirname(`/index.js`), '/'),
            ),
            scriptUrl: 'import.meta.url',
        }),
    ],
    infrastructureLogging: {
        level: "log",
    },
});

module.exports = (env, argv) => [
    makeConfig(argv, { entry: './src/index.tsx', out: '../src/main/resources/html/script.js', target: 'web', library: 'module' }),
];
