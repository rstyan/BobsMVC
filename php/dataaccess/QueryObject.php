<?php
interface QueryObject {
	public function getQueryResource();
	public function getResultType();
	public function morphQueryResults(&$result);
}
?>