var _transporter;

var send = function(subject, text, callback) {
    _send({
        'from' : 'top@focosee.com',
        'to' : 'top@focosee.com',
        'bcc' : 'andiris29@gmail.com',
        'subject' : '[qingshow] ' + subject,
        'text' : text
    }, callback);
};

var debug = function(subject, text, callback) {
    _send({
        'from' : 'top@focosee.com',
        'to' : 'andiris29@gmail.com',
        'subject' : '[qingshow debug] ' + subject,
        'text' : text
    }, callback);
};

var _send = function(options, callback) {
    if (!_transporter) {
        var nodemailer = require('nodemailer'), smtpTransport = require('nodemailer-smtp-transport');

        _transporter = nodemailer.createTransport(smtpTransport({
            // pop.exmail.qq.com:995
            'host' : 'smtp.exmail.qq.com',
            'port' : 465,
            'secure' : true,
            'auth' : {
                'user' : 'top@focosee.com',
                'pass' : 'ABCabc123'
            }
        }));
    }

    _transporter.sendMail(options, callback);
};

module.exports = {
    'send' : send,
    'debug' : debug
};
