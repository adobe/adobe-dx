module.exports = {
  test: /\.jsx?$/,
  exclude: /node_modules/,
  enforce: "pre",
  loader: "eslint-loader",
  options: {
    failOnError: true,
  },
};
