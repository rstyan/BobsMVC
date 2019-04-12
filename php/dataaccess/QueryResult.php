<?php
interface QueryResult {
	public function populate($resultSet);
	public function getPropertyNames();
}
?>