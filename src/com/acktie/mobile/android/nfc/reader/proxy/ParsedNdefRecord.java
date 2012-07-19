package com.acktie.mobile.android.nfc.reader.proxy;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.primitives.Bytes;


import android.net.Uri;
import android.nfc.NdefRecord;
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Convenience class that will parse a NdefMessage and retrieve the Text or URI (URL).
 *  
 * @author TNuzzi
 *
 */
@Kroll.proxy
public class ParsedNdefRecord extends KrollProxy 
{

	// Standard Debugging variables
	private static final String LCAT = "AcktiemobileandroidnfcreaderModule.ParsedNdefRecord";
	
	@SuppressWarnings("unused")
	private static final boolean DBG = TiConfig.LOGD;
	
	private NdefRecord record = null;
	
	// URI Record
	private Uri uri;
	
	// Text Record
	/** ISO/IANA language code */
    private String languageCode;

    private String text;
	
	/**
     * NFC Forum "URI Record Type Definition"
     *
     * This is a mapping of "URI Identifier Codes" to URI string prefixes,
     * per section 3.2.2 of the NFC Forum URI Record Type Definition document.
     */
    private static final BiMap<Byte, String> URI_PREFIX_MAP = ImmutableBiMap.<Byte, String>builder()
            .put((byte) 0x00, "")
            .put((byte) 0x01, "http://www.")
            .put((byte) 0x02, "https://www.")
            .put((byte) 0x03, "http://")
            .put((byte) 0x04, "https://")
            .put((byte) 0x05, "tel:")
            .put((byte) 0x06, "mailto:")
            .put((byte) 0x07, "ftp://anonymous:anonymous@")
            .put((byte) 0x08, "ftp://ftp.")
            .put((byte) 0x09, "ftps://")
            .put((byte) 0x0A, "sftp://")
            .put((byte) 0x0B, "smb://")
            .put((byte) 0x0C, "nfs://")
            .put((byte) 0x0D, "ftp://")
            .put((byte) 0x0E, "dav://")
            .put((byte) 0x0F, "news:")
            .put((byte) 0x10, "telnet://")
            .put((byte) 0x11, "imap:")
            .put((byte) 0x12, "rtsp://")
            .put((byte) 0x13, "urn:")
            .put((byte) 0x14, "pop:")
            .put((byte) 0x15, "sip:")
            .put((byte) 0x16, "sips:")
            .put((byte) 0x17, "tftp:")
            .put((byte) 0x18, "btspp://")
            .put((byte) 0x19, "btl2cap://")
            .put((byte) 0x1A, "btgoep://")
            .put((byte) 0x1B, "tcpobex://")
            .put((byte) 0x1C, "irdaobex://")
            .put((byte) 0x1D, "file://")
            .put((byte) 0x1E, "urn:epc:id:")
            .put((byte) 0x1F, "urn:epc:tag:")
            .put((byte) 0x20, "urn:epc:pat:")
            .put((byte) 0x21, "urn:epc:raw:")
            .put((byte) 0x22, "urn:epc:")
            .put((byte) 0x23, "urn:nfc:")
            .build();

	public ParsedNdefRecord() {
		super();
	}
	
	public ParsedNdefRecord(NdefRecord record) {
		super();
		this.record = record;
		
		parse();
	}
	
	@Kroll.method
	public String getText()
	{
		return this.text;
	}
	
	@Kroll.method
	public String getLanguageCode()
	{
		return this.languageCode;
	}
	
	@Kroll.method
	public String getUriText()
	{
		return this.uri.toString();
	}
	
	@Kroll.method
	public String getTextResult()
	{
		if(this.text != null)
		{
			return getText();
		}
		else if(this.uri != null)
		{
			return getUriText();
		}
		
		return "";
	}
	
	private void parse() 
	{
		Log.d(LCAT, "[Inside] parse");
        short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.getType(), NdefRecord.RTD_URI)) 
        {
            parseWellKnownURI();
        } 
        else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) 
        {
            parseAbsoluteURI();
        } 
        else if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) 
        {
            parseTextRecord();
        }
    }

    /** Parse and absolute URI record */
    private void parseAbsoluteURI() 
    {
    	Log.d(LCAT, "[Inside] parseAbsoluteURI");
        byte[] payload = record.getPayload();
        this.uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
    }

    /** Parse an well known URI record */
    private void parseWellKnownURI() 
    {
    	Log.d(LCAT, "[Inside] parseWellKnownURI");
    	
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_URI));
        byte[] payload = record.getPayload();
        /*
         * payload[0] contains the URI Identifier Code, per the
         * NFC Forum "URI Record Type Definition" section 3.2.2.
         *
         * payload[1]...payload[payload.length - 1] contains the rest of
         * the URI.
         */
        String prefix = URI_PREFIX_MAP.get(payload[0]);
        byte[] fullUri =
            Bytes.concat(prefix.getBytes(Charset.forName("UTF-8")), Arrays.copyOfRange(payload, 1,
                payload.length));
        this.uri = Uri.parse(new String(fullUri, Charset.forName("UTF-8")));
    }
    
    // TODO: deal with text fields which span multiple NdefRecords
    /** Parse Text record */
    private void parseTextRecord() 
    {
    	Log.d(LCAT, "[Inside] parseTextRecord");
    	
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
        try {
            byte[] payload = record.getPayload();
            /*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             *
             * bit7 is the Text Encoding Field.
             *
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             *
             * Bit_6 is reserved for future use and must be set to zero.
             *
             * Bits 5 to 0 are the length of the IANA language code.
             */
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0077;
            this.languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            this.text =
                new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
        	//TODO: Deal with the encoding exception gracefully
        	
            // should never happen unless we get a malformed tag.
            throw new IllegalArgumentException(e);
        }
    }
}
