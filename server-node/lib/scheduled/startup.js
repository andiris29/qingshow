var winston = require('winston');

module.exports = function () {
    require('./goblin-item/run')();
    require('./trade/autoReceiving')();
    require('./trade/notifyTradeInitialized')();
    
    winston.info('Startup scheduled success');
};
