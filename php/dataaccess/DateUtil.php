<?php
class DateUtil {
	public function toString($timestamp) {
	  	return date("M d Y, g:i:s a T", $timestamp);
	}
}
?>