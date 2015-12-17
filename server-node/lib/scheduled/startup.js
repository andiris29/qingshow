var winston = require('winston');

module.exports = function () {
    require('./trade/autoReceiving')();
    require('./user/notifyUnreads')();
    require('./show/switchReductionEnabled')();
    require('./show/dispatchShowBonus')();
    require('./item/cleanSyncStartAt')();

    winston.info('Startup scheduled success');
};
