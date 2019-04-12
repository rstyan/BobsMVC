<?php
class RelationshipDescriptor {

	const one2one = 1;
	const one2many = 2;

	public $type;
	public $autoUpdate;
	
	// The name of the foreign class
	public $className;
	
	// Tell us how my key maps into the foreigner's key
	// array($myKey=>$foreignKey, ...)
	public $keyMap;
	
	public function __construct($className, $keyMap, $type, $autoUpdate) {
		$this->className = $className;
		$this->keyMap = $keyMap;
		$this->type = $type;
		$this->autoUpdate = $autoUpdate;
	}
}

?>