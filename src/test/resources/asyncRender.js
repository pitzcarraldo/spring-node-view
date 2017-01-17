module.exports = function render(model, callback) {
  setTimeout(function () {
        callback && callback(JSON.stringify(model));
    }, 1000);
};
