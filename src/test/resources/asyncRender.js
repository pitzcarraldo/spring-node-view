module.exports = function render(model, callback) {
  return setTimeout(function () {
    callback && callback(JSON.stringify(model));
  }, 1000);
};
