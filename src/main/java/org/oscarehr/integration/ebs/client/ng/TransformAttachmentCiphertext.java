package org.oscarehr.integration.ebs.client.ng;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.xml.sax.SAXException;

public class TransformAttachmentCiphertext extends TransformSpi {

	public static final String TRANSFORM_ATTACHMENT_CIPHERTEXT = 
			"http://docs.oasis-open.org/wss/oasis-wss-SwAProfile-1.1#Attachment-Ciphertext-Transform";

	/**
	 * @see org.apache.xml.security.transforms.TransformSpi#engineGetURI()
	 */
	@Override
	public String engineGetURI() {
		return TRANSFORM_ATTACHMENT_CIPHERTEXT;
	}

	/**
	 * @see org.apache.xml.security.transforms.TransformSpi#enginePerformTransform(org.apache.xml.security.signature.XMLSignatureInput,
	 *      java.io.OutputStream, org.apache.xml.security.transforms.Transform)
	 */
	@Override
	protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input,
			OutputStream os, Transform transformObject) throws IOException,
			CanonicalizationException, InvalidCanonicalizerException,
			TransformationException, ParserConfigurationException, SAXException {
		
		if (input.isOctetStream() || input.isNodeSet()) {
			if (os == null) {
				byte[] contentBytes = input.getBytes();
				XMLSignatureInput output = new XMLSignatureInput(contentBytes);
				return output;
			}
			
			if (input.isByteArray() || input.isNodeSet()) {
				os.write(input.getBytes());
			} else {
				try {
					org.apache.xml.security.utils.Base64.decode(new BufferedInputStream(input.getOctetStreamReal()), os);
				} catch (Base64DecodingException e) {
					throw new IOException("Unable to decode real octet stream", e);
				}
			}
			
			XMLSignatureInput output = new XMLSignatureInput(new byte[] {});
			output.setOutputStream(os);
			return output;
		}
		return input;
	}

}