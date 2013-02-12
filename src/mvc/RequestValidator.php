<?php
// These types of errors should only occure if some one is screwing
// around (ie hacking), or there is something badly wrong.  Just use
// a generic error message so we don't give up too much
// information in the former case.  Perhaps, in fact, the message should just say "Fuck off".
class RequestValidator {
	
	protected $errMsg;
	
	public function __construct() {
		$this->errMsg = "Invalid request. Please contact technical support if this problem persists.";
	}
	
	protected function getStringParameter($name, $parameters) {
		if (!array_key_exists($name, $parameters)) {
			return null;
		}
		
		$param = trim($parameters[$name]);
		if (empty($param)) {
			return null;
		}
		
		return $param;
	}
	
	protected function getNumericParameter($name, $parameters) {
		$parm = $this->getStringParameter($name, $parameters);
		if (is_null($parm)) return null;
		if (!is_numeric($parm)) return null;
		return $parm;
	}
	
	public function validStringParameter($name, $parameters, $candidates=null) {
		$parm = $this->getStringParameter($name, $parameters);
		if (is_null($parm)) {
			return $this->errMsg;
		}
		if (is_null($candidates)) {
			return null;
		}
		foreach ($candidates as $candidate) {
			if (strcasecmp($candidate, $parm)==0) {
				return null;
			}			
		}
		return $this->errMsg;
	}

	public function validNumericParameter($name, $parameters) {
		$parm = $this->getNumericParameter($name, $parameters);
		if (is_null($parm)) {
			return $this->errMsg;
		}
		return null;
	}
}
?>