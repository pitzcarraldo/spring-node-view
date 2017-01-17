module.exports = function render(model) {
  return { body: [JSON.stringify(model)] };
};
