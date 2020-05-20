module.exports = {
  test: /\.jsx?$/,
  exclude: /node_modules/,
  use: {
    loader: "prettier-loader",
  },
};
