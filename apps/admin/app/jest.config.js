// Ignore for code coverage
/* istanbul ignore file */

const config = {
    collectCoverageFrom: [
        '**/*.{js,jsx}',
        '!**/node_modules/**',
        '!**/dist/**',
        '!.history/**',
        '!webpack.config.js',
        '!webpack.config/**',
        '!**/js_reports/**',
    ],
    coverageDirectory: '<rootDir>/js_reports/coverage',
    projects: [
        {
            displayName: 'test',
            moduleNameMapper: {
                '\\.(css|less)$': '<rootDir>/.jestconfig/__mocks__/styleMock.js',
            },
            transform: {
                '\\.html$': 'jest-raw-loader',
                '^.+\\.js?$': './babelwrapper.js',
            },
            setupFilesAfterEnv: ['<rootDir>/.jestconfig/jest.setup.js'],
            testPathIgnorePatterns: ['/node_modules/', '/.history/'],
        },
    ],
    testResultsProcessor: 'jest-sonar-reporter',
};

module.exports = config;
