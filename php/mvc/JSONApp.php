<?php
require_once '../dataaccess/FileUtil.php';

class JSONApp {
	
	private $rootDir;
	
	// Set up the include path for the application.
	public function __construct($rootExtension, $lib, $webDir="") {
		FileUtil::setRootExtension($rootExtension, $webDir);
		$this->rootDir = FileUtil::getDocumentRoot();
		FileUtil::add_include_path($this->rootDir);
		FileUtil::add_include_path("phar://".$this->rootDir.$lib);		
	}
	
	public function run($action, $actionDir="src/ajax") {
		$actionFile = "{$actionDir}/{$action}.php";
		if (!file_exists($this->rootDir.$actionFile)) {
			throw new Exception("action file not found");
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