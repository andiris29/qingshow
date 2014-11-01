var path = require('path');

// /com.focosee.qingshow
var publicPath = path.join(__dirname, '../../../');
var imagePath =  path.join(publicPath, '/server-image-fake');
var videoPath = path.join(publicPath, '/server-video-fake');

module.exports = {
    publicPath : publicPath,
    imagePath : imagePath,
    videoPath : videoPath
};