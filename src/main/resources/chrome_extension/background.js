$.getJSON("header.json", function(data){
	chrome.webRequest.onBeforeSendHeaders.addListener(
	function(details) {
		$.each(data, function (index, header) {
			details.requestHeaders.push({name: header.name, value: header.value});
		});
		return {requestHeaders: details.requestHeaders};
	},
	{urls: []},
	['requestHeaders', 'blocking']
	);	 
});