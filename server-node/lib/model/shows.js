
var mongoose = require('mongoose');
var People = require('./peoples');
var Item = require('./items');

var path = require('path');
var pathConst = require('../httpserver/path-const');
var gm = require('gm').subClass({ imageMagick: true });


var Schema = mongoose.Schema;
var showSchema;
showSchema = Schema({
    name: String,
    cover: String,
    coverMetaData: {
        cover: String,
        width: Number,
        height: Number
    },
    video: String,
    posters: [String],
    numLike: Number,
    numView: Number,
    tags: [String],
    modelRef: {type: Schema.Types.ObjectId, ref: 'peoples'},
    itemRefs: {
        type: [
            {type: Schema.Types.ObjectId, ref: 'items'}
        ]
//        select: false
    },
    discountInfo: {
        start : Date,
        end : Date
    },
    create: { type: Date, default: Date.now }
});

showSchema.methods.updateCoverMetaData = function (callBack){
    if (!this.cover.length || (this.coverMetaData && (this.cover === this.coverMetaData.cover))) {
        callBack();
        return;
    }
    var _this = this;
    var imagePath = path.join(pathConst.image, this.cover);
    var g = gm(imagePath);

    gm(imagePath).size(function (err, size) {
        _this.coverMetaData = {};
        _this.coverMetaData.cover = _this.cover;
        if (size) {
            _this.coverMetaData.width = size.width;
            _this.coverMetaData.height = size.height;
            _this.save(function (err, image) {
                callBack();
            });
        } else {
            callBack();
        }

    });

};
var Show = mongoose.model('shows', showSchema);
module.exports = Show;