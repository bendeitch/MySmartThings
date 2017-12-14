/**
 *  RESTful Music Players
 *
 *  Copyright 2017 Ben Deitch
 *
 */
definition(
    name: "RESTful Music Players",
    namespace: "bendeitch",
    author: "Ben Deitch",
    description: "Exposes a REST API that can be used to remotely control the main functions of music players.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Entertainment/entertainment2-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Entertainment/entertainment2-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Entertainment/entertainment2-icn@3x.png")

preferences {
	section {
		input "players", "capability.music player", title: "Select players to control", multiple: true
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

mappings {
	path("/players") {
        action: [
			GET: "listPlayers"
		]
	}
    path("/player/:playerName/:command") {
    	action: [
        	POST: "playerCommand"
        ]
    }
    path("/player/:playerName/:command/:value") {
    	action: [
        	POST: "playerCommand"
        ]
    }
}

def listPlayers() {
	def resp = []
    players.each {
      resp << [name: it.displayName]
    }
    return resp
}

def fuzzyMatch(deviceName, playerName) {

    def deviceParts = deviceName.toUpperCase().split(" ")
	def playerParts = playerName.toUpperCase().split(" ")
    
    int matches = deviceParts.collect({
    	devicePart -> playerParts.collect({
        	playerPart ->
            	if (devicePart.contains(playerPart)) {
                	2
                } else if (playerPart.contains(devicePart)) {
                	1
                } else {
                	0
                }
            }).sum()
    }).sum()

	return matches
}

def findPlayer(playerName) {

	def matches = players.collect({[ score: (fuzzyMatch(it.displayName, playerName)), player: it ]})

	def highScore = -1
    def player
	for (match in matches) {
    	if (match.score > highScore) {
            	player = match.player
                highScore = match.score
        } else if (match.score == highScore) {
            	player = null
        }
    }
    log.debug("Player selected: ${player}, matches: ${matches}")
    return player
}

def playerCommand() {
	def playerName = params.playerName?.trim()
    def command = params.command?.trim()
    def value = params.value?.trim()
    
    log.debug "Command received: \"${command}\", playerName: \"${playerName}\", value: ${value}"
    
    def player = findPlayer(playerName)
    
    if (player) {
        switch (command) {
        	case "pause":
            	player.pause()
                break
            case "play":
            	player.play()
                break
            case "stop":
            	player.stop()
                break
            case "mute":
            	player.mute()
                break
            case "unmute":
            	player.unmute()
                break
            case "next":
            	player.nextTrack()
                break
            case "previous":
            	player.previousTrack()
                break
            case "volume":
            	player.setLevel(value)
                break
            case "quieter":
            	player.setLevel("-" + value)
                break
            case "louder":
            	player.setLevel("+" + value)
                break
            case "favorite":
            	player.playFavorite(value)
                break
            default:
            	log.debug "command not found: \"${command}\""
        }
    } else {
	    log.debug "player not found: \"${playerName}\""
    }
    
}