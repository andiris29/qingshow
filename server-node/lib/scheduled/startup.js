var winston = require('winston');

module.exports = function () {
    require('./goblin-item/run')();
    require('./trade/autoReceiving')();
    
    winston.info('Startup scheduled success');
};
