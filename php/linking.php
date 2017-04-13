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
if (isset($_GET["where_user"])) { 
    $where_user = $_GET['where_user'];
}
if (isset($_GET["from_user"])) { 
    $from_user = $_GET['from_user'];
}
if (isset($_GET["enterprise"])) { 
    $enterprise = $_GET['enterprise'];
}
if (isset($_GET["position"])) { 
    $position = $_GET['position'];
}
if (isset($_GET["code"])) { 
    $code = $_GET['code'];
}
if (isset($_GET["name_table"])) { 
    $name_table = $_GET['name_table'];
}
if (isset($_GET["state"])) { 
    $state = $_GET['state'];
}
if (isset($_GET["state"])) { 
    $state = $_GET['state'];
}


mysql_connect($mysql_host, $mysql_user, $mysql_password);
mysql_select_db($mysql_database);
mysql_set_charset('utf8');
header('Content-type: text/html; charset=utf-8');

if($action == insert && $where_user != null && $from_user != null && $enterprise != null && $position != null && $name_table != null)
{
	$q=mysql_query("SELECT * FROM `linking` WHERE where_user='$from_user' AND from_user='$where_user'");	
	while($e=mysql_fetch_assoc($q))
	$list_whereAndFrom[]=$e;

	$q=mysql_query("SELECT * FROM `linking` WHERE where_user='$where_user' AND from_user='$from_user'");	
	while($e=mysql_fetch_assoc($q))
	$list_FromAndWhere[]=$e;

	if(count($list_whereAndFrom) == 0 && count($list_FromAndWhere) == 0)
	{
		$q=mysql_query("SELECT * FROM `linking` WHERE from_user='$from_user' AND where_user='$where_user'");	
		while($e=mysql_fetch_assoc($q))
		$list_from[]=$e;
		
		$q=mysql_query("SELECT * FROM `linking` WHERE from_user='$where_user' AND where_user='$from_user'");	
		while($e=mysql_fetch_assoc($q))
		$list_where[]=$e;
		
		if(count($list_from) > 0 || count($list_where) > 0)
		{
			mysql_query("UPDATE `linking` SET where_user='$where_user', enterprise='".mysql_real_escape_string($enterprise)."'
			, position='".mysql_real_escape_string($position)."', code='$code', name_table='$name_table' WHERE
			from_user='$from_user'");
			print(json_encode(true));
		}
		else	
		{
			mysql_query("INSERT INTO `linking`(`where_user`,`from_user`,`enterprise`,`position`,`code`,`name_table`)
			VALUES ('$where_user','$from_user', '".mysql_real_escape_string($enterprise)."', '".mysql_real_escape_string($position)."', '$code', '$name_table')");
			print(json_encode(true));
		}
	}
	else
	{
		print(json_encode(false));
	}
}

if($action == linked && $where_user != null && $from_user != null && $state != null && $name_table != null)
{
	$q=mysql_query("SELECT * FROM `linking` WHERE where_user='$where_user' AND from_user='$from_user'");	
	while($e=mysql_fetch_assoc($q))
	$list_whereAndFrom[]=$e;
	if(count($list_whereAndFrom) > 0)
	{
		mysql_query("UPDATE `linking` SET state='$state' WHERE where_user='$where_user' AND from_user='$from_user'");
		
		if($state == 'true')
		{
			$res_where = mysql_query("SELECT * FROM `users` WHERE login='$where_user'");
			for ($c = 0; $c < mysql_num_rows($res_where); $c++)
			{	
				$f = mysql_fetch_array($res_where);
				$type_account_where = $f[type_account];
			}

			$res_from = mysql_query("SELECT * FROM `users` WHERE login='$from_user'");
			for ($c = 0; $c < mysql_num_rows($res_from); $c++)
			{
				$f = mysql_fetch_array($res_from);
				$type_account_from = $f[type_account];
			}

			$q=mysql_query("SELECT * FROM `linking` WHERE where_user='$where_user' AND from_user='$from_user'");
			for ($c = 0; $c < mysql_num_rows($q); $c++)
			{
				$f = mysql_fetch_array($q);
				$enterprise = $f[enterprise];
				$position = $f[position];
			}		
		
			if($type_account_where == 2)
			{
				mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."',
				position='".mysql_real_escape_string($position)."', name_table='$name_table' WHERE login='$where_user'");
			}
			if($type_account_from == 2)
			{
				mysql_query("UPDATE `users` SET enterprise='".mysql_real_escape_string($enterprise)."',
				position='".mysql_real_escape_string($position)."' , name_table='$name_table' WHERE login='$from_user'");
			}
		}
		else
		if($state == 'false')
		{
			$res_where = mysql_query("SELECT * FROM `users` WHERE login='$where_user'");
			for ($c = 0; $c < mysql_num_rows($res_where); $c++)
			{
				$f = mysql_fetch_array($res_where);
				$type_account_where = $f[type_account];
			}

			$res_from = mysql_query("SELECT * FROM `users` WHERE login='$from_user'");
			for ($c = 0; $c < mysql_num_rows($res_from); $c++)
			{
				$f = mysql_fetch_array($res_from);
				$type_account_from = $f[type_account];
			}
			
			if($type_account_where == 2)
			{
				mysql_query("UPDATE `users` SET enterprise='false',position='false', name_table='false' WHERE login='$where_user'");
			}
			if($type_account_from == 2)
			{
				mysql_query("UPDATE `users` SET enterprise='false',position='false', name_table='false' WHERE login='$from_user'");
			}
		}
		print(json_encode(true));
	}
	else
	{
		print(json_encode(false));
	}
}

if($action == remove)
{
	if($where_user == null && $from_user == null)
	{
		mysql_query("TRUNCATE TABLE `linking`");
		print(json_encode(true));
	}
	else
	{
		if($where_user != null && $from_user != null)
		{
			mysql_query("DELETE FROM `linking` WHERE (where_user='$where_user' AND from_user='$from_user')
			OR (where_user='$from_user' AND from_user='$where_user')");
			
			$res_where = mysql_query("SELECT * FROM `users` WHERE login='$where_user'");
			for ($c = 0; $c < mysql_num_rows($res_where); $c++)
			{
				$f = mysql_fetch_array($res_where);
				$type_account_where = $f[type_account];
			}

			$res_from = mysql_query("SELECT * FROM `users` WHERE login='$from_user'");
			for ($c = 0; $c < mysql_num_rows($res_from); $c++)
			{
				$f = mysql_fetch_array($res_from);
				$type_account_from = $f[type_account];
			}
			
			if($type_account_where == 2)
			{
				mysql_query("UPDATE `users` SET enterprise='false',position='false', name_table='false' WHERE login='$where_user'");
			}
			if($type_account_from == 2)
			{
				mysql_query("UPDATE `users` SET enterprise='false',position='false', name_table='false' WHERE login='$from_user'");
			}
			
			print(json_encode(true));
		}
		else
		{
			print(json_encode(false));
		}
	}
}

if($action == select)
{
	if($where_user != null)
	{
		$q=mysql_query("SELECT * FROM `linking` WHERE where_user='$where_user'");
	}
	else
	if($from_user != null)
	{
		$q=mysql_query("SELECT * FROM `linking` WHERE from_user='$from_user'");	
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