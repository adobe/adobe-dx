const config = {
    collectCoverageFrom: [
        '**/*.{js,jsx}',
        '!**/node_modules/**',
        '!**/dist/**',
        '!.history/**',
        '!webpack.config.js',
        '!webpack.config/**',
        '!**/js_reports/**',
        '!./*.js',
    ],
    coverageDirectory: '<rootDir>/js_reports/coverage',
    projects: [
        {
            displayName: 'test',
            moduleNameMapper: {
                '\\.(css|less)$': '<rootDir>/.jestconfig/__mocks__/styleMock.js',
            },
            transform: {
                '^.+\\.js?$': './babelwrapper.js',
            },
            setupFilesAfterEnv: ['<rootDir>/.jestconfig/jest.setup.js'],
            testPathIgnorePatterns: ['/node_modules/', '/.history/'],
        },
    ],
    testResultsProcessor: 'jest-sonar-reporter',
    testEnvironment: 'jest-environment-jsdom-global',
};

module.exports = config;
