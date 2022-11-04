# onDialogClose

Excerpt taken from [https://github.com/RestComm/jss7/blob/master/docs/userguide/sources-asciidoc/src/main/asciidoc/Chapter-CAP.adoc]

After all incoming components have been processed, the event onDialogDelimiter(CAPDialog capDialog); event is invoked (or onDialogClose(CAPDialog capDialog) in TC-END case). If all response components have been prepared you can tell the Stack to send response:

* capDialog.close(false); - to send TC-END
* capDialog.send(); - to send TC-CONTINUE
* capDialog.close(true); - sends TC-END without any components (prearrangedEnd)
