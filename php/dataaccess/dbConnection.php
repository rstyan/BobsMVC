<?php

require_once('logging/SimpleLogger.php');
require_once('FileUtil.php');
require_once 'mvc/Properties.php';

class dbConnection {
	const confFile = "/conf/db.properties";

	private $connectParameters=null;
	private $myConnection;
	private $isConnected;
	private $logger;
	private static $db=null;
	
	public static function getInstance() {
		if (self::$db == null) {
			self::$db = new dbConnection();
		}
		return self::$db;
	}
	
	private function __construct() {
		$this->paramsLoaded = false;
		$this->isConnected = false;
		$this->logger = SimpleLogger::getInstance();
	}
	
	public function __get($name) {
	  return $this->$name;
	}
	
	private function getConnectionParameters() {
		if (is_null($this->connectParameters)) {
			$this->connectParameters = new Properties(self::confFile, array('dbServer', 'dbUser', 'dbPasswd', 'dbName'));
		}
		return $this->connectParameters->getParameters();
	}
	
	// Connects to mysql via parameters initialized in "conf.dat"
	public function connect() {
		if ($this->isConnected) {
			return true;
		}
		$cpms = $this->getConnectionParameters();
		if (is_null($cpms)) {
			return false;
		}
		@$this->myConnection = new mysqli($cpms['dbServer'], $cpms['dbUser'], $cpms['dbPasswd'], $cpms['dbName']);
		if (!$this->myConnection) {
			$this->logger->log("cannot connect to mysql: (".$cpms['dbServer'].", ".$cpms['dbUser'].", ".$cpms['dbPasswd'].", ".$cpms['dbName'].")");
            return false;
		}
		$this->isConnected = true;
		return true;
	}
	
	// An NR (non-result) query is one that only returns the number of rows
	// affected.
	public function executeNRQuery($query) {
		$query_result = null;
		$this->connect();
		if (!$this->isConnected) {
			return null;
		}
		$query_result = $this->myConnection->query($query);
		if (!$query_result) {
			$this->logger->log("SQL ERROR: " . $this->myConnection->error);
			return null;
		}
		return $this->myConnection->affected_rows;
	}
	
	public function getLastIdGenerated() {
		$id = null;
		$id_result = $this->myConnection->query("select last_insert_id()");
		if (!$id_result) {
			echo "SQL ERROR: " . $this->myConnection->error . "\n";
			$this->logger->log("SQL ERROR: " . $this->myConnection->error);
			return null;
		}
		if ($id_result->num_rows == 1) {
			$row = $id_result->fetch_row();
			$id=$row[0];
		}
		$id_result->close();
		return $id;
	}
	
	// The results will be returned as a list of
	// QueryResult objects, as specified by $class.
	public function executeQuery($query, $class) {
		$query_result = null;
		$results = array();
		$this->connect();
		if (!$this->isConnected) {
			return null;
		}
		$query_result = $this->myConnection->query($query);
		if (!$query_result) {
			$this->logger->log("SQL ERROR: " . $this->myConnection->error);
			echo "SQL ERROR: " . $this->myConnection->error . "\n";
			return null;
		}
		for ($i=0; $i<$query_result->num_rows; $i++) {
			$obj = new $class();
			$obj->populate($query_result->fetch_assoc());
			$results[$i] = $obj;
		}
		$query_result->close();
		return $results;
	}
	
}

?>