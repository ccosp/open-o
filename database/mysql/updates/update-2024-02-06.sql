INSERT INTO `secObjectName` (`objectName`, `description`, `orgapplicable`) VALUES ('_admin.email', 'Configure & Manage Emails', '0');
INSERT INTO `secObjPrivilege` VALUES ('admin','_admin.email','x',0,'999998');

INSERT INTO `secObjectName`(`objectName`, `description`, `orgapplicable`) VALUES ('_email', 'Send and Receive Emails', 0);
INSERT INTO `secObjPrivilege`(`roleUserGroup`, `objectName`, `privilege`, `priority`, `provider_no`) VALUES ('-1', '_email', 'x', 0, '999999');
INSERT INTO `secObjPrivilege`(`roleUserGroup`, `objectName`, `privilege`, `priority`, `provider_no`) VALUES ('admin', '_email', 'x', 0, '999998');
INSERT INTO `secObjPrivilege`(`roleUserGroup`, `objectName`, `privilege`, `priority`, `provider_no`) VALUES ('doctor', '_email', 'x', 0, '999998');