var argv = require('minimist')(process.argv.slice(2));
var express = require('express');
var path = require('path');

// Startup http server
var app = express();
app.listen(argv['web-server-port']);

//static
var _root = path.join(__dirname, '../../');
app.use('/web-mobile', express.static(path.join(_root, '/web-mobile')));
app.use('/server-node-fake', express.static(path.join(_root, '/server-node-fake')));
app.use('/server-image-fake', express.static(path.join(_root, '/server-image-fake')));
app.use('/server-video-fake', express.static(path.join(_root, '/server-video-fake')));

console.log('Web server startup complete!');
