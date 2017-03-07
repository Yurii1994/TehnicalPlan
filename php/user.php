<?php 
$mysql_host = "localhost";
$mysql_user = "technicalplan";
$mysql_password = "11121994";
$mysql_database = "technicalplan";

if (!mysql_connect($mysql_host, $mysql_user, $mysql_password)){
	
echo "<h2>База недоступна!</h2>";
exit;
}

if (isset($_GET["action"])) { 
    $action = $_GET['action'];
}

if (isset($_GET["name"])) { 
    $name = $_GET['name'];
}
if (isset($_GET["surname"])) { 
    $surname = $_GET['surname'];
}
if (isset($_GET["surname_father"])) { 
    $surname_father = $_GET['surname_father'];
}
if (isset($_GET["enterprise"])) { 
    $enterprise = $_GET['enterprise'];
}
if (isset($_GET["position"])) { 
    $position = $_GET['position'];
}
if (isset($_GET["login"])) { 
    $login = $_GET['login'];
}
if (isset($_GET["new_login"])) { 
    $new_login = $_GET['new_login'];
}
if (isset($_GET["password"])) { 
    $password = $_GET['password'];
}
if (isset($_GET["email"])) { 
    $email = $_GET['email'];
}
if (isset($_GET["type_account"])) { 
    $type_account = $_GET['type_account'];
}
if (isset($_GET["refresh"])) { 
    $refresh = $_GET['refresh'];
}

mysql_connect($mysql_host, $mysql_user, $mysql_password);
mysql_select_db($mysql_database);
mysql_set_charset('utf8');
header('Content-type: text/html; charset=utf-8');

if($action == insert && $name != null && $surname != null
 && $surname_father != null && $login != null && $password != null && $email != null && $type_account != null)
{
	$q=mysql_query("SELECT * FROM users WHERE email='$email'");	
	while($e=mysql_fetch_assoc($q))
    $list_email[]=$e;
	
	$q=mysql_query("SELECT * FROM users WHERE login='$login'");	
	while($e=mysql_fetch_assoc($q))
	$list_login[]=$e;
	
	if(count($list_email) == 0 || $refresh == true)
	{
		if(count($list_login) > 0)
		{
			if($new_login == null)
			{
				$new_login = $login;
			}
			if($refresh == true)
			{
				$res=mysql_query("SELECT * FROM users WHERE login='$login'");	
				for ($c = 0; $c < mysql_num_rows($res); $c++)
				{
					$f = mysql_fetch_array($res);
					$type_account_old = $f[type_account];
				}	
				
				mysql_query("UPDATE `users` SET name='$name', surname='$surname', surname_father='$surname_father'
				,enterprise='$enterprise',position='$position',login='$new_login',password='$password',email='$email',type_account='$type_account'
				WHERE login='$login'");
				print(json_encode(true));
			
				//Якщо тип акаунта не мінявся
				if($type_account_old == $type_account)
				{
					if($type_account == 1)
					{
						//обновит логин в спеціальності
						mysql_query("UPDATE `position` SET login='$new_login' WHERE login='$login'");
						//обновит підпремство в привязкі і логін менеджера
						mysql_query("UPDATE `linking` SET enterprise='$enterprise', where_user='$new_login' WHERE where_user='$login'");
						mysql_query("UPDATE `linking` SET enterprise='$enterprise', from_user='$new_login' WHERE from_user='$login'");
					}
					else
					{	
						//обновит логін виконавця в привязкі
						mysql_query("UPDATE `linking` SET from_user='$new_login' WHERE from_user='$login'");
						mysql_query("UPDATE `linking` SET where_user='$new_login' WHERE where_user='$login'");
					}
				}
				else
				{
					//видалить дані привязані до менеджера
					if($type_account == 2)
					{
						//таблиця linking
						mysql_query("DELETE FROM `linking` WHERE where_user='$login'");
						mysql_query("DELETE FROM `linking` WHERE from_user='$login'");
						//таблиця position
						mysql_query("DELETE FROM `position` WHERE login='$login'");
					}
				}
			}
			else
			{
				print(json_encode(false_login));
			}
		}
		else
		{
			mysql_query("INSERT INTO `users`(`name`,`surname`,`surname_father`,`enterprise`,
			`position`,`login`,`password`,`email`,`type_account`) VALUES
			('$name','$surname','$surname_father','$enterprise','$position','$login','$password','$email','$type_account')");
			mail($email, "Регістрація виконана.", "Вітаємо $name, Ви зареєструвалися в системі. Дякуємо, що Ви обрали нас. \nДля входу викристовуйте: \nЛогін: $login \nПароль: $password");
			print(json_encode(true));
		}
	}
	else
	if(count($list_email) > 0 & count($list_login) > 0)
	{
		print(json_encode(false_emailANDfalse_login));
	}
	else
	{
		print(json_encode(false_email));
	}
}

if($action == refresh && $login != null && $enterprise != null)
{
	mysql_query("UPDATE `users` SET enterprise='$enterprise' WHERE login='$login'") or die(print(json_encode(false)));
	print(json_encode(true));
}

if($action == remove)
{
	if($login == null)
	{
		/*mysql_query("TRUNCATE TABLE `users`");
		print(json_encode(true));*/
	}
	else
	{
		//удаление пользователя
		mysql_query("DELETE FROM `users` WHERE login='$login'");
		
		//удаление изображения пользователя
		$res = mysql_query("SELECT * FROM `users` WHERE login='$login'");
		while($e=mysql_fetch_assoc($res))
		$output[]=$e;
		if(count($output) > 0)
		{
			$res = mysql_query("SELECT * FROM `users` WHERE login='$login'");
			for ($c = 0; $c < mysql_num_rows($res); $c++)
			{
				$f = mysql_fetch_array($res);
				$name = $f[images];
				unlink("images_account/$name");
			}	
			$new_name = '';
			mysql_query("UPDATE `users` SET images='$new_name' WHERE login='$login'");
		}
		
		print(json_encode(true));
	}
}

if($action == select)
{
	if($login == null)
	{
		if($email != null)
		{
			$q=mysql_query("SELECT * FROM users WHERE email='$email'");	
		}
		else
		{
			$q=mysql_query("SELECT * FROM users");
		}
	}
	else
	{
		$q=mysql_query("SELECT * FROM users WHERE login='$login'");	
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

if($action == message)
{
	if($email != null)
	{
		$res = mysql_query("SELECT * FROM users WHERE email='$email'");	
		while($e=mysql_fetch_assoc($res))
			$output[]=$e;
		if(count($output) > 0)
		{	
			$res = mysql_query("SELECT * FROM users WHERE email='$email'");	
			$row = mysql_fetch_array($res);
			$login = $row['login'];
			$password = $row['password'];
			mail($email, "Відновлення входу.", "Ваш:\nЛогін: $login\nПароль: $password");
			print(json_encode(true));
		}
		else
		{
			print(json_encode(false));
		}
	}
}

if($action == code && $email != null)
{
	$code = generate_password(6);
	mail($email, "Код підтвердження.", "Для підтвердження введіть код: $code");
	print(json_encode($code));
}

function generate_password($number)
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

mysql_close();
?>