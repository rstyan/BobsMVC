<?php
class FieldDescriptor {
	// Attribute types
	const bool = "boolean";
	const string = "string";
	const int = "integer";
	const decimal = "double";
	
	public $name;
	public $type;
	public $isNullable;
	public $length;
	public $default;
	public $isKey;
	public $isAutogen;
	
	public function __construct($name, $type, $len, $nullable, $default, $key, $auto) {
		$this->name = $name;
		$this->type = $type;
		$this->isNullable = $nullable;
		$this->length = $len;
		$this->default = $default;
		$this->isKey = $key;
		$this->isAutogen = $auto;
	}
}
?>