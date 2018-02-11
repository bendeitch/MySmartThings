/**
 *  Squeezebox Player
 *
 *  Copyright 2017 Ben Deitch
 *
 */

metadata {

  definition (name: "Squeezebox Player", namespace: "bendeitch", author: "Ben Deitch") {
    capability "Actuator"
    capability "Music Player"
    capability "Refresh"
    capability "Sensor"
    capability "Switch"

    attribute "serverHostAddress", "string"
    attribute "playerMAC", "string"

    command "playFavorite", ["number"]
    command "fav1"
    command "fav2"
    command "fav3"
    command "fav4"
    command "fav5"
    command "fav6"
    command "playTrackAndResume", ["string", "number", "number"]
    command "playTrackAndRestore", ["string", "number", "number"]
    command "playTrackAtVolume", ["string","number"]
  }

  tiles {
    standardTile("onOff", "device.switch", canChangeIcon: true) {
      state "off", label: 'OFF', action: "switch.on", icon: "st.Electronics.electronics16", backgroundColor: "#ffffff", nextState: "turningOn"
      state "turningOn", label: 'TURNING ON', icon: "st.Electronics.electronics16", backgroundColor: "#00a0dc", nextState: "on"
      state "on", label: 'ON', action: "switch.off", icon: "st.Electronics.electronics16", backgroundColor: "#00a0dc", nextState: "turningOff"
      state "turningOff", label: 'TURNING OFF', icon: "st.Electronics.electronics16", backgroundColor: "#ffffff", nextState: "off"
    }
    standardTile("mute", "device.mute", decoration: "flat") {
      state "unmuted", label:'', icon:'st.custom.sonos.unmuted', action:'music Player.mute', nextState: "muted"
      state "muted", label:'', icon:'st.custom.sonos.muted', action:'music Player.unmute', nextState: "unmuted"
    }
    controlTile("volume", "device.level", "slider", range:"(0..100)") {
      state "level", action:"music Player.setLevel"
    }
    standardTile("playpause", "device.status", decoration: "flat") {
      state "paused", label:'', icon:'st.sonos.play-btn', action:'music Player.play'
      state "playing", label:'', icon:'st.sonos.pause-btn', action:'music Player.pause'
    }
    standardTile("stop", "device.stop", decoration: "flat") {
      state "default", label:'', action:"music Player.stop", icon:"st.sonos.stop-btn"
    }
    standardTile("prev", "device.switch", decoration: "flat") {
      state "default", label:'', action:"music Player.previousTrack", icon:"st.sonos.previous-btn"
    }
    standardTile("next", "device.switch", decoration: "flat") {
      state "default", label:'', action:"music Player.nextTrack", icon:"st.sonos.next-btn"
    }
    valueTile("trackDescription", "device.trackDescription", decoration: "flat", width: 3, height: 1) {
      state "default", label:'${currentValue}'
    }
    standardTile("fav1", "device.switch", decoration: "flat") {
      state "default", label:'1', action:"fav1"
    }
    standardTile("fav2", "device.switch", decoration: "flat") {
      state "default", label:'2', action:"fav2"
    }
    standardTile("fav3", "device.switch", decoration: "flat") {
      state "default", label:'3', action:"fav3"
    }
    standardTile("fav4", "device.switch", decoration: "flat") {
      state "default", label:'4', action:"fav4"
    }
    standardTile("fav5", "device.switch", decoration: "flat") {
      state "default", label:'5', action:"fav5"
    }
    standardTile("fav6", "device.switch", decoration: "flat") {
      state "default", label:'6', action:"fav6"
    }
    main "onOff"
    details (["onOff", "mute", "volume", "trackDescription", "prev", "playpause", "next", "fav1", "fav2", "fav3", "fav4", "fav5", "fav6"])
  }
}

def configure(serverHostAddress, playerMAC) {

  state.serverHostAddress = serverHostAddress
  sendEvent(name: "serverHostAddress", value: state.serverHostAddress, displayed: false, isStateChange: true)

  state.playerMAC = playerMAC
  sendEvent(name: "playerMAC", value: state.playerMAC, displayed: false, isStateChange: true)
}

def processJsonMessage(msg) {

  //log.debug "Squeezebox Player Message [${device.name}]: ${msg}"

  def command = msg.params[1][0]

  switch (command) {
    case "status":
      processStatus(msg)
  }
}

def processStatus(msg) {

  updatePower(msg.result?.get("power"))
  updateVolume(msg.result?.get("mixer volume"))
  updatePlayPause(msg.result?.get("mode"))
    
  def trackDetails = msg.result?.playlist_loop?.get(0)
  String track
  if (trackDetails) {
    track = trackDetails.artist ? "${trackDetails.title} by ${trackDetails.artist}" : trackDetails.title
  }
  updateTrackDescription(track)
}

def updatePower(onOff) {

  String current = device.currentValue("switch")
  String onOffString = String.valueOf(onOff) == "1" ? "on" : "off"

  if (current != onOffString) {

    //log.debug "Squeezebox Player [${device.name}]: updating power: ${current} -> ${onOffString}"
    sendEvent(name: "switch", value: onOffString, displayed: true)
    return true
 
  } else {
    return false
  }
}

def updateVolume(volume) {
  String absVolume = Math.abs(Integer.valueOf(volume)).toString()
  sendEvent(name: "level", value: absVolume, displayed: true)
}

def updatePlayPause(playpause) {

  String status
  switch (playpause) {
    case "play":
      status = "playing"
      break
    case "pause":
      status = "paused"
      break
    case "stop":
      status = "stopped"
      break
    default:
      status = playpause
  }

  sendEvent(name: "status", value: status, displayed: true)
}

def updateTrackDescription(trackDescription) {
  sendEvent(name: "trackDescription", value: trackDescription, displayed: true)
}
/************
 * Commands *
 ************/

//--- Power
def on() {
  log.debug "Executing 'on'"
  buildAction(["power", 1])
}
def off() {
  log.debug "Executing 'off'"
  buildAction(["power", 0])
}

//--- Volume
def setLevel(level) {
  log.debug "Executing 'setLevel'"
  buildAction(["mixer", "volume", level])
}
def mute() {
  log.debug "Executing 'mute'"
  buildAction(["mixer", "muting", 1])
}
def unmute() {
  log.debug "Executing 'unmute'"
  buildAction(["mixer", "muting", 0])
}

//--- Playback
def setPlaybackStatus() {
  log.debug "Executing 'setPlaybackStatus'"
  // TODO: handle 'setPlaybackStatus' command
}
def play() {
  log.debug "Executing 'play'"
  buildAction(["play"])
}
def pause() {
  log.debug "Executing 'pause'"
  buildAction(["pause"])
}
def stop() {
  log.debug "Executing 'stop'"
  buildAction(["stop"])
}
def nextTrack() {
  log.debug "Executing 'nextTrack'"
  buildAction(["playlist", "jump", "+1"])
}
def previousTrack() {
  log.debug "Executing 'previousTrack'"
  buildAction(["playlist", "jump", "-1"])
}
def setTrack(trackToSet) {
  log.debug "Executing 'setTrack'"
  // TODO: handle 'setTrack' command
}
def resumeTrack(trackToResume) {
  log.debug "Executing 'resumeTrack'"
  // TODO: handle 'resumeTrack' command
}
def restoreTrack(trackToRestore) {
  log.debug "Executing 'restoreTrack'"
  // TODO: handle 'restoreTrack' command
}
def playTrack(trackToPlay) {
  log.debug "Executing 'playTrack'"
  playUri(trackToPlay)
}
def playTrackAtVolume(uri, volume) {
  log.debug "Executing 'playTrackAtVolume'"
  def actions = [setLevel(volume), playUri(uri)]
  actions
}
def playTrackAndResume(uri, duration) {
  log.debug "Executing 'playTrackAndResume'"
  playUri(uri)
}
def playTrackAndResume(uri, duration, volume) {
  log.debug "Executing 'playTrackAndResume'"
  def actions = [setLevel(volume), playUri(uri)]
  actions
}
def playTrackAndRestore(uri, duration) {
  log.debug "Executing 'playTrackAndRestore"
  playUri(uri)
}
def playTrackAndRestore(uri, duration, volume) {
  log.debug "Executing 'playTrackAndRestore"
  def actions = [setLevel(volume), playUri(uri)]
  actions
}
def playUri(uri) {
  buildAction(["playlist", "play", uri])
}
//--- Favorites
def playFavorite(index) {
  log.debug "Playing favorite ${index}"
  buildAction(["favorites", "playlist", "play", "item_id:${index - 1}"])
}

def fav1() { playFavorite(1) }
def fav2() { playFavorite(2) }
def fav3() { playFavorite(3) }
def fav4() { playFavorite(4) }
def fav5() { playFavorite(5) }
def fav6() { playFavorite(6) }

/*******************
 * Utility Methods *
 *******************/
 
def buildAction(params) {
   
  def method = "POST"
    
  def headers = [:]
  headers.put("HOST", state.serverHostAddress)
  headers.put("Content-Type", "text/plain")
    
  def path = "/jsonrpc.js"
    
  def body = buildJsonRequest(params)
  
  def action = new physicalgraph.device.HubAction(
    method: method,
    headers: headers,
    path: path,
    body: body
  )

  //log.debug "ACTION: ${action}"
  
  action
}
 
def buildJsonRequest(params) {
 
  def request = [
    id: 1,
    method: "slim.request",
    params: [state.playerMAC, params]
  ]
    
  def json = new groovy.json.JsonBuilder(request)

  json
}