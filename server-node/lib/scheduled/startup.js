var winston = require('winston');

module.exports = function (config) {
    //require('./goblin/goblin-item/run')(config.goblin);
    require('./trade/autoReceiving')();
    require('./trade/notifyTradeInitialized')();

    require('./goblin/scheduler/GoblinScheduler').start();
    require('./goblin/slaver/GoblinMainSlaver').start();

    winston.info('Startup scheduled success');
};
