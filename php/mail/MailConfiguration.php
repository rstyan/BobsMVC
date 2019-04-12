<?php
require_once 'dataaccess/FileUtil.php';
require_once 'logging/SimpleLogger.php';

class MailConfiguration {
	
	const nl="\r\n";
	
	const confFile = "/conf/mail.properties";
	private $mailParameters;
	private $paramsLoaded;
	private $logger;
	
	public function __construct() {
		$this->logger = logger::getInstance();
		$this->paramsLoaded=false;
		$this->mailParameters=null;
	}

	private function validateParameter($k) {
		if (is_null($this->mailParameters[$k])) {
			return false;
		}
		return true;
	}
	
	// returns true if parameters have been loaded
	private function loadMailParameters($fileName) {
		if (!file_exists($fileName)) {
			return false;
		}
        $lines = file($fileName);  
        foreach ($lines as $line) {
          list($k, $v) = explode('=', $line);
          $this->mailParameters[trim($k)] = trim($v);
        }
        $validParameters = 
            $this->validateParameter('mail.host') && $this->validateParameter('mail.to') && 
        	$this->validateParameter('mail.user') && $this->validateParameter('mail.user.passwd');
        if ($validParameters) {
	    	$this->paramsLoaded = true;
	    }
	    return $validParameters;
	}
	
	public function __get($name) {
		if (strcmp($name, "mailParameters")==0) {
			if (!$this->parametersLoaded && !$this->loadMailParameters(FileUtil::getDocumentRoot() . SendMail::confFile)) {
				return null;
			}
			return $this->mailParameters;
		}
		return $this->$name;
	}
}
?>