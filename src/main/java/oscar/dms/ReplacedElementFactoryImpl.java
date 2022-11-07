package oscar.dms;


import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;

import org.xhtmlrenderer.pdf.*;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An override implementation of the Flying Saucer HTMLtoPDF ReplacedElementFactory class
 * that handles scaling of embedded images more accurately.
 */
public class ReplacedElementFactoryImpl implements ReplacedElementFactory {

    private static Logger logger = MiscUtils.getLogger();

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox, UserAgentCallback userAgentCallback, int width, int height) {
        Element e = blockBox.getElement();
        if (e == null) {
            return null;
        }
        String nodeName = e.getNodeName();
        if (nodeName.equals("img")) {
            String attribute = e.getAttribute("src");
            FSImage fsImage;
            try {
                fsImage = imageForPDF(attribute, userAgentCallback);
            } catch (BadElementException e1) {
                fsImage = null;
                logger.error("", e);
            } catch (IOException e1) {
                fsImage = null;
                logger.error("", e);
            }
            if (fsImage != null) {
                if (width != -1 || height != -1) {
                    fsImage.scale(width, height);
                }
                return new ITextImageElement(fsImage);
            }
        }
        if(nodeName.equals("input")) {
            return new TextFormField(layoutContext, blockBox, width, height);
        }

        return blockBox.getReplacedElement();
    }

    protected final FSImage imageForPDF(String attribute, UserAgentCallback uac) throws IOException, BadElementException{
        FSImage fsImage;
        try (InputStream input = new FileInputStream(attribute)) {
            byte[] bytes = IOUtils.toByteArray(input);
            Image image = Image.getInstance(bytes);
            fsImage = new ITextFSImage(image);
        }
        return fsImage;
    }

    @Override
    public void reset() {
        // override
    }

    @Override
    public void remove(Element element) {
        // override
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener formSubmissionListener) {
        // override
    }
}
