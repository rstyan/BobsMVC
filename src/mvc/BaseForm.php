<?php
require_once "dataaccess/Table.php";
require_once "dataaccess/ResultSet.php";
require_once "logging/SimpleLogger.php";
require_once "dataaccess/Map.php";

class BaseForm extends Map {
	
    private $errors;
    private $messages;
	protected $logger;
    private $includeFile;
	public $page;
	public $title;
	
    public function __construct($page="", $defaultTitle="") {
    	parent::__construct();
    	$this->errors = array();
    	$this->messages = array();
		$this->logger = SimpleLogger::getInstance();
		$this->page = $page;
		$this->title = $defaultTitle;
    }
    
	public function hasErrors() {
    	return !empty($this->errors);
    }
    
	public function hasMessages() {
    	return !empty($this->messages);
    }
    
    public function getMessages() {
    	return $this->messages;
    }
    
    public function addMessage($msg) {
    	$this->messages[] = $msg;
    }
    
    public function hasError($errName) {
    	return !empty($this->errors) && array_key_exists($errName, $this->errors);
    }
    
    public function getError($errName, $prefix=null) {
    	return $this->hasError($errName)?($prefix . $this->errors[$errName]):"$errName is not a valid error condition";
    }
    
    public function addError($name, $msg) {
    	$this->errors[$name] = $msg;
    }
    
    public function addErrors($errs) {
    	foreach ($errs as $key=>$value) {
    		$this->errors[$key] = $value;
    	}
    }
    
    public function getErrors() {
    	return $this->errors;
    }
    
    //maybe there should be an array of include files?
    public function forwardTo($page, $include="") {
    	$this->page = $page;
    	$this->includeFile = $include;
    }
    
    public function setTitle($title) {
    	$this->title = $title;
    }

    public function dateToString($timestamp) {
	  	return date("M. d, Y g:i:s A T", $timestamp);
	}
	
}
?>