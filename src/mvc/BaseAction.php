<?php
require_once 'logging/SimpleLogger.php';

abstract class BaseAction {
	
	protected $logger;
	
	public function __construct() {
		$this->logger = SimpleLogger::getInstance();
	}
	
	public function __get($name) {
		return $this->$name;
	}

	public abstract function doAction($form, $post, $get, $session, $server);
	
	// Validate the input data; return true if its okay, false otherwise
    // By default to do nothing
	public function isValidData($form, $post, $get) {
		return true;
	}
	
	// When validation fails, an alternate action is invoked
	// By default it is the same as the original.
	public function doAlternateAction($form, $post, $get, $session, $server) {
		return $this->doAction($form, $post, $get, $session, $server);
	}
	
}
?>