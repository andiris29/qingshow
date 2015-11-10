var winston = require('winston');

module.exports = function () {
    require('./trade/autoReceiving')();
    require('./user/notifyUnreads')();

    winston.info('Startup scheduled success');
};
