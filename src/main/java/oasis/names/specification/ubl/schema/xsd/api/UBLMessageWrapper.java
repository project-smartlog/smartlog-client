package oasis.names.specification.ubl.schema.xsd.api;

import oasis.names.specification.ubl.schema.xsd.billoflading_2.BillOfLading;
import oasis.names.specification.ubl.schema.xsd.forwardinginstructions_2.ForwardingInstructions;
import oasis.names.specification.ubl.schema.xsd.packinglist_2.PackingList;
import oasis.names.specification.ubl.schema.xsd.transportationstatus_2.TransportationStatus;
import oasis.names.specification.ubl.schema.xsd.transportationstatusrequest_2.TransportationStatusRequest;
import oasis.names.specification.ubl.schema.xsd.waybill_2.Waybill;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Response wrapper for UBL-messages.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Messages", namespace = "com.propentus.api.messages")
@XmlSeeAlso({ForwardingInstructions.class, Waybill.class, BillOfLading.class, PackingList.class, TransportationStatusRequest.class, TransportationStatus.class})
public class UBLMessageWrapper {

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> any;

    public List<Object> getMessage() {
        if(any == null) {
            any = new ArrayList<Object>();
        }
        return any;
    }

    public void addMessage(Object message) {
        getMessage().add(message);
    }
}
