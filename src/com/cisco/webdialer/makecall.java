package com.cisco.webdialer;

// Copyright (c) 2019 Cisco and/or its affiliates.
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

// Sample Java program demonstrating usage of the Cisco WebDialer SOAP API
// and Apache Axis to perform a <makeCallSoap> call, wait 5 seconds, then end the
// call with <endCallSoap>

// See README.md for configuration and launch instructions

// Tested using:
//
// Ubuntu Linux 19.04
// Java 1.8u201
// CUCM 11.5

import org.apache.axis.AxisFault;
import java.rmi.RemoteException;
import javax.xml.rpc.Service;
import java.net.*;

import WD70.*;

public class makecall {

    public static void main( String[] args ) {

        String cucm = System.getenv( "CUCM" );
        String proxyUser = System.getenv( "PROXY_USER" );
        String password = System.getenv( "PASSWORD" );
        String token = null; // Won't use SSO tokens in this sample
        String userName = System.getenv( "USER_NAME" );
        String deviceName = System.getenv( "DEVICE_NAME" );
        String callFrom = System.getenv( "CALL_FROM" );
        String callTo = System.getenv( "CALL_TO" );

        try {
            // Instantiate the Axis-generated WebDialer web service object
            Service service = new org.apache.axis.client.Service();

            // Create a Credential object with our WebDialer user credentials
            Credential credentials = new Credential( token, proxyUser, password );

            URL soapURL = new URL( "https://" + cucm + ":8443/webdialer/services/WebdialerSoapService70" );
            
            // Create the generated WebDialer SOAP binding
            WebdialerSoapService70SoapBindingStub web_dialer = new WebdialerSoapService70SoapBindingStub( soapURL, service );
            
            // Create a UserProfile object, and set the originating device name for the call
            UserProfile profile = new UserProfile();

            profile.setDeviceName( deviceName );
            profile.setUser( userName );
            profile.setLineNumber( callFrom );
            profile.setLocale( "" ); // This field is required per the WSDL, but is ignored by WebDialer

            // Make the call to the destination dial string
            CallResponse response = web_dialer.makeCallSoap( credentials, callTo, profile );

            System.out.println( response.getResponseDescription() );

            // Wait 5 seconds
            Thread.sleep( 5000 ); 

            // End the call
            response = web_dialer.endCallSoap( credentials, profile );

            System.out.println( response.getResponseDescription() );
            
        } catch ( AxisFault e ) {
            e.printStackTrace();
        } catch ( RemoteException e1 ) {
            e1.printStackTrace();
        } catch ( MalformedURLException e2 ) {
            e2.printStackTrace();
        } catch ( InterruptedException e3 ) {
            e3.printStackTrace();
        }
    }
}