<!doctype html>
<html>
<head>
    <meta name="layout" content="main_layout"/>
    <asset:stylesheet src="bootstrap.css"/>
    <asset:stylesheet src="dashboard.css"/>
    <link href="//fonts.googleapis.com/css?family=Lato:100,200,300,400,500,600,700,800,900,100i,200i,300i,400i,500i,600i,700i,800i,900i" rel="stylesheet" type="text/css">
    <asset:stylesheet src="override.css"/>
</head>
<body>
    <div class="container-fluid  no-padding">
        <header id="header">
            <h2 class="text-uppercase  header__title">
                <span class="main-title">smartlog</span><span class="text-uppercase  subtitle"> dashboard</span>
            </h2>
        </header>
    </div>
    <div class="container-fluid">
        <div class="col-sm-3 col-md-2 sidebar">
            <ul class="nav nav-sidebar">
                %{--<li><a href="${createLink(controller: "management", action: "index")}">Overview</a></li>--}%
                <li><a href="${createLink(controller: "console", action: "index")}">Console</a></li>
                <li><a href="${createLink(controller: "api", action: "index")}">UBL API</a></li>
                <li><a href="${createLink(controller: "smartlogapi", action: "doc")}">SmartLog API</a></li>
                <li><a href="${createLink(controller: "installer", action: "index")}">Installer</a></li>
            </ul>
        </div>
    </div>
    <div class="container status-container">
        <div class="row status-row">
            <div class="status-text">
                <span class="text-uppercase">Connection to blockchain:</span>
            </div>
            <g:if  test="${statusList[0] == true}">
                <div class="status-icon green">
                    <span class="glyphicon glyphicon-ok"></span>
                </div>
            </g:if>
            <g:else>
                <div class="status-icon red">
                    <span class="glyphicon glyphicon-remove"></span>
                </div>
            </g:else>
        </div>
        <div class="row status-row">
            <div class="status-text">
                <span class="text-uppercase">Connection to couchdb:</span>
            </div>
            <g:if  test="${statusList[1] == true}">
                <div class="status-icon green">
                    <span class="glyphicon glyphicon-ok"></span>
                </div>
            </g:if>
            <g:else>
                <div class="status-icon red">
                    <span class="glyphicon glyphicon-remove"></span>
                </div>
            </g:else>
        </div>
        <div class="row status-row">
            <div class="status-text">
                <span class="text-uppercase">Connection to orderer:</span>
            </div>
            <g:if  test="${statusList[2] == true}">
                <div class="status-icon green">
                    <span class="glyphicon glyphicon-ok"></span>
                </div>
            </g:if>
            <g:else>
                <div class="status-icon red">
                    <span class="glyphicon glyphicon-remove"></span>
                </div>
            </g:else>
        </div>
        <div class="row status-row">
            <div class="status-text">
                <span class="text-uppercase">Connection to peer:</span>
            </div>
            <g:if  test="${statusList[3] == true}">
                <div class="status-icon green">
                    <span class="glyphicon glyphicon-ok"></span>
                </div>
            </g:if>
            <g:else>
                <div class="status-icon red">
                    <span class="glyphicon glyphicon-remove"></span>
                </div>
            </g:else>
        </div>
        <div class="button-wrapper">
            <g:link class="test-button" name="testButton" contoller="dashboard" action="runTests" >Test again</g:link>
        </div>
    </div>
</body>
</html>
