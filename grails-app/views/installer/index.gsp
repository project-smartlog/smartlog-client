<!doctype html>
<html>
<head>
    <meta name="layout" content="main_layout"/>
    <asset:stylesheet src="bootstrap.css"/>
    <asset:stylesheet src="dashboard.css"/>
    <link href="//fonts.googleapis.com/css?family=Lato:100,200,300,400,500,600,700,800,900,100i,200i,300i,400i,500i,600i,700i,800i,900i" rel="stylesheet" type="text/css">
    <asset:stylesheet src="override.css"/>
    <asset:javascript src="installer/installer.js" />
</head>
<body>
    <div class="container-fluid  no-padding">
        <header id="header">
            <h2 class="text-uppercase  header__title">
                <span class="main-title">smartlog</span><span class="text-uppercase  subtitle"> installer</span>
            </h2>
        </header>
    </div>
    <div class="container">
        <hr>
        <g:uploadForm class="install-form" action="install" method="post">
            <div class="row">
                <div class="col-md-4 col-md-offset-2 form-div-left">
                    <label class="input-label" for="dockerRegistryUser" style="text-align: center">Docker registry username</label>
                    <input type="text" class="form-control" name="dockerRegistryUser" id="dockerRegistryUser" placeholder="Enter docker registry username">
                </div>
                <div class="col-md-4 form-div-right">
                    <label class="input-label" for="dockerRegistryPassword">Docker registry password</label>
                    <input type="password" class="form-control" name="dockerRegistryPassword" id="dockerRegistryPassword" placeholder="Enter docker registry password">
                </div>
            </div>
            <div class="row">
                <div class="col-md-4 col-md-offset-2 form-div-left">
                    <label class="input-label" for="configPassword">Config package password</label>
                    <input type="text" class="form-control" name="configPassword" id="configPassword" placeholder="Enter config package password">
                </div>
                <div class="col-md-4 form-div-right">
                    <label class="input-label" for="configPackage">Config file</label>
                    <input class="form-control-file" type="file" id="configPackage" name="configPackage"/>
                    <p class="help-block">Upload config file</p>
                </div>
            </div>
            <div class="button-wrapper">
                <button type="submit" class="installer-button install-button" name="installMessageButton">Install</button>
            </div>
        </g:uploadForm>
        <p/>
        <div class="button-wrapper">
            <a class="installer-button dashboard-button" name="dashboardButton" href="${createLink(controller: "dashboard", action: "index")}">To dashboard</a>
        </div>
        <hr>
    </div>
    <div class="js-overlay overlay">
    </div>
    <div class="js-sk-fading-circle sk-fading-circle">
        <div class="sk-circle1 sk-circle"></div>
        <div class="sk-circle2 sk-circle"></div>
        <div class="sk-circle3 sk-circle"></div>
        <div class="sk-circle4 sk-circle"></div>
        <div class="sk-circle5 sk-circle"></div>
        <div class="sk-circle6 sk-circle"></div>
        <div class="sk-circle7 sk-circle"></div>
        <div class="sk-circle8 sk-circle"></div>
        <div class="sk-circle9 sk-circle"></div>
        <div class="sk-circle10 sk-circle"></div>
        <div class="sk-circle11 sk-circle"></div>
        <div class="sk-circle12 sk-circle"></div>
    </div>
    <div class="install-text-container">
        <h2 class="js-install-text install-text">INSTALLING SMARTLOG</h2>
    </div>
</body>
</html>
