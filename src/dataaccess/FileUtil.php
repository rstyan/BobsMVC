<?php
class FileUtil {
	
	const fileSeparator = "/";
	
	private static $rootExtension = "/";
	
	public static function getDocumentRoot() {
		// we want the directory containing WWW
		$root = pathinfo($_SERVER["DOCUMENT_ROOT"], PATHINFO_DIRNAME) . self::$rootExtension;
		return str_replace("\\",self::fileSeparator,$root);
	}
	
	public static function setRootExtension($extension) {
		self::$rootExtension = empty($extension)?"/":"/$extension/";
	}
	
	public static function getRootExtension() {
		return self::$rootExtension;
	}
	
	public static function add_include_path($path) {
	    set_include_path(get_include_path() . PATH_SEPARATOR . $path);				
	}
	
	// Relative path is for URL references in a web page;
	// So make everything relative to the document root.
	public static function getRelativePath($fullPath) {
		$rootPath = str_replace("\\", self::fileSeparator, $_SERVER["DOCUMENT_ROOT"]).self::fileSeparator;
		$len = strlen($rootPath);
		$cmp = substr($fullPath,0,$len);
		if (strcmp($rootPath,$cmp) != 0) {
			return null;
		}
		return substr($fullPath,$len-1);
	}

	// For brain dead IIS servers who refuse to give us the document
	// root, we must work against the script root.
	// Necessary for < PHP 5.3.6
	public static function getDocumentRoot_thePrequel() {
		if(isset($_SERVER['DOCUMENT_ROOT'])) {
			return $_SERVER['DOCUMENT_ROOT'];
		}
		$relpath = getenv("SCRIPT_NAME");
		$fullpath = null;
		$fullpath = realpath(basename($relpath));
		$fullpath = str_replace("\\",self::fileSeparator,$fullpath);
		$docroot = substr($fullpath,0,strpos($fullpath,$relpath));
	    $path = explode(self::fileSeparator, $docroot);
	    // the document root is one level above the script root.
	    array_pop($path);
	    $docroot = implode(self::fileSeparator, $path);
		return $docroot;
	}
	
}
?>