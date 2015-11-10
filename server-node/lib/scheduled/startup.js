var winston = require('winston');

module.exports = function () {
    require('./trade/autoReceiving')();
    require('./trade/notifyUnreads')();

    winston.info('Startup scheduled success');
};
