<?php
/*
 * A simple session manager based on a login id and two levels
 * of privalges: normal usage and admin usage.  The session manager
 * allows users to login via a login screen, or via a cookie.
 */


class SessionManager {
	
	private $loginCookieName;
	private $cookies;
	
	private $userID=null;
	private $isAdmin=false;
	private static $instance=null;
	private $isPersistent=false;
	
	// Keep protected; this is a singleton class
	protected function __construct($loginCookieName) {
		$this->cookies = array();
		$this->registerCookie($loginCookieName);
		$this->loginCookieName = $loginCookieName;
    	if (isset($_SESSION['userID'])) {
    		$this->userID = $_SESSION['userID'];
    	}
    	if (isset($_SESSION['isAdmin'])) {
    		$this->isAdmin = $_SESSION['isAdmin'];
    	}
    	if (!$this->inSession() && $this->hasCookie($this->loginCookieName)) {
    		$this->isAdmin = $this->getAdminFromCookie();
    		$this->userID = $this->getUserFromCookie();
    		$this->isPersistent = true;
    	}
	}
	
	protected static function getInstance($className='SessionManager') {
		if (is_null(self::$instance)) {
			self::$instance = new $className();
		}
		return self::$instance;
	}
	
	public function inSession() {
    	return !is_null($this->userID);
	}
	
	public function getUser() {
		return $this->userID;
	}
	
	public function isAdmin() {
		return $this->isAdmin==1;
	}
	
	public function update($user, $isAdmin) {
    	$this->userID = $user;
    	$this->isAdmin = $isAdmin;
    	$_SESSION['userID'] = $user;
    	$_SESSION['isAdmin'] = $isAdmin?1:0;
	}
	
    protected function reset() {
    	unset($_SESSION['userID']);
    	unset($_SESSION['isAdmin']);
    	$this->userID = null;
    	$this->isAdmin = false;
    	session_destroy();
    }
    
    /*
     * Keeping track of cookies:
     * The session manager will store one cookie - the user name / admin property.
     * However, applications may want to store additional cookie information.  So
     * here's some crap to let you do that.
     * 
     * We have a cookie array (not meant to replace $_COOKIE) that keeps track of the
     * application's cookie set. It records existance only, so when a logout happens
     * the cookie values can be systematically distroyed.  All other information will
     * be kept the standard php way, through $_COOKIE.
     */
    public function hasCookie($name) {
    	return isset($_COOKIE[$name]);
    }
    
    public function registerCookie($name) {
    	$this->cookies[$name] = "";
    }
    
    public function setCookieValue($name, $value) {
    	return setcookie($name, $value, time()+60*60*24*500, "/");
    }
    
    public function getCookieValue($name) {
    	return $_COOKIE[$name];
    }
	
    private function resetCookies() {
    	foreach ($this->cookies as $cookie=>$value) {
    		setcookie($cookie, 'foo', time()-3600, "/");
    	}
    }
    
    // Based on the login: if login was from a cookie - then persistence is assumed
    // unless otherwise told.
    public function isPersistent() {
    	return $this->isPersistent;
    }
    /*
     * Login cookie:
     * keeps track of UserID and admin status.
     */
    
    private function getUserFromCookie() {
    	$val = intval($this->getCookieValue($this->loginCookieName));
    	return ($val < 1)?-$val:$val;
    }
    
    private function getAdminFromCookie() {
    	$val = intval($this->getCookieValue($this->loginCookieName));
       	return $val > 0;
    }
    
    public function saveSession() {
    	$this->isPersistent = true;
    	$value = strval($this->isAdmin?$this->userID:-$this->userID);
    	return $this->setCookieValue($this->loginCookieName, $value);
    }
    
    public function removeSession() {
    	$this->isPersistent = false;
    	$this->resetCookies();
    	$this->reset();
    }
    
}
?>