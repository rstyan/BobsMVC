<?php
//require_once('FirePHPCore/FirePHP.class.php');

class SimpleLogger {
	
	private static $testMode = false;
	private static $instance = null;
	
	public function log($msg) {
//		echo "logging: ".$msg."\n";
	}

	public static function getInstance() {
//		if (self::$testMode) {
			if (is_null(self::$instance)) {
				self::$instance = new SimpleLogger();
			}
			return self::$instance;
//		}
//    	return FirePHP::getInstance(true);
    }
    
    public static function setTestMode($mode=true) {
    	self::$testMode = $mode;
    }
    
}
?>