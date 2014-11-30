module.exports.generateGeneralCallback = function(res) {
    return function(err, result) {
        if (err) {
            ServicesUtil.responseError(res, new ServerError(err));
        } else {
            if (result) {
                res.json(result);
            } else {
                res.end();
            }
        }
    };
};

