<?php
require_once 'SelectCriteria.php';

class HavingCriteria extends SelectCriteria {
	
	public function __construct($key, $expr, $valueType=FieldDescriptor::string, $operator="=") {
		parent::__construct($key, $expr, $valueType, $operator, QueryCriteria::postSelector);
	}
}

?>