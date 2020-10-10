# hedisql-opt

# How to Recover a Stored Password from HeidiSQL

## 1、 Open HeidiSQL and select File > Export Settings to dump settings into a text file.
## 2、 Open the text file and search on the host name of the database you want to recover the password for
## 3、 A couple lines below the host name is the encoded password. It’ll look something like: 755A5A585C3D8141. Keep this handy.
## 4、 Copy the following code into a new document

```html
<!doctype html>
<html>
<body>
<script>
function heidiDecode(hex) {
    var str = '';
    var shift = parseInt(hex.substr(-1));
    hex = hex.substr(0, hex.length - 1);
    for (var i = 0; i < hex.length; i += 2) str += String.fromCharCode(parseInt(hex.substr(i, 2), 16) - shift); return str; } document.write(heidiDecode('[ENCODED_PASSWORD]')); </script>
</body>
</html>
```
## 5、 Copy and paste the encoded password from the HeidiSQL settings file into the heidiDecode function as the value to be passed as the hex argument, i.e. replace [ENCODED_PASSWORD] with your actual encoded password.
## 6、 Save as a HTML document and run it in a web browser.

# The text that displays on the web page is the decoded password stored in HeidiSQL. Simple!
