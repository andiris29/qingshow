var winston = require('winston');

module.exports = function () {
    require('./goblin-item/run')();
    
    winston.info('Startup scheduled success');
};
