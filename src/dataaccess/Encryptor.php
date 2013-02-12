<?php
class Encryptor {
	const minKeyLen = 8;
	
	private static function convert($str, $key) {
		$key=str_replace(chr(32), '', $key);
		if(strlen($key) < self::minKeyLen) {
			return $str;
		}
		$keyLen = strlen($key) < 32?strlen($key):32;
		$k = array();
		for($i=0; $i < $keyLen; $i++) {
			$k[$i] = ord($key{$i})&0x1F;
		}
		$j=0;
		for($i=0; $i < strlen($str); $i++) {
			$e = ord($str{$i});
			$str{$i} = $e & 0xE0 ? chr($e^$k[$j]):chr($e);
			$j++;
			$j = $j==$keyLen?0:$j;
		}
		return $str;
	}
	
	public static function encrypt($str, $key='') {
		return self::convert($str, $key);
	}
	
	public static function decrypt($str, $key='') {
		return self::convert($str, $key);
	}
}
?>