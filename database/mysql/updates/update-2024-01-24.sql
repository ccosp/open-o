ALTER TABLE `billing_preferences` MODIFY `defaultPayeeNo` VARCHAR(11) NOT NULL DEFAULT '0';
ALTER TABLE `SystemPreferences` MODIFY `value` varchar(255);