var argv = require('minimist')(process.argv.slice(2));
var express = require('express');
var path = require('path');

// Startup http server
var app = express();
app.listen(argv['web-server-port']);

//static
// /com.focosee.qingshow
var _root = path.join(__dirname, '../../');
app.use('/com.focosee.qingshow/web-mobile', express.static(path.join(_root, '/web-mobile')));
app.use('/com.focosee.qingshow/server-node-fake', express.static(path.join(_root, '/server-node-fake')));
app.use('/com.focosee.qingshow/server-image-fake', express.static(path.join(_root, '/server-image-fake')));
app.use('/com.focosee.qingshow/server-video-fake', express.static(path.join(_root, '/server-video-fake')));

console.log('Web server startup complete!');
