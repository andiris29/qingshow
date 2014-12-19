var gm = require('gm').subClass({
    imageMagick : true
});

var ImageUtils = module.exports;

ImageUtils.createOrUpdateMetadata = function(model, field, callback) {
    var fieldMetadata = field + 'Metadata';

    if (model[field]) {
        if (model[fieldMetadata] && model[fieldMetadata].url === model[field]) {
            // Do nothing, already created
            callback(null);
        } else {
            delete model[fieldMetadata];
            gm(model[field]).size( function(err, size) {
                if (!err && size) {
                    // Update metadata
                    model[fieldMetadata] = {
                        'url' : model[field],
                        'width' : size.width,
                        'height' : size.height
                    };
                } else {
                    // Update default metadata
                    model[fieldMetadata] = _defaultMetadata;
                }
                model.save(callback);
            }.bind(this));
        }
    } else {
        if (model[fieldMetadata]) {
            // Remove outdated
            delete model[fieldMetadata];
            model.save(callback);
        } else {
            // Do nothing
            callback(null);
        }
    }
};

var _defaultMetadata = {
    'url' : null,
    'width' : 145,
    'height' : 270
};

