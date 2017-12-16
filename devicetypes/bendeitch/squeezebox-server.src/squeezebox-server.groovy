/**
 *  Squeezebox Server
 *
 *  Copyright 2017 Ben Deitch
 *
 */

// super simple child device for Squeezebox Manager smart app used to receive HTTP responses from Squeezebox Server
metadata {
  definition (name: "Squeezebox Server", namespace: "bendeitch", author: "Ben Deitch")
}

// receive the response
def parse(String description) {
  // parse the response into a message
  def msg = parseLanMessage(description)
  // if the message contains JSON then pass it to the parent Squeezebox Manager smart app
  if (msg.json) {
    //log.debug "Squeezebox Received: ${msg.json}"
    parent.processJsonMessage(msg.json)
  }
}