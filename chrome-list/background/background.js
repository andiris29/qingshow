var debug = 'local';

var apiUrl = 'http://chingshow.com/services/goblin/findItem?source=';
if (debug == 'dev') {
    apiUrl = 'http://dev.chingshow.com/services/goblin/findItem?source=';
} else if (debug == 'local') {
    apiUrl = 'http://localhost:30001/services/goblin/findItem?source=';
}

/**
 * Possible parameters for request:
 *  action: "xhttp" for a cross-origin HTTP request
 *  data  : data to send in a POST request
 *
 * The callback function is called upon completion of the request */
chrome.runtime.onMessage.addListener(function(request, sender, callback) {
    console.log(apiUrl);
    if (request.action == "xhttp") {
        var xhttp = new XMLHttpRequest();
        xhttp.onload = function() {
            callback(xhttp.responseText);
        };
        xhttp.onerror = function() {
            // Do whatever you want on error. Don't forget to invoke the
            // callback to clean up the communication port.
            callback();
        };
        
        xhttp.open('GET', apiUrl + request.data);
        xhttp.send();
        return true; // prevents the callback from being called too early on return
    }
});
