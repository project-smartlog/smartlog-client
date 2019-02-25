<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <!-- Bootstrap core CSS -->
    <asset:stylesheet src="bootstrap.css"/>
    <asset:stylesheet src="dashboard.css"/>

    <link href="//fonts.googleapis.com/css?family=Lato:100,200,300,400,500,600,700,800,900,100i,200i,300i,400i,500i,600i,700i,800i,900i" rel="stylesheet" type="text/css">

    <script src="//code.jquery.com/jquery-2.2.4.min.js" integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44=" crossorigin="anonymous"></script>
    <g:layoutHead/>

    <!-- Override CSS -->
    <asset:stylesheet src="override.css"/>
</head>

<body>

    <g:layoutBody/>

<div id="alert" class="alert alert-warning alert-dismissible fade in col-md-6 notification js-notification js-alert" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">×</span>
    </button>
    <p id="warning-message" class="js-warning-message">${flash.warning}</p>
</div>

<div id="error" class="alert alert-danger alert-dismissible fade in col-md-6 notification js-notification js-error" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">×</span>
    </button>
    <p id="error-message" class="js-error-message">${flash.error}</p>
</div>

<div id="success" class="alert alert-success alert-dismissible fade in col-md-6 notification js-notification js-success" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">×</span>
    </button>
    <p id="success-message" class="js-success-message">${flash.success}</p>
</div>

<div id="overlay" class="overlay  js-overlay"></div>

<!-- Bootstrap core JavaScript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<asset:javascript src="bootstrap.js"/>
</body>
</html>
