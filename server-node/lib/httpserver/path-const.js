var path = require('path');

var publicPath = path.join(__dirname, '../../public');
var imagePath =  path.join(publicPath, '/images');
var videoPath = path.join(publicPath, '/videos');

module.exports = {
    publicPath : publicPath,
    imagePath : imagePath,
    videoPath : videoPath
};