<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN">
<!--
Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
Initial Developer: Gunsioo Group
-->
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <title>${text.a.title}</title>
    <link rel="stylesheet" type="text/css" href="stylesheet.css" />
</head>
<frameset cols="*" rows="36,*" frameborder="1" framespacing="4" border="${frameset-border}" bordercolor="white">
    <frame noresize="noresize" frameborder="0" marginheight="0" marginwidth="0" src="header.jsp?jsessionid=${sessionId}" name="header" scrolling="no" />
    <frameset cols="200,*" rows="*" frameborder="1" framespacing="4" border="${frameset-border}" bordercolor="white">
        <frame frameborder="0" marginheight="0" marginwidth="0" src="tables.do?jsessionid=${sessionId}" name="h2menu" />
        <frameset  rows="180,*" frameborder="1" framespacing="4" border="${frameset-border}" bordercolor="white">
            <frame frameborder="0" marginheight="0" marginwidth="0" src="query.jsp?jsessionid=${sessionId}" name="h2query" scrolling="no" />
            <frame frameborder="${frame-border}" marginheight="0" marginwidth="0" src="help.jsp?jsessionid=${sessionId}" name="h2result" />
        </frameset>
    </frameset>
</frameset>
<noframes>
<body>
    ${text.a.lynxNotSupported}
</body>
</noframes>
</html>
