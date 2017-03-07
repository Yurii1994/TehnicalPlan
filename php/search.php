<?php 
$mysql_host = "localhost";
$mysql_user = "technicalplan";
$mysql_password = "11121994";
$mysql_database = "technicalplan";


if (isset($_GET["action"])) { 
    $action = $_GET['action'];
}

if (isset($_GET["name_one"])) { 
    $name_one = $_GET['name_one'];
}
if (isset($_GET["name_two"])) { 
    $name_two = $_GET['name_two'];
}
if (isset($_GET["name_three"])) { 
    $name_three = $_GET['name_three'];
}
if (isset($_GET["type_account"])) { 
    $type_account = $_GET['type_account'];
}

mysql_connect($mysql_host, $mysql_user, $mysql_password);
mysql_select_db($mysql_database);
mysql_set_charset('utf8');
header('Content-type: text/html; charset=utf-8');

if($action == search)
{
	if($name_one != null && $name_two != null && $name_three != null)
	{
		$users = searchThreeOption($name_one, $name_two, $name_three, $type_account);
	}
	else
	if($name_one != null && $name_two != null)
	{
		$users = searchTwoOption($name_one, $name_two, $type_account);
	}
	else
	if($name_one != null)
	{
		$users = searchOneOption($name_one, $type_account);
	}
	
	if($users != false)
	{
		while($e=mysql_fetch_assoc($users))
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
	else
	{
		print(json_encode(false));
	}
}

function searchThreeOption($query_name_one, $query_name_two, $query_name_three, $type_account) 
{ 
    $query_name_one = trim($query_name_one); 
    $query_name_one = mysql_real_escape_string($query_name_one);
    $query_name_one = htmlspecialchars($query_name_one);
	$query_name_two = trim($query_name_two); 
    $query_name_two = mysql_real_escape_string($query_name_two);
    $query_name_two = htmlspecialchars($query_name_two);
	$query_name_three= trim($query_name_three); 
    $query_name_three = mysql_real_escape_string($query_name_three);
    $query_name_three = htmlspecialchars($query_name_three);
    if (!empty($query_name_one) && !empty($query_name_two) && !empty($query_name_three)) 
    { 
        if (mb_strlen($query_name_one, 'utf8') < 3 && mb_strlen($query_name_two, 'utf8') < 3 && mb_strlen($query_name_three, 'utf8') < 3)
		{
            $result = false;
        }
		else
		if (mb_strlen($query_name_one, 'utf8') > 128 && mb_strlen($query_name_two, 'utf8') > 128 && mb_strlen($query_name_three, 'utf8') > 128)
		{
            $result = false;
        }
		else
		{ 
			if($type_account == 1)
			{
				$res = "SELECT `name`, `surname`, `surname_father`, `login`, `enterprise`, `images` FROM `users` WHERE 
				((`name` LIKE '%$query_name_one%' OR `name` LIKE '%$query_name_two%' OR `name` LIKE '%$query_name_three%')
				AND (`surname` LIKE '%$query_name_one%' OR `surname` LIKE '%$query_name_two%' OR `surname` LIKE '%$query_name_three%')
				AND (`surname_father` LIKE '%$query_name_one%' OR `surname_father` LIKE '%$query_name_two%' OR `surname_father` LIKE '%$query_name_three%'))
				AND (`type_account` = '$type_account' AND `enterprise` != 'null')";
				$result = mysql_query($res);   
			}
			else
			{
				$res = "SELECT `name`, `surname`, `surname_father`, `login`, `enterprise`, `images` FROM `users` WHERE 
				((`name` LIKE '%$query_name_one%' OR `name` LIKE '%$query_name_two%' OR `name` LIKE '%$query_name_three%')
				AND (`surname` LIKE '%$query_name_one%' OR `surname` LIKE '%$query_name_two%' OR `surname` LIKE '%$query_name_three%')
				AND (`surname_father` LIKE '%$query_name_one%' OR `surname_father` LIKE '%$query_name_two%' OR `surname_father` LIKE '%$query_name_three%'))
				AND (`type_account` = '$type_account' AND `enterprise` = 'null')";
				$result = mysql_query($res);   
			}
            
		} 
    }
	else
	{
        $result = false;
    }
    return $result; 
} 

function searchTwoOption($query_name_one, $query_name_two, $type_account) 
{ 
    $query_name_one = trim($query_name_one); 
    $query_name_one = mysql_real_escape_string($query_name_one);
    $query_name_one = htmlspecialchars($query_name_one);
	$query_name_two = trim($query_name_two); 
    $query_name_two = mysql_real_escape_string($query_name_two);
    $query_name_two = htmlspecialchars($query_name_two);
    if (!empty($query_name_one) && !empty($query_name_two)) 
    { 
        if (mb_strlen($query_name_one, 'utf8') < 3 && mb_strlen($query_name_two, 'utf8') < 3)
		{
            $result = false;
        }
		else
		if (mb_strlen($query_name_one, 'utf8') > 128 && mb_strlen($query_name_two, 'utf8') > 128)
		{
            $result = false;
        }
		else
		{ 
			if($type_account == 1)
			{
				$res = "SELECT `name`, `surname`, `surname_father`, `login`, `enterprise`, `images` FROM `users` WHERE
				(((`name` LIKE '%$query_name_one%' OR `name` LIKE '%$query_name_two%') AND
				(`surname` LIKE '%$query_name_one%' OR `surname` LIKE '%$query_name_two%'))
			
				OR((`name` LIKE '%$query_name_one%' OR `name` LIKE '%$query_name_two%')
				AND (`surname_father` LIKE '%$query_name_one%' OR `surname_father` LIKE '%$query_name_two%'))
			
				OR((`surname` LIKE '%$query_name_one%' OR `surname` LIKE '%$query_name_two%')
				AND (`surname_father` LIKE '%$query_name_one%' OR `surname_father` LIKE '%$query_name_two%')))
			
				AND (`type_account` = '$type_account' AND `enterprise` != 'null')";
				$result = mysql_query($res);  
			}
			else
			{
				$res = "SELECT `name`, `surname`, `surname_father`, `login`, `enterprise`, `images` FROM `users` WHERE
				(((`name` LIKE '%$query_name_one%' OR `name` LIKE '%$query_name_two%') AND
				(`surname` LIKE '%$query_name_one%' OR `surname` LIKE '%$query_name_two%'))
			
				OR((`name` LIKE '%$query_name_one%' OR `name` LIKE '%$query_name_two%')
				AND (`surname_father` LIKE '%$query_name_one%' OR `surname_father` LIKE '%$query_name_two%'))
			
				OR((`surname` LIKE '%$query_name_one%' OR `surname` LIKE '%$query_name_two%')
				AND (`surname_father` LIKE '%$query_name_one%' OR `surname_father` LIKE '%$query_name_two%')))
			
				AND (`type_account` = '$type_account' AND `enterprise` = 'null')";
				$result = mysql_query($res);   
			}
            
		} 
    }
	else
	{
        $result = false;
    }
    return $result; 
} 

function searchOneOption($query, $type_account) 
{ 
    $query = trim($query); 
    $query = mysql_real_escape_string($query);
    $query = htmlspecialchars($query);
    if (!empty($query)) 
    { 
        if (mb_strlen($query, 'utf8') < 3)
		{
            $result = false;
        }
		else
		if (mb_strlen($query, 'utf8') > 128)
		{
            $result = false;
        }
		else
		{ 
			if($type_account == 1)
			{
				$res = "SELECT `name`, `surname`, `surname_father`, `login`, `enterprise`, `images` FROM `users` WHERE (`name` LIKE '%$query%'
				OR `surname` LIKE '%$query%' OR `surname_father` LIKE '%$query%') AND (`type_account` = '$type_account' AND `enterprise` != 'null')";
				$result = mysql_query($res);  
			}
			else
			{
				$res = "SELECT `name`, `surname`, `surname_father`, `login`, `enterprise`, `images` FROM `users` WHERE (`name` LIKE '%$query%'
				OR `surname` LIKE '%$query%' OR `surname_father` LIKE '%$query%') AND (`type_account` = '$type_account' AND `enterprise` = 'null')";
				$result = mysql_query($res); 
			}
		} 
    }
	else
	{
        $result = false;
    }
    return $result; 
} 

mysql_close();
?>