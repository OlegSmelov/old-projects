<?php

/* Just an example on how to use this rest webservice */

$token = '?token=EicxAdMkZXnY2niT8M23';
$url = 'http://localhost:3333/';

function get_curl($table, $id) {
    $ch = curl_init();
    
    global $url;
    $new_url = $url . $table . '/';
    
    if (!empty($id))
        $new_url .= $id;
    
    global $token;
    $new_url .= $token;
    
    curl_setopt($ch, CURLOPT_URL, $new_url);
    curl_setopt($ch, CURLOPT_HEADER, 0);
    
    return $ch;
}

function execute_curl($ch) {
    ob_start();
    
    $result = null;
    if (curl_exec($ch))
        $result = ob_get_contents();
    
    ob_end_clean();
    curl_close($ch);
    
    return $result;
}

function rest_get($table, $id = null) {
    $ch = get_curl($table, $id);
    return execute_curl($ch);
}

function rest_post($table, $values) {
    $ch = get_curl($table, null);
    curl_setopt($ch, CURLOPT_POST, 1);
    curl_setopt($ch, CURLOPT_POSTFIELDS, implode('|', $values));
    return execute_curl($ch);
}

function rest_delete($table, $id) {
    $ch = get_curl($table, $id);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
    return execute_curl($ch);
}

echo "Getting students:\n";
echo rest_get('students');

echo "Adding a new student:\n";
$response = rest_post('students', Array('Vardenis', 'Pavardenis', '857471588'));
preg_match('/Row id: ([0-9]+)/', $response, $matches);
$id = $matches[1];
echo $response;

echo "Getting students:\n";
echo rest_get('students');

echo "Deleting student:\n";
echo rest_delete('students', $id);

?>
