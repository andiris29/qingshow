
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
    $coverMetadata: {
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
    if (!this.cover.length || (this.$coverMetadata && (this.cover === this.$coverMetadata.cover))) {
        callBack();
        return;
    }
    var _this = this;
    var defaultMetadata = {
        cover : "",
        width : 145,
        height : 270
    };

    gm(_this.cover).size(function (err, size) {
        if (size) {
            _this.$coverMetadata = {
                cover : _this.cover,
                width : size.width,
                height : size.height
            };
        } else {
            _this.$coverMetadata = defaultMetadata;
        }
        _this.save(function (err, image) {
            callBack();
        });
    });
};
var Show = mongoose.model('shows', showSchema);
module.exports = Show;