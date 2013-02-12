<?php
require_once '../dataaccess/FileUtil.php';

class Webapp {
	
	// Set up the include path for the application.
	public function __construct($rootExtension, $lib, $webDir="") {
		FileUtil::setRootExtension($rootExtension, $webDir);
		$docRoot = FileUtil::getDocumentRoot();
		FileUtil::add_include_path($docRoot);
		FileUtil::add_include_path("phar://".$docRoot.$lib);		
	}
	
	/*
	 * Run: Run the specified Action.
	 * 
	 * action: name of the action to be performed.
	 * ActionDir: directory containing action classes
	 * ErrorAction: in case someone is hacking, provide an error action to log them out and display an error page.
	 * PageTemplate: specify if the MVC is a "page template" model, with includes embedded in the containing template html page.
	 * DefaultPageTitle: specify if the MVC is a "page template" model
	 * DefaultForm: Specify if you need to subclass BaseForm for a general form class, otherwise use BaseForm and specific <action>Form classes.
	 * ActionSuffix: specify if you're bored with <action>Action names for action classes.
	 * FormSuffix: specify if you're bored with <action>Form names for form classes.
	 * 
	 */
	public function run($actionName, $actionDir, $defaultForm="", $errorAction="Error", $pageTemplate="", $defaultPageTitle="", $actionSuffix = "Action", $formSuffix = "Form") {
		$form = null;
		// Find the action code to exectue
		$root = FileUtil::getDocumentRoot();
		$actionFile = null;
		if (file_exists($root . $actionDir . $actionName . ".php")) {
			$actionFile = $actionDir . $actionName . ".php";
			$actionSuffix = "";
		}
		else if (file_exists($root . $actionDir . $actionName . $actionSuffix . ".php")) {
			$actionFile = $actionDir . $actionName . $actionSuffix . ".php";
		}
		else {
			// Go to the "something is badly wrong or someone is hacking us" error page.
			// this is not a debugging feature - it assumes the "errorAction" is valid.
			$actionName = $errorAction;
			$actionFile = $actionDir . $actionName . $actionSuffix . ".php";
		}
		
		// Get the action to execute.
		require_once $actionFile;
		$actionClass = basename($actionName . $actionSuffix);
		// Find the action result data structure
		// First, check if the form is specific to the action.
		$formFile = $actionDir . $actionName . $formSuffix . ".php";
		$formName = null;
		if (file_exists($root . $formFile)) {
			require_once $formFile;
			$formName = $actionName . $formSuffix;
		}
		// Then check to see if an app-wide default form is used.
		else if (file_exists($root . $actionDir . $defaultForm . ".php")) {
			require_once $actionDir . $defaultForm . ".php";
			$formName = $defaultForm;
		}
		else {
			require_once 'mvc/BaseForm.php';
			$formName = "BaseForm";
		}

		// Want session to start after the file includes - this will allow session variables to
		// store objects (i.e. object declarations must be encountered before a session is started), 
		// BUT before any data is constructed, so session data will be present.
		if (!isset($_SESSION)) {
			session_start();
		}
		
		// Construct the form:
		$action = new $actionClass();
		$form = new $formName($pageTemplate, $defaultPageTitle);
		
		// Execute the action
		if ($action->isValidData($form, $_POST, $_GET)) {
			$form = $action->doAction($form, $_POST, $_GET, $_SESSION, $_SERVER);
		}
		else {
			$form = $action->doAlternateAction($form, $_POST, $_GET, $_SESSION, $_SERVER);
		}
		
		// NOTE: the variable $actionForm is used for communction between 
		// the action and the template page.  Keep its name consistent!
		$actionForm = $form;
		include ($form->page);
	}
}

?>