module.exports = function render(model) {
  return {
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(model)
  };
};
