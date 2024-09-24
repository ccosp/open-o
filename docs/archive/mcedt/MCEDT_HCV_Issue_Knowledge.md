MCEDT and HCV Issue Knowledge

The MCEDT and HCV functionalities are working now, this document is a reference for future review / update / fix.

# NoClassDefFoundError

This error was caused after the "Apache CXF" was updated from version 2.7.11 to 3.2.0. The "WSPasswordCallBack" class is moved from "org.apache.ws.security" to "org.apache.wssj4.common.ext" in 3.2.0.

To resolve this error, we had to update the "ebs-client" library to make it compatible with Apache CXF 3.2.0. Since the latest published version of the ebs-client is 0.0.6, which is what we had in the application, we modified the ebs-client core classes, implemented these classes into the OSCAR source code, and removed the ebs-client 0.0.6 dependency to avoid any future conflict between two different versions.

The updated ebs-client classes are located in pacakge "org.oscarehr.integration.ebs.client.ng" (file path: src/main/java/org/oscarehr/integration/ebs/client/ng).

# Header Parsing Error

The error "org.apache.cxf.binding.soap.SoapFault: An error was discovered processing the wsse:Security header" threw when getting list on both Sent and Download pages, which was caused due to the algorithm RSA1.5 being unsupported by the "WSS4J 2.2.0" because of its security risk (WSS4J 2.2.0 is the default version in CXF 3.2.0, we can't manually downgrade or upgrade it to another version).

We had to insert a line of code into the EDTClientBuilder.java (one of the classes of ebs-client), newWSSInInterceptorConfiguration method:

"inProps.put(WSHandlerConstants.ALLOW_RSA15_KEY_TRANSPORT_ALGORITHM, "true");"

This enabled the use of this RSA 1.5 algorithm in the current WSS4J version, and the header parsing error was resolved. 

# Signature Invalid Error

The error "org.apache.ws.security.WSSecurityException: The signature or decryption was invalid" threw when attempting to download the file on the Download page.

We had to insert two lines of code into the EDTClientBuilder.java, newWSSInInterceptorConfiguration method:

"inProps.put(WSHandlerConstants.STORE_BYTES_IN_ATTACHMENT, "false");"

"inProps.put(WSHandlerConstants.EXPAND_XOP_INCLUDE, "false");"

After setting the "STORE_BYTES_IN_ATTACHMENT" and "EXPAND_XOP_INCLUDE" to false, the binary data is forced to be included inline in the SOAP message (STORE_BYTES_IN_ATTACHMENT, "false"), ensuring no issues arise from processing attachments. By setting EXPAND_XOP_INCLUDE to false, we avoided looking for XOP references in the response. The error was resolved after changing the setting.

The default setting of the two properties is false. However, the setting has been switched to true due to other configurations (likely when MTOM is enabled, the setting changed to true automatically). In this case, we had to configure the properties to "false".

# Unit Tests of MCEDT and HCV

## MCEDT Tests

The error "NullPointerException" was caused by "SpringUtils.getBean", which led to most of the test cases running to fail. This is because test cases use Spring components, which are not properly set up in the BaseTest class. We solved the error by adding spring initializing code in the BaseTest class. 

After we fixed the error, most of the test cases passed.

There are 7 test cases running failed and throwing "NumberFormatException" error, and we are not going to fix them at this point. 

## HCV Tests

There are 18 test cases skipped, and we are not going to enable them to run at this point. 
