module.exports = () => {
  return process.env.NODE_ENV === "production"
    ? false
    : "eval-cheap-module-source-map";
};
