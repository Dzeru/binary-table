var getUrlParameter = function getUrlParameter(sParam) {
	var sPageURL = decodeURIComponent(window.location.search.substring(1)),
	    sURLVariables = sPageURL.split('&'),
	    sParameterName,
	    i;

	for (i = 0; i < sURLVariables.length; i++) {
	    sParameterName = sURLVariables[i].split('=');

	    if (sParameterName[0] === sParam) {
	        return sParameterName[0] === undefined ? false : sParameterName[1];
	    }
	}
};

var id = getUrlParameter('id');
var url = "/getgoal?id=" + id;
console.log(url);

function getGoalInfo() {
	$.ajax({
		url: url,
		type: 'GET',
		// url: /goal
		// type: 'POST',
		// data: JSON.stringify(id),
		success: function(res) {
			console.log(res);
			var self = document.getElementById('script');
			var test = document.createElement('div');
			var info = "Server answer (better look in console): " + res;
			test.appendChild(document.createTextNode(info));
			self.parentNode.insertBefore(test, self);
		},
		error: function(err) {
			console.log(err);
			var self = document.getElementById('script');
			var test = document.createElement('div');
			var info = "Server answer (better look in console): " + err;
			test.appendChild(document.createTextNode(info));
			self.parentNode.insertBefore(test, self);
		}
	})
}

getGoalInfo();