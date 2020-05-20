const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const miniCssExtractLoader = () => {
  return {
    test: /\.(css|less)$/,
    use: [MiniCssExtractPlugin.loader, "css-loader", "less-loader"],
  };
};

module.exports = {
  miniCssExtractLoader,
  MiniCssExtractPlugin,
};
