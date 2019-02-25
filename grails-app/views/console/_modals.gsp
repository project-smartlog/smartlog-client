<%-- Message modal --%>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Message content</h4>
            </div>
            <div class="modal-body">

                <h2>Parameters</h2>
                <div class="message-parameters">
                    <div class="message-parameter-container">
                        <label for="parameter-container-id" class="parameter-label">Container ID: </label><span id="parameter-container-id"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-supply-chain-id" class="parameter-label">Supply chain ID:</label><span id="parameter-supply-chain-id"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-sender-party" class="parameter-label">Sender party:</label><span id="parameter-sender-party"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-status" class="parameter-label">Status:</label><span id="parameter-status"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-location" class="parameter-label">Location:</label><span id="parameter-location"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-container-rfid" class="parameter-label">Container RFID:</label><span id="parameter-container-rfid"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-carrier-assigned-id" class="parameter-label">Carrier assigned ID:</label><span id="parameter-carrier-assigned-id"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-shipping-order-id" class="parameter-label">Shipping order ID:</label><span id="parameter-shipping-order-id"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-empty-full-indicator" class="parameter-label">Empty or full:</label><span id="parameter-empty-full-indicator"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-document-id" class="parameter-label">Document ID:</label><span id="parameter-document-id"></span>
                    </div>
                    <div class="message-parameter-container">
                        <label for="parameter-timestamp" class="parameter-label">Timestamp:</label><span id="parameter-timestamp"></span>
                    </div>
                </div>

                <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
                    <div class="panel panel-default">

                        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-controls="collapseOne" aria-expanded="false">
                            <div class="panel-heading" role="tab" id="headingOne">
                                <h4 class="panel-title">Raw UBL-message</h4>
                            </div>
                        </a>

                        <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
                            <div class="panel-body">

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- New message modal --%>
<div class="modal fade" id="newMessageModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog  modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title bold-text" id="newMessageModalLabel">NEW MESSAGE</h4>
            </div>
            <div class="modal-body">

                <form id="newMessageForm" name="newMessageForm">
                    <div class="row">
                        <div class="form-group  col-xs-6">
                            <label for="supply-chain-select" class="bold-text">SUPPLY CHAIN*</label>
                            <select id="supply-chain-select" class="form-control mandatory-field-invalid" name="supplyChain">
                                <option value="" disabled selected>Choose chain</option>
                                <g:each in = "${orgChains}" var = "chain">
                                    <option value="${chain.id}">${chain.id}
                                    (
                                    <g:each in = "${chain.participants}" var = "part">
                                        ${part.substring(0, part.length() -3)}
                                        <g:if test = "${!(part == chain.participants.last())}" >
                                            ,
                                        </g:if>
                                    </g:each>
                                    )
                                    </option>
                                </g:each>
                            </select>
                            <p class="instruction-text">The supply chain this shipment belongs to. Only companies that belong to the selected supply chain can view the message.</p>
                        </div>
                        <div class="form-group  col-xs-6">
                            <label for="container-name-input" class="bold-text">CONTAINER NAME</label>
                            <input id="container-name-input" class="form-control" name="containerId" />
                            <p class="instruction-text">Unique identifier for the container or other transport equipment. Use standard identifiers (for example ISO 6346) if possible.</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="form-group  col-xs-6">
                            <label for="status-location-input" class="bold-text">LOCATION</label>
                            <input id="status-location-input" class="form-control" name="statusLocationId"/>
                            <p class="instruction-text">A location identifier (for example city and/or country) for this shipment.</p>
                        </div>
                        <div class="form-group  col-xs-6">
                            <label for="container-rfid-input" class="bold-text">CONTAINER RFID</label>
                            <input id="container-rfid-input" class="form-control" name="rfidTransportEquipment"/>
                            <p class="instruction-text">Unique RFID identifier for the container or other transport equipment. Use standard identifiers (for example GS1 EPC) if possible.</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="form-group  col-xs-6">
                            <label for="status-type-input" class="bold-text">STATUS</label>
                            <input id="status-type-input" class="form-control" name="statusTypeCode"/>
                            <p class="instruction-text">Status description for this shipment. Insert if needed.</p>
                        </div>
                        <div class="form-group  col-xs-6">
                            <label for="carrier-assigned-input" class="bold-text">CARRIER ASSIGNED ID</label>
                            <input id="carrier-assigned-input" class="form-control" name="carrierAssignedId"/>
                            <p class="instruction-text">An id assigned by a carrier or its agent to identify a specific shipment. Insert if needed.</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="form-group  col-xs-6">
                            <label for="shipping-order-input" class="bold-text">SHIPPING ORDER ID</label>
                            <input id="shipping-order-input" class="form-control" name="shippingOrderId"/>
                            <p class="instruction-text">A reference number to identify a Shipping Order.</p>
                        </div>
                        <div class="form-group  col-xs-6">
                            <label for="empty-full-select" class="bold-text">EMPTY OR FULL*</label>
                            <select id="empty-full-select" class="form-control mandatory-field-invalid" name="emptyFullIndicator">
                                <option value="" disabled selected>Choose option</option>
                                <option value="Full">Full</option>
                                <option value="Empty">Empty</option>
                            </select>
                            <p class="instruction-text">Choose "Full" for a loaded and "Empty" for an empty container or other transport equipment.</p>
                        </div>
                    </div>
                    <div class="errors row">
                        <span class="message-error-text" id="message-error-text"> </span>
                    </div>
                    <div class="new-message-buttons-container row">
                        <button type="button" class="create-message-button" disabled=true name="createMessageButton">Send</button>
                        <button class="cancel-message-button" data-dismiss="modal">Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
