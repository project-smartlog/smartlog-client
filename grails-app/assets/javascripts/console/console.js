/*
 * Copyright 2016-2019
 *
 * Interreg Central Baltic 2014-2020 funded project
 * Smart Logistics and Freight Villages Initiative, CB426
 *
 * Kouvola Innovation Oy, FINLAND
 * Region Örebro County, SWEDEN
 * Tallinn University of Technology, ESTONIA
 * Foundation Valga County Development Agency, ESTONIA
 * Transport and Telecommunication Institute, LATVIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Main js-file for the client console
 */
$(function() {

    /**
     * Load custom parts for slider and tablesorter
     */
    loadCustomDateParserForTablesorter();
    loadCustomFilterForDateSlider();

    //  Open message max tab by default
    document.getElementById("defaultTab").click();

    //  Remove sorting from the message icon column
    $("table#myTable thead th:eq(0)").data("sorter", false);

    /**
     * Initialize tablesorter if there are messages to show
     */
    if ($(".js-message-table__message").length !== 0) {

        /**
         * Initialize tablesorter with filter-, saveSort- and sticyHeaders-widget.
         *
         * Set custom CSS-class for the filters so we can initialize the selects as selectize.
         */
        $("#myTable").tablesorter({
            sortList: [[1,1]],
            resort: false,
            widgets: ["saveSort", "filter", "stickyHeaders"],
            widgetOptions : {
                filter_cssFilter : 'js-custom-selectize',
                filter_searchFiltered: true,
                // enable/disable saveSort dynamically
                saveSort: true,
                filter_external : '.message-table-search',
            }
        }).tablesorterPager();

        //  Initialize filter selects and create selectized selects
        var selects = $('.js-custom-selectize').selectize({
            allowEmptyOption: true
        });

    }

    //  Initialize custom handle so we can manipulate its text from slider's functions
    var handle = $( "#custom-handle-count" );

    /**
     * Initialize slider, must use negative values so we can flip the vertical slider upside down.
     *
     * JQuery-slider is not created flipping in mind so this is kind of a hax.
     */
    $( "#slider-vertical-count" ).slider({
        orientation: "vertical",
        range: "min",
        min: -50,
        max: -5,
        value: 0,

        //  When handle is slided, update handle text
        slide: function( event, ui ) {
            handle.text(Math.abs(ui.value));
        },

        //  When slider is created, set default value to it
        //  Also set default to tablesorter
        create: function( event, ui ) {
            handle.text(Math.abs("5")); //  5 is the default value
            $("#myTable").trigger("pageAndSize", [0, 5]);
            //updateSelects(selects);
        },

        //  Set handle text and update tablesorter size with value selected
        change: function( event, ui ) {

            var value = Math.abs(ui.value);

            handle.text(value);
            $("#myTable").trigger("pageAndSize", [0, value]);
            //updateSelects(selects);
        },

        //  Set slider option "min" to -50
        //  Avoids creating havoc when user has clicked "Show all" and then starts to slide the slider
        start: function( event, ui ) {

            $(this).slider( "option", "min", parseInt("-" + 50) );

            //  Fixes bug with hovering of the range selector stops
            //  Removes pointer-events from the element
            $(".range-slider-stop").addClass("unselectable");
        },
        stop: function( event, ui ) {
            $(".range-slider-stop").removeClass("unselectable");
        }
    });

    //  Initialize custom handle for time slider so we can manipulate its text from slider's functions
    var timeHandle = $( "#custom-handle-time" );

    /**
     * Initialize slider, must use negative values so we can flip the vertical slider upside down.
     *
     * JQuery-slider is not created flipping in mind so this is kind of a hax.
     */
    $( "#slider-vertical-time" ).slider({
        orientation: "vertical",
        range: "min",
        min: -50,
        max: -5,
        value: 0,
        slide: function( event, ui ) {
            timeHandle.text(Math.abs(ui.value));
        },
        create: function( event, ui ) {
            timeHandle.text(Math.abs("5")); //  5 is the default value
        },
        change: function( event, ui ) {
            timeHandle.text(Math.abs(ui.value));
        },
        start: function( event, ui ) {
        },
        stop: function( event, ui ) {
        }
    });

    //  Initialize click events for max slider stops
    initMaxSliderStopsEvents();

    //  Initialize  click events for time slider stops
    initTimeSliderStopsEvents();

    //  Load tr-click events, aka what happens when user clicks message from list
    initMessageModalEvents();

    //  Init row information
    initRowInformation();

    //  Init logout click
    $(".js-logout").click(function() {

        var logoutUrl = $(".js-logout-url").val();

        window.location = logoutUrl;
    });

    // Init new message click
    $(".js-new-message-button").click(function() {

        $('#newMessageModal').modal();

    });

    // Check if mandatory field is changed and set it as valid
    $(".mandatory-field-invalid").change(function(){
        $(this).removeClass("mandatory-field-invalid").addClass("mandatory-field-valid");
        if($("#supply-chain-select").val()  && $("#empty-full-select").val()){
            $(".create-message-button").prop("disabled", false);
        }
    });


    // Send form information to addMessage function
    $(".create-message-button").click(function() {
        var valid = validateForm();
        if (valid == true){
            $.ajax({
                type: "POST",
                url: "/console/addMessage",
                data: $("#newMessageForm").serializeArray(),

                success: function () {
                    window.location="/console/index"
                },
                error: function () {

                }
            });
        }
        else {
            return false;
        }
    });
});

/**
 * Count and time filters are divided in own separate tabs.
 * @param evt
 * @param tabName
 */
function openTab(evt, tabName) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(tabName).style.display = "block";
    document.getElementById(tabName).className += " tab_" + tabName;
    evt.currentTarget.className += " active";
}

/**
 * Initialize click events for the slider stop buttons
 *
 * When slider stop is clicked, value gets read from the button and
 * sliders value gets modified using the slider's value-API
 */
function initMaxSliderStopsEvents() {

    $("#max .range-slider-stop").click(function() {

        var value = $(this).data("value");

        //  If user selects show all, set value from the count of rows in message table.
        if ($(this).hasClass("js-range-slider-stop-all")) {
            var trCount = $("#myTable tbody tr").size();
            value = trCount;
            $(this).closest(".tabcontent").find(".slider-vertical").slider( "option", "min", parseInt("-" + value) );

        } else {
            $(this).closest(".tabcontent").find(".slider-vertical").slider( "option", "min", parseInt("-" + 50) );
        }

        $(this).closest(".tabcontent").find(".slider-vertical").slider("value", "-" + value);
    });
}

/**
 * Init custom events for the time filter stop events
 */
function initTimeSliderStopsEvents() {

    $("#time .range-slider-stop").click(function() {

        var value = $(this).data("value");

        if ($(this).hasClass("js-range-slider-stop-all")) {
            setTimeFilter("");
        } else {
            setTimeFilter(value);
        }

        $("#time .range-slider-stop").each(function() {
            $(this).removeClass("selected");
        });

        if (!$(this).hasClass("js-range-slider-stop-all")) {
            $(this).addClass("selected");
        }

    });

}

/**
 * Binds click-event for the tr-elements of messagelist.
 *
 * When user clicks the row, modal with message properties / whole message gets shown.
 *
 * Modal needs to be populated here to get show each message correctly
 */
function initMessageModalEvents() {

    $("#myTable tbody tr").click(function() {

        var modalContent = $(this).data("content");
        $('#myModal').modal();
        $('#parameter-container-id').text($(this).data("containerid"));
        $('#parameter-supply-chain-id').text($(this).data("supplychainid"));
        $('#parameter-sender-party').text($(this).data("organisationid").substring(0, $(this).data("organisationid").length - 3));
        $('#parameter-status').text($(this).data("status"));
        $('#parameter-location').text($(this).data("location"));
        $('#parameter-container-rfid').text($(this).data("containerrfid"));
        $('#parameter-carrier-assigned-id').text($(this).data("carrierid"));
        $('#parameter-shipping-order-id').text($(this).data("shippingid"));
        $('#parameter-empty-full-indicator').text($(this).data("emptyfull"));
        $('#parameter-document-id').text($(this).data("documentid"));
        $('#parameter-timestamp').text($(this).data("timestamp"));
        $("#collapseOne .panel-body").html(formatXml(modalContent));
    });
}

/**
 * Add custom date parser to tablesorter. Gets datetime as pattern "d.M.yyyy H.mm"
 * and converts it to Epoch time for easy comparison.
 */
function loadCustomDateParserForTablesorter() {

    $.tablesorter.addParser({
        id: 'customDate',
        is: function(s, table, cell, $cell) {

            // return false so this parser is not auto detected
            return false;
        },
        format: function(s, table, cell, cellIndex) {

            // format your data for normalization
            var dateTime = $(cell).data("timestamp");
            return dateToEpoch(dateTime);
        },
        // set type, either numeric or text
        type: 'numeric'
    });
}

/**
 * Parses timestamp from UBL-messages to epoch time for easy comparison
 * @param dateTime
 * @returns {number}
 */
function dateToEpoch(dateTime) {

    var dateTimeSplit = dateTime.split(" ");

    var date = dateTimeSplit[0];
    var time = dateTimeSplit[1];

    var dateSplit = date.split("-");
    var fullTimeSplit = time.split("+");
    var time = fullTimeSplit[0];
    var timeSplit = time.split(":");

    var day = dateSplit[2];
    var month = dateSplit[1];
    var year = dateSplit[0];
    var hours = timeSplit[0];
    var minutes = timeSplit[1];
    var seconds = timeSplit[2];

    var d = new Date(year, (parseInt(month) - 1), day, hours, minutes, seconds, 0);

    return Date.parse(d);
}

/**
 * Gets hour value as dtX where X is numeric value.
 *
 * This filter fires only if filter-input contains dt at start.
 *
 * Gets current time and compares it to time in message.
 *
 * If currentTime - time from message < given hours in millisecond, return true
 *
 * Return true means that message is visible in table.
 */
function loadCustomFilterForDateSlider() {

    $.tablesorter.filter.types.datecompare = function( config, data ) {

        if ( /^dt/.test( data.iFilter ) && data.iFilter.length > 2 ) {

            var value = data.iFilter;
            var compareTime = dateToEpoch($(data.$cells[1]).data("timestamp"));
            var currentTime = (new Date).getTime();

            currentTime -= currentTime % 60000;
            compareTime -= compareTime % 60000;

            var hour = value.substring(2, value.length);
            hour = parseInt(hour);

            var hoursInMillis = hour*60*60*1000;
            var remains = currentTime - compareTime;

            return remains < hoursInMillis
        }

        // Tablesorter filters need to always return null if filter doesn't fire.
        return null;
    };
}

/**
 * TODO: is this even needed in console? because then we need to keep track of the messages that were watched?
 */
function initRowInformation() {

    var currentOrg = $(".js-current-org").val();

    $("#myTable tbody tr").each(function(index) {

        var sender = $(this).data("organisationId");
        var participants = $(this).data("participants");

        participants = participants.split(",");

        var currentIndex = $.inArray(currentOrg, participants);
        var senderIndex = $.inArray(sender, participants);
        var difference = currentIndex - senderIndex;

        //  This organization is the next in line, so it can forward the message
        if (difference === 1) {
            $(this).addClass("notification-forward-message");
        }
    });

}

/**
 * Pretty prints raw UBL-message
 * @param xml
 * @returns {string}
 */

// TODO: Fix formatting if tags don't have any content between them.

function formatXml(xml) {
    var formatted = '';
    var reg = /(>)(<)(\/*)/g;
    xml = xml.replace(reg, '$1\r\n$2$3');
    var pad = 0;
    jQuery.each(xml.split('\r\n'), function(index, node) {
        var indent = 0;
        if (node.match( /.+<\/\w[^>]*>$/ )) {
            indent = 0;
        } else if (node.match( /^<\/\w/ )) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
            indent = 1;
        } else {
            indent = 0;
        }

        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }

        formatted += padding + node + '\r\n';
        pad += indent;
    });

    var xml_escaped = formatted.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/ /g, '&nbsp;').replace(/\n/g,'<br />');

    return xml_escaped;
}

/**
 * Sets time filter with value dt + value
 *
 * Tablesorter then recognizes it and filters the results using selected time
 *
 * If time filter wants to be resetted, just pass empty string ("")
 *
 * @param value
 */
function setTimeFilter(value) {

    if (value != "") {
        $("#datetime-search").val("dt" + value);
    } else {
        $("#datetime-search").val("");
    }

    $("#datetime-search").blur();
}

/**
 * Validates the information given in the new message form
 *
 * Highlights the field that has an error in its value
 *
 * Highlighting removed once the given input is valid
 *
 * Validations done for every field if needed in the future
 *
 * Not needed if send message button is disabled until mandatory fields are filled
 *
 * @returns {boolean}
 */


function validateForm(){

    if(!document.newMessageForm.supplyChain.value){
        document.getElementById("message-error-text").innerHTML = "Choose a supply chain";
        document.getElementById("supply-chain-select").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
        document.getElementById("supply-chain-select").closest(".form-group").classList.remove("has-error");
    }
    /*if(document.newMessageForm.containerId.value.length < 1){
        document.getElementById("message-error-text").innerHTML = "Container name is mandatory";
        document.getElementById("container-name-input").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
        document.getElementById("container-name-input").closest(".form-group").classList.remove("has-error");
    }
    if(document.newMessageForm.statusLocationId.value.length < 1) {
        document.getElementById("message-error-text").innerHTML = "Location is mandatory";
        document.getElementById("status-location-input").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
            document.getElementById("status-location-input").closest(".form-group").classList.remove("has-error");
    }
    if(document.newMessageForm.rfidTransportEquipment.value.length < 1) {
        document.getElementById("message-error-text").innerHTML = "Location is mandatory";
        document.getElementById("container-rfid-input").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
        document.getElementById("container-rfid-input").closest(".form-group").classList.remove("has-error");
    }
    if(document.newMessageForm.statusTypeCode.value.length < 1) {
        document.getElementById("message-error-text").innerHTML = "Location is mandatory";
        document.getElementById("status-type-input").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
        document.getElementById("status-type-input").closest(".form-group").classList.remove("has-error");
    }
    if(document.newMessageForm.carrierAssignedId.value.length < 1) {
        document.getElementById("message-error-text").innerHTML = "Location is mandatory";
        document.getElementById("carrier-assigned-input").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
        document.getElementById("carrier-assigned-input").closest(".form-group").classList.remove("has-error");
    }
    if(document.newMessageForm.shippingOrderId.value.length < 1) {
        document.getElementById("message-error-text").innerHTML = "Location is mandatory";
        document.getElementById("shipping-order-input").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
        document.getElementById("shipping-order-input").closest(".form-group").classList.remove("has-error");
    }*/
    if(document.newMessageForm.emptyFullIndicator.value.length < 1) {
        document.getElementById("message-error-text").innerHTML = "Empty or full indicator is mandatory";
        document.getElementById("empty-full-select").closest(".form-group").classList.add("has-error");
        return (false);
    }
    else {
        document.getElementById("empty-full-select").closest(".form-group").classList.remove("has-error");
    }
    return (true);
}
