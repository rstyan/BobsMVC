<?php
require_once 'QueryResult.php';

class ResultSet implements QueryResult {
	public $queryResult;
	private $attributes = array();
	
	public function __construct() {
		$this->queryResult = array();
	}
	
	public function __clone() {
		$this->attributes = $this->attributes;
		$this->queryResult = $this->queryResult;
	}
	
	public function __get($name) {
		if (!array_key_exists($name, $this->queryResult)) {
	  	 	$file_info = debug_backtrace();
			die("Fatal Error: $name is not a valid attribute; in file ". $file_info[0]['file'] . " at line " . $file_info[0]['line'] . "\n");
		}
		return $this->queryResult[$name];
	}
	
	public function populate($resultSet) {
		foreach ($resultSet as $key=>$value) {
			$this->queryResult[$key] = $value;
			$this->attributes[] = $key;
		}
		return array();
	}
	
	public function hasProperty($key) {
		return array_key_exists($key, $this->queryResult);
	}

	public function getPropertyNames() {
		return $this->attributes;
	}

	public function getNumber($key, $dec=2, $prefix='') {
		$formattedValue = 'unknown';
		$value = $this->$key;
		if (!is_null($value) && is_numeric($value)) {
			$formattedValue = number_format($value, $dec);
		}
		return  $prefix . $formattedValue;
	}
	
	public function toArray() {
		return $this->queryResult;
	}
}
?>