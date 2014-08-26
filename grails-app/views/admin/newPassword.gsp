<!DOCTYPE html>
<html>
<head>
    <title><g:message code="springSecurity.login.title"/></title>
    <meta name="layout" content="core"/>
    <r:require modules="core"/>
    <r:layoutResources/>

</head>

<body>

<div id="main">

    <div class="container">

        <g:if test='${flash.message}'>
            <div class="alert alert-danger">${flash.message}</div>
        </g:if>

        <div class="row">
            <div class="col-md-8 col-md-offset-2 login-header">
                <h1><p>We need to reset your password. Please enter a new one</p></h1>
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 col-md-offset-3">
                <g:form action='updatePassword' method='POST' id='passwordResetForm' class='form form-horizontal cssform' autocomplete='off'>

                    <div class="form-group">
                        <label class="control-label col-sm-3" id="id_email">Username:</label>

                        <div class="col-sm-8">
                            <span class='form_control' name='j_username' id='username'>${username}</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="control-label col-sm-3" for="oldPassword">Current password:</label>

                        <div class="col-sm-8">
                            <input type='password' class='text_' name='oldPassword' id='oldPassword'/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="control-label col-sm-3" for="newPassword">New password:</label>

                        <div class="col-sm-8">
                            <input type='password' class='text_' name='newPassword' id='newPassword'/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="control-label col-sm-3" for="newPassword2">New password (again):</label>

                        <div class="col-sm-8">
                            <input type='password' class='text_' name='newPassword2' id='newPassword2'/>
                        </div>
                    </div>

                    <div style="text-align:center; padding-top: 20px;">
                        <input class="btn btn-primary btn-lg" type='submit' id="submit"
                               value='Reset'/>
                    </div>

                </g:form>

            </div>
        </div>
    </div>

</div>
<script type='text/javascript'>
    <!--
    (function() {
        $('#oldPassword').focus();
    })();
    // -->
</script>
</body>
</html>