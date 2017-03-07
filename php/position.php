<?php 
$mysql_host = "localhost";
$mysql_user = "technicalplan";
$mysql_password = "11121994";
$mysql_database = "technicalplan";

if (!mysql_connect($mysql_host, $mysql_user, $mysql_password)){
	
echo "<h2>Ѕаза недоступна!</h2>";
exit;
}

if (isset($_GET["action"])) { 
    $action = $_GET['action'];
}
if (isset($_GET["login"])) { 
    $login = $_GET['login'];
}
if (isset($_GET["new_login"])) { 
    $new_login = $_GET['new_login'];
}
if (isset($_GET["position"])) { 
    $position = $_GET['position'];
}
if (isset($_GET["enterprise"])) { 
    $enterprise = $_GET['enterprise'];
}
if (isset($_GET["code"])) { 
    $code = $_GET['code'];
}
if (isset($_GET["refresh"])) { 
    $refresh = $_GET['refresh'];
}

mysql_connect($mysql_host, $mysql_user, $mysql_password);
mysql_select_db($mysql_database);
mysql_set_charset('utf8');
header('Content-type: text/html; charset=utf-8');

if($action == insert && $position != null && $login != null && $enterprise != null)
{
	if($refresh == true && $code != null)
	{
		mysql_query("UPDATE `position` SET position='$position' WHERE code='$code' AND login='$login'");
		
		//обновить спеціальність привязкі по логіну і коду
		mysql_query("UPDATE `linking` position='$position' WHERE code='$code' AND where_user='$login'");
		mysql_query("UPDATE `users` SET enterprise='$enterprise' WHERE login='$login'");
		
		$uploads_dir = './images_account/';
		$ext = array_pop(explode('.',$_FILES['scv']['name']));
		$new_name = time().'.'.$ext;
		move_uploaded_file($_FILES['scv']['tmp_name'], $uploads_dir.$new_name);
		
		print(json_encode(true));
	}
	else	
	{
		$code = generate_code(10);
		
		$uploads_dir = './images_account/';
		$ext = array_pop(explode('.',$_FILES['scv']['name']));
		$new_name = time().'.'.$ext;
		move_uploaded_file($_FILES['scv']['tmp_name'], $uploads_dir.$new_name);

		mysql_query("INSERT INTO `position`(`login`,`position`,`code`) VALUES ('$login','$position', '$code')") or die(print(json_encode(false)));
		mysql_query("UPDATE `users` SET enterprise='$enterprise' WHERE login='$login'");
		print(json_encode(true));
	}
}

function generate_code($number)
{
    $arr = array('a','b','c','d','e','f',
                 'g','h','i','j','k','l',
                 'm','n','o','p','r','s',
                 't','u','v','x','y','z',
                 'A','B','C','D','E','F',
                 'G','H','I','J','K','L',
                 'M','N','O','P','R','S',
                 'T','U','V','X','Y','Z',
                 '1','2','3','4','5','6',
                 '7','8','9','0');
    $pass = "";
    for($i = 0; $i < $number; $i++)
    {    
      $index = rand(0, count($arr) - 1);
      $pass .= $arr[$index];
    }
    return $pass;
}

if($action == remove)
{
	if($login == null)
	{
		mysql_query("TRUNCATE TABLE `position`");
	}
	else
	{
		if($code == null)
		{
			mysql_query("DELETE FROM `position` WHERE login='$login'") or die(print(json_encode(false)));
			print(json_encode(true));
		}
		else
		{
			mysql_query("DELETE FROM `position` WHERE login='$login' AND code='$code'") or die(print(json_encode(false)));
			print(json_encode(true));
		}
	}
}

if($action == select)
{
	if($login == null)
	{
		$q=mysql_query("SELECT * FROM `position`");
	}
	else
	{
		$q=mysql_query("SELECT * FROM `position` WHERE login='$login'");	
	}

	while($e=mysql_fetch_assoc($q))
    $output[]=$e;
	if(count($output) > 0)
	{
		print(json_encode(true));
		print(json_encode($output, JSON_UNESCAPED_UNICODE));
	}
	else
	{
		print(json_encode(false));
	}
}

mysql_close();
?>