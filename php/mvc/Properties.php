<?php
require_once 'logging/SimpleLogger.php';
require_once 'dataaccess/FileUtil.php';

class Properties {
	private $parameters;
	private $hasLoaded;
	private $logger;
	private $pfile;
	private $properties; // array of property names
	
	public function __construct($pfile, $props) {
		$this->logger = SimpleLogger::getInstance();
		$this->hasLoaded = false;
		$this->parameters = null;
		$this->pfile = $pfile;
		$this->properties = $props;
	}
	
	public function __get($name) {
		if (array_key_exists($name, $this->parameters)) {
			return $this->parameters[$name];
		}
		$this->logger->log("__get:invalid property: " . $name);
		return null;
	}
	
	public function __set($name, $value) {
		if (in_array($name, $this->properties)) {
			$this->propertes[$name] = $value;
		}
		$this->logger->log("__set:invalid property: " . $name);
	}
	
	private function validateParameter($k) {
		if (is_null($this->parameters[$k])) {
			$this->logger->log("missing parameter: " . $k);
			return false;
		}
		return true;
	}
	  	
	private function loadParameters($fileName) {
		if (!file_exists($fileName)) {
			$this->logger->log("parameter file (" . $fileName . ") does not exits");
			return false;
		}
        $lines = file($fileName);  
        foreach ($lines as $line) {
          list($k, $v) = explode('=', $line);
          $this->parameters[trim($k)] = trim($v);
        }
        $validParameters = true;
        foreach ($this->properties as $p) {
            $validParameters = $validParameters && $this->validateParameter($p);
            if (!$validParameters) {
            	break;
            }
        }
	    $this->hasLoaded = $validParameters;
	    return $validParameters;
	}
	
	public function getParameters() {
		if (!$this->hasLoaded && !$this->loadParameters(FileUtil::getDocumentRoot() . $this->pfile)) {
		    $this->logger->log("can't find parameters in $this->pfile");
		    return null;
		}
		return $this->parameters;
	}
	
	public function isProperty($name) {
		return in_array($name, $this->properties);
	}
}

?>