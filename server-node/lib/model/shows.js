
var mongoose = require('mongoose');
var People = require('./peoples');
var Item = require('./items');

var path = require('path');
var gm = require('gm').subClass({ imageMagick: true });
var http = require('http');


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
    create: { type: Date, 'default': Date.now }
});

showSchema.methods.updateCoverMetaData = function (callBack){
    if (!this.cover.length || (this.coverMetaData && (this.cover === this.coverMetaData.cover))) {
        callBack();
        return;
    }
    var _this = this;
    var defaultMetadata = {
        cover : "",
        width : 145,
        height : 270
    };
    http.get(this.cover, function (res) {
        res.on('data', function (buf) {
            gm(buf).size(function (err, size) {
                if (size) {
                    _this.coverMetaData = {
                        cover : _this.cover,
                        width : size.width,
                        height : size.height
                    };
                } else {
                    _this.coverMetaData = defaultMetadata;
                }
                _this.save(function (err, image) {
                    callBack();
                });
            });
        });
    })
        .on('error', function (e) {
            //http request error, use default server metadata
            _this.coverMetaData = defaultMetadata;
            _this.save(function (err, image) {
                callBack();
            });
        });


};
var Show = mongoose.model('shows', showSchema);
module.exports = Show;