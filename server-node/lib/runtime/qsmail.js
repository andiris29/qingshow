var _transporter;

var send = function(subject, text, callback) {
    _send({
        'from' : 'info@focosee.com',
        'to' : 'info@focosee.com',
        'bcc' : 'andiris29@gmail.com',
        'subject' : '[focosee] ' + subject,
        'text' : text
    }, callback);
};

var debug = function(subject, text, callback) {
    _send({
        'from' : 'info@focosee.com',
        'to' : 'andiris29@gmail.com',
        'subject' : '[focosee debug] ' + subject,
        'text' : text
    }, callback);
};

var _send = function(options, type) {
    if (!_transporter) {
        var nodemailer = require('nodemailer'), smtpTransport = require('nodemailer-smtp-transport');

        _transporter = nodemailer.createTransport(smtpTransport({
            // pop.exmail.qq.com:995
            'host' : 'smtp.exmail.qq.com',
            'port' : 465,
            'secureConnection' : true,
            'auth' : {
                'user' : 'info@focosee.com',
                'pass' : '123qaz'
            }
        }));
    }

    _transporter.sendMail(options, callback);
};

module.exports = {
    'send' : send,
    'debug' : debug
};
