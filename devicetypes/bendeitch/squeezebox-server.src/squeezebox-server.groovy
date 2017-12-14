/**
 *  Squeezebox Server
 *
 *  Copyright 2017 Ben Deitch
 *
 */
metadata {
	definition (name: "Squeezebox Server", namespace: "bendeitch", author: "Ben Deitch")
}

def parse(String description) {
	def msg = parseLanMessage(description)
    if (msg.json) {
    	//log.debug "Squeezebox Received: ${msg.json}"
  		parent.processJsonMessage(msg.json)
    }
}