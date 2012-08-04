# Acktie Mobile Android NFC Module

## Description

This module enables your application to take advantage of the devices NFC capabilites.  The purpose is to provide a simplified interface with the Android 
apis and to access them from the Appcelerator framework.  In order to be compatible with as many Android devices as possible, the module was developed
for Android 2.3.3 and higher (Version 10).  As a result, the newer Jelly Bean (version 16) API are not accessible however the feature to read,
write and do P2P messages are available.  

If you are unfamiliar with NFC, we encourge you the read the documenation on the Android website. 

*  [Android NFC Basics](http://developer.android.com/guide/topics/connectivity/nfc/nfc.html)
*  [Android NFC Advance](http://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc.html)

The module was created to take a broad view of NFC and allows you to implement a number of feature in your application.
For example:

1.  Launch your app when a certain NFC tag is read
2.  Read all NFC tags once you are in your app
3.  Write a number of different NFC messages 

See the app.js in the example directory for a working example.

We used NFC tags purchased from [tagstand](http://www.tagstand.com) for the module development.

NOTE: We recommend downloading a NFC read/write application to assist you in your development.  It is useful to ensure your tag has the correct type/data.  We used NXP Tag writer.

NOTE: This module was tested and developed using a Samsung Nexus S.  We could not find a good simulator so testing on a device is required.

## Acktie Mobile Android NFC Module

To access this module from JavaScript, you would do the following:

	var nfc = require("com.acktie.mobile.android.nfc");

The nfc variable is a reference to the Module object.	

## Reference

The following are the features and Javascript functions you can call with the module.

### Launch your application when an NFC tag is read
This section will describe how to modify the AndroidModule.xml via the tiapp.xml to register your application to receive NFC intents.  As a result,
your application will be launched when your tag is read.

The [Android documentation](http://developer.android.com/guide/topics/connectivity/nfc/nfc.html#dispatching) had a good flowchart explaining the NFC tag dispatch system.
Read through this section to get an understanding of how it works.  Below is an example of how to setup your application to be launched when your tag is read:

Lets assume you want your application to be launched when a tag with the mime type of text/plain is read:

1.  Use an application like NXP Tag Writer to write plain text data to a tag.
2.  Copy and modify (the android:name and android:label with your app data) in example/tiapp.xml (See tiapp.xml in the example directory for detail).
3.  Place the modified xml code in your app's tiapp.xml file.
4.  Build and Launch on a device.

With these steps done your application should be launched when your tag is read.  If not, make sure the tag data is text/plain and your AndroidManifest.xml looks correct.

### init (required)
This method is used to initialize the module with user settings.

#### mimeType (optional)
This property is used to set the mime type for the IntentFilter on incoming intents.

Default is \*/\* (all mime types).

### wasAppLaunchViaNFCIntent
This method tests to determine if the current intent or passed intent was from an read NFC event.  By default the method will check using all the NFC intent actions (ACTION_NDEF_DISCOVERED, ACTION_TECH_DISCOVERED, and ACTION_TAG_DISCOVERED).

#### action (optional)
If you want the method to only check for a specific intent use this property with one of the follow constants, ACTION_NDEF_DISCOVERED, ACTION_TECH_DISCOVERED, or ACTION_TAG_DISCOVERED.

### isNFCEnabled
Check to see if NFC is available and enabled on the device.

### isWriteModeEnabled
Check to see if the module is in write mode.

### containsKnownNdefMessages
This method will check the current intent (in the Activity) or a passed in intent to determine if it has any Ndef Messages (NFC data).

#### proxyIntent (optional)
The intent to check for the NDef Messages

### parse
This method will parse the Ndef messages in current intent (in the Activity) or a passed in intent.

### getParsedNdefRecord
This method will return the parsed Ndef Message (call parse() first).

### disableForegroundDispatch
This method will cancal any enableForegroundDispach call that were made.  

NOTE: Calls the disableForegroundDispatch method on the NfcAdapter class (See Android Javadocs for more details).

### disableForegroundNdefPush
This method will cancal any enableForegroundNdefPush call that were made.  

NOTE: Calls the disableForegroundNdefPush method on the NfcAdapter class (See Android Javadocs for more details).

### enableForegroundDispatch
This method is used to listen for NFC events.  If a tag is read then a PendingIntent is populated and sent to the application
calling the newIntent listener.

NOTE: Calls the enableForegroundDispatch method on the NfcAdapter class (See Android Javadocs for more details).

#### intentFilter (optional)
If you want the method to only check for a specific intent use this property with one of the follow constants, ACTION_NDEF_DISCOVERED or ACTION_TAG_DISCOVERED.

### enableForegroundNdefPush
This method is used to Push a NdefMessage to a tag or another NFC device.

NOTE: Calls the enableForegroundNdefPush method on the NfcAdapter class (See Android Javadocs for more details).

#### msg (required)
Use this option to pass in the Ndefmessge.  This is the message that will be received by the tag or device.

### Create NDEF Records
This next section describes methods that are used to create NdefMessages.  It uses the process described in the [Android Documentation](http://developer.android.com/guide/topics/connectivity/nfc/nfc.html#creating-records).
### createPlainTextNFCData
This method will create an NDefMessage of type TNF_WELL_KNOWN and RTD_TEXT.  This is mime type of text/plain.  Use the output to pass to writeToTag method or enableForegroundNdefPush.

#### text (required)
The text string of the message.

#### locale (optional)
The two letter language locale (See JavaDocs locale documenation for examples).  Locale.getDefault() (Java code) is used by default.

#### useUTF16Encoding(optional)
Whether or not to encode the message using UTF16 encoding.  Default is UTF8.

Default is false.

### createURINFCData
The method create a NdefMessage contain a URI.

#### uri (requried)
A URI string

### createAbsoluteURINFCData
The method create a NdefMessage contain an absolute URI.

#### uri (requried)
An absolute URI string

### createMimeMediaNFCData
This method will create a NdefMessage contain a mime type and message.  

#### mimeType (requried)
The mime type string of the message.

#### message (requried)
A message string

### enableTagWriteMode
This method will enable the module for tag/P2P write mode.

#### dontDisablePendingForegroundDispatchOrPush (optional)
This boolean is to indicate whether or not to disable the foreground dispatch/push.

The default is false.  It will disable any outstanding foreground dispatch/push.

### disableTagWriteMode
This method will disable the tag write mode.

### writeToTag
This method will write the passed NdefMessage to the detected tag (from the passed intent) and notify a callback with the results of the write operation.

#### nFCmessage (required)
The NdefMessage to write.  This message would be the result of one of the create operations (see above).

#### proxyIntent (required)
This is the intent that was received with the detected tag attached.

#### callback (required)
The JS function that will be called when the write operation has completed.

#### result
The JS function will be passed a dictionary object that will contain the result of the write operation.

*  result - A boolean on whether or not the write operation was successful.
*  message - If a failure occured, this is the error message


## Known issues/Limitations
Only 1 NFC device was available for the module development.  As a result the P2P functionality was not tested.  However, the only method used 
for the P2P is the enableForegroundNdefPush method (See NfcAdaptor for more info).  We have exposed this method so you can use it in your application.

## Change Log

*  1.0: Initial release of NFC Module

## Author

Tony Nuzzi @ Acktie

Twitter: @Acktie

Email: support@acktie.com

