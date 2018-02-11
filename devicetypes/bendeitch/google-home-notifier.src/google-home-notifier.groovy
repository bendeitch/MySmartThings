/**
 *  Google Home Notifier
 *
 *  Copyright 2018 Ben Deitch
 *
 */
preferences {
  input("serviceHost", "string", required: true, title: "Host address for notifier service")
  input("servicePath", "string", required: true, title: "Path for notifier service")   
} 

metadata {
  definition (name: "Google Home Notifier", namespace: "bendeitch", author: "Ben Deitch") {
    capability "Actuator"
    capability "Speech Synthesis"
    capability "Music Player"
    
    attribute "serviceUrl", "string"
    
    command "speak", ["string"]
    command "speakWith", ["string", "string"]
    command "playTrack", ["string", "number"]
    command "playTrackAtVolume", ["string", "number", "number"]
    command "playTrackAndResume", ["string", "number", "number"]
  }
  
  tiles(scale: 2) {
    standardTile("notifier", "device.label", width: 6, height: 4) {
      state "default", label:"Google Home", inactivelabel:true, icon:"st.alarm.beep.beep", backgroundColor: "#ffffff"
    }
    valueTile("serviceUrl", "device.serviceUrl", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
      state "default", label:'Service URL:\n${currentValue}'
    }
    main "notifier"
    details(["notifier", "serviceUrl"])	
  }
}

void installed() {
  log.debug "Installed with settings: ${settings}"
  initialize()
}

def updated() {
  log.debug "Updated with settings: ${settings}"
  initialize()
}

def initialize() {
  def serviceUrl = "http://${settings.serviceHost}/${settings.servicePath}"
  sendEvent(name: "serviceUrl", value: serviceUrl, displayed: false, isStateChange: true)
}

// handle commands
def speak(text) {
	log.debug "Speaking: ${text}"
	buildAction(text)
}

def speakWith(device, text) {
	log.debug "Speaking (with ${device}): ${text}"
	buildAction(text, device)
}

def playTrack(uri) {
  log.debug "Playing: ${uri}"
  buildAction(uri)
}

def playTrackAtVolume(uri, duration) {
  playTrack(uri)
}

def playTrackAtVolume(uri, duration, volume) {
  playTrack(uri)
}

def playTrackAndResume(uri, duration) {
  playTrack(uri)
}

def playTrackAndResume(uri, duration, volume) {
  playTrack(uri)
}

def buildAction(text, device = '') {
   
  def method = "POST"
    
  def headers = [:]
  headers.put("HOST", settings.serviceHost)
  headers.put("Content-Type", "application/x-www-form-urlencoded")
    
  def path = "/" + settings.servicePath + "/" + URLEncoder.encode(device, 'UTF-8').replace('+','%20')
  
  log.debug(path)
  
  def action = new physicalgraph.device.HubAction(
    method: method,
    headers: headers,
    path: path,
    body: [text: "${text}"]
  )

  action
}
