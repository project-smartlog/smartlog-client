<!doctype html>
<html>
<head>

    <asset:javascript src="application.js"/>

</head>
<body>

<div id="connectionStatus">
    <h1>Blockchain connector status</h1>
    <span>Connection working: ${alive}</span>
    <br>
    <g:if test="${channelName != null}">
        <span>Joined channel: ${channelName}</span>
    </g:if>
</div>

<hr/>

<div id="responseContainer">

</div>

</body>
</html>
