<!doctype html>
<html>
    <head>
        <meta name="layout" content="main_layout"/>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
    </head>
    <body>

        <div class="login-form">
            <form id="login-form" name="login-form" action="${createLink(controller: "console", action: "auth")}" method="post">
                <div class="row">
                    <div class="form-group">
                        <label for="login-username">Username</label>
                        <input id="login-username" class="form-control" name="username" autofocus/>

                    </div>
                </div>
                <div class="row">
                    <div class="form-group">
                        <label for="login-password">Password</label>
                        <input id="login-password" class="form-control" name="password" type="password"/>
                    </div>
                </div>
                <div class="row">
                    <button type="submit" class="btn btn-success">Login</button>
                </div>
            </form>
        </div>

    </body>

</html>