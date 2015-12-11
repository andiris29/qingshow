var winston = require('winston');

module.exports = function () {
    require('./trade/autoReceiving')();
    require('./user/notifyUnreads')();
    require('./show/switchReductionEnabled')();
    require('./show/dispatchShowBonus')();

    winston.info('Startup scheduled success');
};
