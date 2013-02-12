<?php
require_once '../dataaccess/FileUtil.php';
require_once "XMLBuilder.php";

class XmlApp {
	
	private $rootDir;
	
	// Set up the include path for the application.
	public function __construct($rootExtension, $lib, $webDir="") {
		FileUtil::setRootExtension($rootExtension, $webDir);
		$this->rootDir = FileUtil::getDocumentRoot();
		FileUtil::add_include_path($this->rootDir);
		FileUtil::add_include_path("phar://".$this->rootDir.$lib);		
	}
	
	public function run($action, $actionDir="src/ajax") {
		$actionFile = $actionDir."/$action.php";
		header('Content-Type: application/xml; charset=ISO-8859-1');
		if (!file_exists($this->rootDir.$actionFile)) {
			echo "<error>Request denied</error>";
			return;
		}
		require_once $actionFile;
		// Want session to start after the file includes - this will allow session variables to
		// store objects (i.e. object declarations must be encountered before a session is started).
		session_start();
		$builder = new $action();
		echo $builder->performRequest($_POST, $_GET);
	}
}
?>