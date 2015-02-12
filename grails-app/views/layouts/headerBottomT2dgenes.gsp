<div id="header-bottom">
    <div class="container">
        <sec:ifLoggedIn>
            <div class="rightlinks">
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <g:link controller='admin' action="users" class="mgr">manage  users</g:link>
                    &middot;
                </sec:ifAllGranted>
                <sec:ifAllGranted roles="ROLE_SYSTEM">
                    <g:link controller='system' action="systemManager">System Mgr</g:link>
                    &middot;
                </sec:ifAllGranted>
                <sec:loggedInUserInfo field="username"/>   &middot;
                <g:link controller='logout'><g:message code="mainpage.log.out"/></g:link>
            </div>
        </sec:ifLoggedIn>
        <sec:ifNotLoggedIn>
            <div class="rightlinks">
                <oauth:connect provider="google" id="google-connect-link"><g:message code="google.log.in"/></oauth:connect>
            </div>
        </sec:ifNotLoggedIn>
        <g:renderSigmaSection>
            <a href="${createLink(controller:'home',action:'portalHome')}"><g:message code="localized.home"/></a> &middot;
            <a href="${createLink(controller:'informational', action:'aboutSigma')}"><g:message code="localized.aboutTheData"/></a> &middot;
            <a href="${createLink(controller:'informational', action:'contact')}"><g:message code="localized.contact"/></a>
        </g:renderSigmaSection>
        <g:renderNotSigmaSection>
            <a href="${createLink(controller:'home',action:'portalHome')}"><g:message code="localized.home"/></a> &middot;
            <a href="${createLink(controller:'informational', action:'about')}"><g:message code="portal.header.nav.about"/></a> &middot;
            <a href="#"><g:message code="portal.header.nav.tutorials"/></a> &middot;
            <a href="${createLink(controller:'informational', action:'policies')}"><g:message code="portal.header.nav.policies"/></a> &middot;
            <a href="${createLink(controller:'informational', action:'contact')}"><g:message code="portal.header.nav.contact"/></a>

        </g:renderNotSigmaSection>
    </div>
</div>