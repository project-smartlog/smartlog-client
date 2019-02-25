<!doctype html>
<html>
    <head>

        <meta name="layout" content="main_layout"/>
        <asset:javascript src="console/console.js"/>
        <asset:stylesheet src="tablesorter/theme.default.min.css"/>
        <asset:javascript src="tablesorter/jquery.tablesorter.js"/>
        <asset:javascript src="tablesorter/jquery.tablesorter.widgets.min.js"/>
        <asset:javascript src="tablesorter/jquery.tablesorter.pager.js"/>
        <asset:javascript src="selectize/selectize.min.js"/>
        <asset:stylesheet src="selectize/selectize.css"/>
        <%--TODO: load to disk, because what if CDN is slow or unavailable?--%>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
        <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    </head>
    <body>

        <input type="hidden" id="current-org" class="js-current-org" value="${currentOrg}"/>
        <input type="hidden" id="all-participants" class="js-all-participants" value="${allParticipants.collect{"${it}"}.join(",")}"/>
        <input type="hidden" id="logout-url" class="js-logout-url" value="${createLink(controller: "console", action: "logout")}"/>

        <div class="container-fluid  no-padding">
            <header id="header">
                <h2 class="text-uppercase  header__title">
                    <span class="main-title">smartlog</span><span class="text-uppercase  subtitle"> client console</span>
                </h2>
                <span id="orgName">${currentOrg.substring(0, (currentOrg.size() - 3))}</span>
                <g:if test="${isCloud}">
                    <span id="logout" class="js-logout  glyphicon glyphicon-log-out"></span>
                </g:if>
            </header>
            <div class="content  container-fluid">
                <div class="section-label">
                    <label class="text-uppercase browse-text">browse messages</label>
                    <div class="new-message-button  js-new-message-button">
                            <span class="glyphicon glyphicon-plus new-message-icon"></span>
                            <p class="new-message-text">NEW MESSAGE</p>
                    </div>
                </div>

                <div class="messages-search row">
                    <div class="messages-search__count-time-filter col-sm-1">
                        <!-- Tab links -->
                        <div class="tab">
                            <button id="defaultTab" class="tablinks" onclick="openTab(event, 'max')"><g:render template="/svg/messages_icon"/></button>
                            <button class="tablinks" onclick="openTab(event, 'time')"><g:render template="/svg/time_icon"/></button>
                        </div>

                        <!-- Tab content -->
                        <div id="max" class="tabcontent  message-search__count-container">

                            <div id="slider-vertical-count" class="slider-vertical">
                                <div id="custom-handle-count" class="ui-slider-handle  slider-handle"></div>
                            </div>

                            <div class="range-slider-stop  range-slider-stop--5  js-range-slider-stop-5" data-value="5">5</div>
                            <div class="range-slider-stop  range-slider-stop--10  js-range-slider-stop-10" data-value="10">10</div>
                            <div class="range-slider-stop  range-slider-stop--25  js-range-slider-stop-25" data-value="25">25</div>
                            <div class="range-slider-stop  range-slider-stop--50  js-range-slider-stop-50" data-value="50">50</div>
                            <div class="range-slider-stop  range-slider-stop--all  js-range-slider-stop-all" data-value="all">SHOW ALL</div>

                        </div>

                        <div id="time" class="tabcontent  message-search__time-container">

                            <div id="slider-vertical-time" class="slider-vertical">
                                <div id="custom-handle-time" class="ui-slider-handle  slider-handle"></div>
                            </div>

                            <div class="range-slider-stop  range-slider-stop--5  js-range-slider-stop-1-hour" data-value="1">Last hour</div>
                            <div class="range-slider-stop  range-slider-stop--10  js-range-slider-stop-6-hours" data-value="6">Last 6 hours</div>
                            <div class="range-slider-stop  range-slider-stop--25  js-range-slider-stop-12-hours" data-value="12">Last 12 hours</div>
                            <div class="range-slider-stop  range-slider-stop--50  js-range-slider-stop-24-hours" data-value="24">Last 24 hours</div>
                            <div class="range-slider-stop  range-slider-stop--all  js-range-slider-stop-all" data-value="all">SHOW ALL</div>

                        </div>
                    </div>
                    <div class="col-sm-11 messages-search__table">

                        <g:if test="${messages}">

                            <%-- MESSAGE FILTERS --%>
                            <input type="search" id="datetime-search" class="message-table-search" value="" data-column="1"/>

                            <table id="myTable" class="tablesorter  message-table">
                                <thead>
                                <tr>
                                    <th class="filter-false" data-placeholder="All"></th>
                                    <th class="filter-false" data-placeholder="All" data-sorter="customDate">TIME</th>
                                    <th class="filter-select  filter-onlyAvail" data-placeholder="All">SENDER</th>
                                    <th class="filter-select  filter-onlyAvail" data-placeholder="All">STATUS</th>
                                    <th class="filter-select  filter-onlyAvail" data-placeholder="All">CONTAINER</th>
                                    <th class="filter-select  filter-onlyAvail" data-placeholder="All">SHIPPING ORDER</th>
                                    <th class="filter-select  filter-onlyAvail" data-placeholder="All">SUPPLY CHAIN</th>
                                </tr>
                                </thead>
                                <tbody>
                                
                                <%-- LIST FOUND MESSAGES --%>
                                <g:each in="${messages}" var="${message}">
                                    <tr class="message-table__message
                                        js-message-table__message"
                                        data-participants="${message.getParticipants().collect{"${it.getMSPID()}"}.unique().join(",")}"
                                        data-content="${message.getDecryptedMessage()}"
                                        data-containerid="${message.getContainerID()}"
                                        data-supplychainid="${message.getSupplyChainID()}"
                                        data-organisationid="${message.getOrganisationID()}"
                                        data-status="${message.getStatusTypeCode()}"
                                        data-location="${message.getStatusLocationId()}"
                                        data-containerrfid="${message.getRFIDTransportEquipment()}"
                                        data-carrierid="${message.getCarrierAssignedID()}"
                                        data-shippingid="${message.getShippingOrderID()}"
                                        data-emptyfull="${message.getEmptyFullIndicator()}"
                                        data-documentid="${message.getDocumentID()}"
                                        data-timestamp="${message.getTimestamp()}">

                                        <td><g:render template="/svg/message_icon"/></td>
                                        <td data-timestamp="${message.getTimestamp()}">${message.getTimestamp()}</td>
                                        <td>${message.getOrganisationID().substring(0, (message.getOrganisationID().size() - 3))}</td>
                                        <td>${message.getStatusTypeCode()}</td>
                                        <td>${message.getContainerID()}</td>
                                        <td>${message.getShippingOrderID()}</td>
                                        <td>${message.getSupplyChainID()}</td>
                                    </tr>
                                </g:each>

                                </tbody>
                            </table>

                        </g:if>
                        <g:else>
                            <h1>No messages found</h1>
                        </g:else>

                    </div>
                </div>
            </div>
        </div>

        <%-- CONSOLES MODALS --%>
        <g:render template="/console/modals"/>
    </body>
</html>
