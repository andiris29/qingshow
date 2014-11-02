var path = require('path');

// /com.focosee.qingshow
var _root = path.join(__dirname, '../../../');
var webMobile = path.join(_root, '/web-mobile');
// TODO Move to formal path
var image = path.join(_root, '/server-image-fake');

module.exports = {
    webMobile : webMobile,
    image : image
};
