var channelStore = module.exports;

var _store = {};

channelStore.set = function(ip, channel) {
    _store[ip] = channel;
};

channelStore.get = function(ip) {
    return _store[ip];
};
