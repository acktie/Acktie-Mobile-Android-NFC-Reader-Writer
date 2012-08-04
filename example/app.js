// Example NFC Read/Write application
// NOTE: It will read any tag but only write mime plain text (text/plain) tags (unless changed)
Titanium.UI.setBackgroundColor('#000');

var writeTagDialog = undefined;

var nfc = require('com.acktie.mobile.android.nfc');
Ti.API.info("Is NFC Supported: " + nfc.isNFCEnabled);

// If NFC is not enabled not point in init'ing
if(nfc.isNFCEnabled)
{
	nfc.init();
}
else
{
	alert('Device does not support NFC! :( )');
}

//
// create base UI tab and root window
//
var win1 = Titanium.UI.createWindow({  
    title:'Tab 1',
    backgroundColor:'#000'
});

var ta1 = Titanium.UI.createTextArea({
	top: 110,
	backgroundColor: '#fff',
	color:'#336699',
	value: 'Scan a tag',
	font:{fontSize:20,fontFamily:'Helvetica Neue'},
	textAlign:'left',
	width:'100%',
	height: '80%',
	editable: false
});

var tf1 = Titanium.UI.createTextField({
    color:'#336699',
    height:80,
    top:10,
    left:5,
    width: '80%',
    borderStyle:Titanium.UI.INPUT_BORDERSTYLE_ROUNDED
});

var button = Titanium.UI.createButton({
   title: 'Write',
   top: 10,
   height: 80,
   right: 0,
   width: '15%',
});

win1.add(tf1);
win1.add(ta1);
win1.add(button);

win1.open();

button.addEventListener('click', function()
{
	if(tf1.value === '')
	{
		alert('Please enter text in the text field.');
		return;
	}

	// Hide keyboard
	tf1.blur();	
	
	// Enables module for write mode
	nfc.enableTagWriteMode();
	
	writeTagDialog = Titanium.UI.createAlertDialog({
    	title: 'Touch tag to write',
    	buttonNames: ['Cancel']
    });
    
    // If user cancels turn off write mode and enable read mode
    writeTagDialog.addEventListener('click', function(e){
        nfc.disableTagWriteMode();
		nfc.enableForegroundDispatch();
    });

	writeTagDialog.show();
});

var activity = Ti.Android.currentActivity;

activity.addEventListener('create', function(e)
{
	// If App (Activity) was Launched via an NFC tag then read it and display the contents to Text Area
	if(nfc.wasAppLaunchViaNFCIntent(nfc.ACTION_NDEF_DISCOVERED))
	{
		var text = readNFCData();
		
		// There are instances were the device can read the data but if the tag is removed too early the data is corrupt
		// thus making the text null (or undefined).
		if(text != undefined)
		{
			ta1.value = "Discovered tag (create): " + text;	
		}
		else
		{
			ta1.value = "Discovered tag (create): Unable to read text from tag... Try again";
		}
	}
});

activity.addEventListener('newIntent', function(e)
{
	var intent = e.intent;
	
	// If a new intent was passed to the application determine if the module is in writeMode.  If 
	// the module is in write mode write the data to the discovered tag.  If not, read the NDEF data
	// from the discovered tag
	// NOTE: If your app passed more then NFC intents you will need to determine which ones are NFC
	// intents and handle them appropriately.  This example only pushs NFC intents. 
	if(nfc.isWriteModeEnabled)
	{
		writeNFCData(tf1.value, intent);
	}
	else 
	{
		var text = readNFCData(intent);
		
		if(text != undefined)
		{
			ta1.value = "Discovered tag (newIntent): " + text;	
		}
		else
		{
			ta1.value = "Discovered tag (newIntent): Unable to read text from tag... Try again";
		}
	}
});

// If app is pause disable dispatch
activity.addEventListener('pause', function(e)
{
	Ti.API.info('Inside pause');
	nfc.disableForegroundDispatch();
});

// If app is resumed re-enable dispatch
activity.addEventListener('resume', function(e)
{
	Ti.API.info('Inside resume');
	nfc.enableForegroundDispatch();
});

// Helper to read the data off the NFC tag
function readNFCData(intent)
{
	// Needed for Java
	if(intent === undefined)
	{
		intent = null;
	}
	
	var message = undefined
	
	// Determine if passed in intent or active intent (on the current activity) contain
	// NDEF messages.
	var containsMessages = nfc.containsKnownNdefMessages(intent);
	Ti.API.info('containsKnownNdefMessages: ' + containsMessages);
	
	if(containsMessages)
	{
		// Parse the NDEF Messages out of the intent
		nfc.parse(intent);
		
		// Get the NDEF Message
		var message = nfc.getParsedNdefRecord(intent);
		Ti.API.info('Message: ' + message);
		if(message != null)
		{
			// Read the message
			Ti.API.info('Message text: ' + message.getTextResult());
			message = message.getTextResult();
		}
	}
	
	return message;	
}

// Helper to write the data to the NFC tag
function writeNFCData(text, intent)
{
	// Create plain text NDEF Message
	// Valid methods (See documentation for more details):
	//  - createURINFCData
	//  - createAbsoluteURINFCData
	//  - createMimeMediaNFCData
	//  -  createPlainTextNFCData
	var ndefMessage = nfc.createPlainTextNFCData(text);
	nfc.writeToTag(ndefMessage, intent, function(result)
	{
		// Inside Callback
		nfc.disableTagWriteMode();
		nfc.enableForegroundDispatch();
	
		if(writeTagDialog != undefined)
		{
			writeTagDialog.hide();
			writeTagDialog = undefined;
		}
		
		if(result.result)
		{
			alert("Successfully wrote message to tag!");
		}
		else
		{
			alert("Failed to write to tag: " + result.message);
		}
	});
}