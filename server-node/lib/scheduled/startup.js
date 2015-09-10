var winston = require('winston');

module.exports = function (config) {
    require('./goblin/goblin-item/run')(config.goblin);
    require('./trade/autoReceiving')();
    require('./trade/notifyTradeInitialized')();
    
    winston.info('Startup scheduled success');
};
