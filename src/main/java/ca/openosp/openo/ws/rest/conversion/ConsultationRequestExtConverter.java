//CHECKSTYLE:OFF
package ca.openosp.openo.ws.rest.conversion;

import ca.openosp.openo.common.model.ConsultationRequestExt;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ws.rest.to.model.ConsultationRequestExtTo1;

public class ConsultationRequestExtConverter extends AbstractConverter<ConsultationRequestExt, ConsultationRequestExtTo1> {
    @Override
    public ConsultationRequestExt getAsDomainObject(LoggedInInfo loggedInInfo, ConsultationRequestExtTo1 t) throws ConversionException {
        ConsultationRequestExt d = new ConsultationRequestExt();

        //d.setId(t.getId());
        if (t.getRequestId() != null) {
            d.setRequestId(t.getRequestId());
        }
        d.setKey(t.getKey());
        d.setValue(t.getValue());
        d.setDateCreated(t.getDateCreated());

        return d;
    }

    @Override
    public ConsultationRequestExtTo1 getAsTransferObject(LoggedInInfo loggedInInfo, ConsultationRequestExt d) throws ConversionException {
        ConsultationRequestExtTo1 t = new ConsultationRequestExtTo1();

        t.setId(d.getId());
        t.setRequestId(d.getRequestId());
        t.setKey(d.getKey());
        t.setValue(d.getValue());
        t.setDateCreated(d.getDateCreated());

        return t;
    }
}