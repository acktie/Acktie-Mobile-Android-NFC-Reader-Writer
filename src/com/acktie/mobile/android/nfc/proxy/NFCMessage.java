/**
 * 
 */
package com.acktie.mobile.android.nfc.proxy;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import android.nfc.NdefMessage;

/**
 * Shell class to hold the NdefMessage on the Javascript side.  Keep getting the 
 * following error trying the pass the NdefMessage directly to Javascript side:
 * 
 * !!! Unable to convert unknown Java object class 'android.nfc.NdefMessage' to Js value !!!
 * 
 * @author TNuzzi
 *
 */
@Kroll.proxy
public class NFCMessage extends KrollProxy {

	private NdefMessage message = null;
	
	public NFCMessage(NdefMessage message) {
		this.message = message;
	}
	
	public NdefMessage getMessage() {
		return message;
	}

}
