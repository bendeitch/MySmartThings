/**
 *  Squeezebox Manager
 *
 *  Copyright 2017 Ben Deitch
 *
 */
definition(
    name: "Squeezebox Manager",
    namespace: "bendeitch",
    author: "Ben Deitch",
    description: "Service manager that integrates a Squeezebox Server instance into the Smartthings Hub.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Entertainment/entertainment2-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Entertainment/entertainment2-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Entertainment/entertainment2-icn@3x.png")

preferences {
	page(name: "serverPage", title: "Configure Squeezebox Server", nextPage: "playersPage", install: false, uninstall: true) {
		section("Connection Details") {
			input(name: "serverIP", type: "text", required: true, title: "Server IP Address")
       		input(name: "serverPort", type: "number", required: true, title: "Server Port Number")
		}
   	}
    page(name: "playersPage", title: "Select Squeezebox Players", install: true, uninstall: false)
}

def playersPage() {
	
    initializeServer()
    getServerStatus()
    
    def playerNames = connectedPlayerNames()
    def playerRefreshInterval = playerNames?.isEmpty() ? 2 : null
    
    dynamicPage(name: "playersPage", refreshInterval: playerRefreshInterval) {
    	section("Device Naming") {
        	input(name: "deviceNameSuffix", type: "string", title: "Add Suffix To Device Names", required: false)
        }
   		section("Connected Players") {
            input(name: "selectedPlayers", type: "enum", title: "Select Players (${playerNames.size()} found)", multiple: true, options: playerNames)
        }
    }
}

def connectedPlayerNames() {
	state.connectedPlayers ? state.connectedPlayers?.collect( {it.name} ) : []
}

def installed() {
	initialize()
}

def updated() {
	initialize()
}

def getServerDni() {
	String serverHex = "${serverIP}".tokenize('.')*.toInteger().asType(byte[]).encodeHex()
    String portHex = Integer.toHexString("${serverPort}".toInteger())
	return (serverHex + ":" + portHex).toUpperCase()
}

def createServerDevice(dni) {
        
    def hub = location.hubs[0].id

	def prefs = [
        isComponent: true,
        componentName: "SqueezeboxServer",
        componentLabel: "Squeezebox Server"
    ]
        
	addChildDevice("bendeitch", "Squeezebox Server", dni, hub, prefs)
}

def initialize() {
	unschedule()
	initializeServer()
    initializePlayers()
    scheduleServerStatus()
}

def initializeServer() {

	def serverDni = getServerDni()
    def serverDevice = getChildDevice(serverDni)
    if (!serverDevice) {
    	serverDevice = createServerDevice(serverDni)
    }
}

def initializePlayers() {

	def hub = location.hubs[0].id
    
    def serverHostAddress = "${serverIP}:${serverPort}"
    
    state.connectedPlayers?.each({
    	def player = getChildDevice(it.mac)
       	if (!player) {
		    def playerName = deviceNameSuffix ? "${it.name} ${deviceNameSuffix}" : it.name
			player = addChildDevice(
            	"bendeitch", 
                "Squeezebox Player", 
                it.mac, 
                hub,
                ["name": playerName, "label": playerName]
            )
			player.configure(serverHostAddress, it.mac)
        }
    })

}

def scheduleServerStatus() {
	schedule("0 * * * * ?", getServerStatus0) 
	schedule("2 * * * * ?", getServerStatus2) 
	schedule("4 * * * * ?", getServerStatus4) 
	schedule("6 * * * * ?", getServerStatus6) 
	schedule("8 * * * * ?", getServerStatus8) 
	schedule("10 * * * * ?", getServerStatus10) 
	schedule("12 * * * * ?", getServerStatus12) 
	schedule("14 * * * * ?", getServerStatus14) 
	schedule("16 * * * * ?", getServerStatus16) 
	schedule("18 * * * * ?", getServerStatus18) 
	schedule("20 * * * * ?", getServerStatus20) 
	schedule("22 * * * * ?", getServerStatus22) 
	schedule("24 * * * * ?", getServerStatus24) 
	schedule("26 * * * * ?", getServerStatus26) 
	schedule("28 * * * * ?", getServerStatus28) 
	schedule("30 * * * * ?", getServerStatus30) 
	schedule("32 * * * * ?", getServerStatus32) 
	schedule("34 * * * * ?", getServerStatus34) 
	schedule("36 * * * * ?", getServerStatus36) 
	schedule("38 * * * * ?", getServerStatus38) 
	schedule("40 * * * * ?", getServerStatus40) 
	schedule("42 * * * * ?", getServerStatus42) 
	schedule("44 * * * * ?", getServerStatus44) 
	schedule("46 * * * * ?", getServerStatus46) 
	schedule("48 * * * * ?", getServerStatus48) 
	schedule("50 * * * * ?", getServerStatus50) 
	schedule("52 * * * * ?", getServerStatus52) 
	schedule("54 * * * * ?", getServerStatus54) 
	schedule("56 * * * * ?", getServerStatus56) 
	schedule("58 * * * * ?", getServerStatus58) 
}

def getServerStatus0() { getServerStatus() }
def getServerStatus2() { getServerStatus() }
def getServerStatus4() { getServerStatus() }
def getServerStatus6() { getServerStatus() }
def getServerStatus8() { getServerStatus() }
def getServerStatus10() { getServerStatus() }
def getServerStatus12() { getServerStatus() }
def getServerStatus14() { getServerStatus() }
def getServerStatus16() { getServerStatus() }
def getServerStatus18() { getServerStatus() }
def getServerStatus20() { getServerStatus() }
def getServerStatus22() { getServerStatus() }
def getServerStatus24() { getServerStatus() }
def getServerStatus26() { getServerStatus() }
def getServerStatus28() { getServerStatus() }
def getServerStatus30() { getServerStatus() }
def getServerStatus32() { getServerStatus() }
def getServerStatus34() { getServerStatus() }
def getServerStatus36() { getServerStatus() }
def getServerStatus38() { getServerStatus() }
def getServerStatus40() { getServerStatus() }
def getServerStatus42() { getServerStatus() }
def getServerStatus44() { getServerStatus() }
def getServerStatus46() { getServerStatus() }
def getServerStatus48() { getServerStatus() }
def getServerStatus50() { getServerStatus() }
def getServerStatus52() { getServerStatus() }
def getServerStatus54() { getServerStatus() }
def getServerStatus56() { getServerStatus() }
def getServerStatus58() { getServerStatus() }

def schedulePlayersStatus() {
}

def processJsonMessage(msg) {
	def playerId = msg.params[0]
	if (playerId == "") {
    	processServerMessage(msg)
    } else {
    	def player = getChildDevice(playerId)
        if (player) {
        	player.processJsonMessage(msg)
        }
    }
}

def processServerMessage(msg) {
	def command = msg.params[1][0]

    switch (command) {
    	case "serverstatus":
        	processServerStatus(msg)
    }
}

def processServerStatus(msg) {

    def connectedPlayers = msg.result.players_loop.collect({[
    	name: it.name,
        mac: it.playerid,
        power: it.power
    ]})

	state.connectedPlayers = connectedPlayers
    
    updatePlayers()
}

def getServerStatus() {
    sendCommand(["", ["serverstatus", 0, 99]])
}

def updatePlayers() {
	state.connectedPlayers?.each({
    	def player = getChildDevice(it.mac)
        if (player && (it.power == "1" || player.updatePower(it.power))) {
            sendCommand([it.mac, ["status", "-", 1, "tags:abcl"]])
        }
    })
}

/*******************
 * Utility Methods *
 *******************/
 
 def sendCommand(params) {
 	//log.debug "Squeezebox Send: ${params}"
 	sendHubCommand(buildAction(params))
 }
 
 def buildAction(params) {
 	
    def method = "POST"
    
    def headers = [:]
	headers.put("HOST", "${serverIP}:${serverPort}")
	headers.put("Content-Type", "text/plain")
    
    def path = "/jsonrpc.js"
    
    def body = buildJsonRequest(params)
  
 	def action = new physicalgraph.device.HubAction(
    	method: method,
        headers: headers,
    	path: path,
    	body: body
	)

    action
 }
 
 def buildJsonRequest(params) {
 
 	def request = [
    	id: 1,
        method: "slim.request",
        params: params
    ]
    
 	def json = new groovy.json.JsonBuilder(request)

	json
}
