module.exports = function render(model, callback) {
    return setTimeout(function () {
        callback && callback({
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(model)
        });
    }, 1000);
};
